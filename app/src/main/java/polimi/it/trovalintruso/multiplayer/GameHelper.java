package polimi.it.trovalintruso.multiplayer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;

import polimi.it.trovalintruso.App;
import polimi.it.trovalintruso.R;
import polimi.it.trovalintruso.ResultsActivity;
import polimi.it.trovalintruso.ScreenActivity;
import polimi.it.trovalintruso.model.Device;
import polimi.it.trovalintruso.model.Element;
import polimi.it.trovalintruso.model.Game;
import polimi.it.trovalintruso.model.GameMessage;
import polimi.it.trovalintruso.multiplayer.network.Base64Coder;
import polimi.it.trovalintruso.multiplayer.network.MultiPlayerConnectionHelper;
import polimi.it.trovalintruso.network.MultiPlayerDiscoveryActivity;

/**
 * Created by poool on 02/03/15.
 */
public class GameHelper {

    private MultiPlayerServiceHelper multiPlayerServiceHelper;
    private MultiPlayerConnectionHelper mConnection;
    private Context mContext;
    private ProgressDialog multiPlayerConnectionWaitDialog;
    private ProgressDialog multiPlayerInitializationWaitDialog;
    private Handler mHandler;
    private AlertDialog.Builder dialog;
    private boolean _connected;

    private ScreenActivity _currentScreenActivity;

    public GameHelper(Context context) {
        mContext = context;
        mHandler = new MultiPlayerHandler();
        _connected = false;
    }

    private void init() {
        multiPlayerServiceHelper = new MultiPlayerServiceHelper(mContext);
        mConnection = new MultiPlayerConnectionHelper(mHandler);
        multiPlayerServiceHelper.initializeNsd();
        if(mConnection.getLocalPort() > -1) {
            multiPlayerServiceHelper.registerService(mConnection.getLocalPort());
        } else {
            Log.d(multiPlayerServiceHelper.TAG, "ServerSocket isn't bound.");
        }
    }

    public void registerCurrentScreenActivity(ScreenActivity activity) {
        _currentScreenActivity = activity;
    }

    public void onActivityResume(Context context) {
        mContext = context;
    }

    public void onMainActivityCreate() {
        init();
        multiPlayerServiceHelper.discoverServices();
    }

    public void onMainActivityDestroy() {
        multiPlayerServiceHelper.tearDown();
        multiPlayerServiceHelper.stopDiscovery();
        mConnection.tearDown();
    }

    public void onAppTerminate() {
        //mConnection.tearDown();
    }

    public Handler getHandler() {
        return mHandler;
    }

    public void connectToClient(InetAddress address, int port) {
        mConnection.connectToServer(address, port);
        multiPlayerConnectionWaitDialog = ProgressDialog.show(mContext,
                mContext.getString(R.string.wait),
                mContext.getString(R.string.inviting_player), true);
        _connected = true;
    }

    public void removeServiceDiscoveryHandler() {
        if (multiPlayerServiceHelper != null) {
            multiPlayerServiceHelper.removeUpdateHandler();
        }
    }

    public void setServiceDiscoveryHandler(Handler handler) {
        if (multiPlayerServiceHelper != null) {
            multiPlayerServiceHelper.setUpdateHandler(handler);
        }
    }

    public ArrayList<Device> getDeviceList() {
        return multiPlayerServiceHelper.getDevices();
    }

    private class MultiPlayerHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            GameMessage message = (GameMessage) msg.getData().getSerializable("message");

            if (message.type == GameMessage.Type.ConnectionRequest) {
                dialog = new AlertDialog.Builder(mContext);
                dialog.setTitle("Richiesta di connessione");
                dialog.setCancelable(false);
                dialog.setMessage("Un dispositivo vuole giocare con te, accetti?");
                dialog.setIcon(android.R.drawable.ic_dialog_alert);
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mConnection.connectToClient();
                        _connected = true;
                        multiPlayerInitializationWaitDialog = ProgressDialog.show(mContext,
                                mContext.getString(R.string.wait),
                                mContext.getString(R.string.initializing_game), true);
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mConnection.disconnectClient();
                        _connected = false;
                    }
                });
                dialog.show();
            }

            if(message.type == GameMessage.Type.ConnectionClosed) {
                if(multiPlayerConnectionWaitDialog != null)
                    multiPlayerConnectionWaitDialog.dismiss();
                else {
                    dialog = new AlertDialog.Builder(mContext);
                    dialog.setTitle("Errore");
                    dialog.setCancelable(false);
                    dialog.setMessage("Connessione persa.");
                    dialog.setIcon(android.R.drawable.ic_dialog_alert);
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(mContext, ScreenActivity.class);
                            String pkg = mContext.getPackageName();
                            App.game.restart();
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            mContext.startActivity(intent);
                        }
                    });
                }
                mConnection.disconnectClient();
                _connected = false;
            }

            if(message.type == GameMessage.Type.ConnectionAccepted) {
                if(multiPlayerConnectionWaitDialog != null)
                    multiPlayerConnectionWaitDialog.dismiss();
                multiPlayerInitializationWaitDialog = ProgressDialog.show(mContext,
                        mContext.getString(R.string.wait),
                        mContext.getString(R.string.initializing_game), true);
                _connected = true;
                sendGameInfo();
            }

            if(message.type == GameMessage.Type.SendGame) {
                try {
                    App.game = (Game) fromString(message.msg);
                    App.game.initializeMultiplayerSession(true);
                    GameMessage ack = new GameMessage(GameMessage.Type.SendGameAck);
                    mConnection.sendMessage(ack);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                mConnection.sendMessage(new GameMessage(GameMessage.Type.SendGameAck));
                startMultiplayerSession();
            }

            if(message.type == GameMessage.Type.SendGameAck) {
                startMultiplayerSession();
            }

            if(message.type == GameMessage.Type.NextScreen) {
                moveToNextScreen();
            }

            if(message.type == GameMessage.Type.ElementPressed) {
                int index = Integer.valueOf(message.msg);
                _currentScreenActivity.elementFeedback(index);
            }
        }
    }

    private void sendGameInfo() {
        GameMessage message = new GameMessage(GameMessage.Type.SendGame);
        try {
            message.msg = toString(App.game);
            mConnection.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        App.game.initializeMultiplayerSession(false);
    }

    public void startMultiplayerSession() {
        Intent intent = new Intent(mContext, ScreenActivity.class);
        mContext.startActivity(intent);
    }

    public void startGame() {
        App.game.initialize();
        Intent intent;
        if(App.game.getSettings().get_singlePlayer())
            intent = new Intent(mContext, ScreenActivity.class);
        else
            intent = new Intent(mContext, MultiPlayerDiscoveryActivity.class);
        mContext.startActivity(intent);
    }

    public void nextScreen() {
        if (!App.game.getSettings().get_singlePlayer()) {
            GameMessage message = new GameMessage(GameMessage.Type.NextScreen);
            mConnection.sendMessage(message);
        }
        moveToNextScreen();
    }


    public void elementPressed(int index) {
        GameMessage message = new GameMessage(GameMessage.Type.ElementPressed);
        message.msg = String.valueOf(index);
        mConnection.sendMessage(message);
    }

    private void moveToNextScreen() {
        if(App.game.goToNextScreen()) {
            Intent intent = new Intent(mContext, ScreenActivity.class);
            mContext.startActivity(intent);
        }
        else {
            Intent intent = new Intent(mContext, ResultsActivity.class);
            mContext.startActivity(intent);
        }
    }

    //private game serialization methods

    private static String toString(Serializable o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        oos.close();
        return new String(Base64Coder.encode(baos.toByteArray()));
    }

    /** Read the object from Base64 string. */
    private static Object fromString(String s) throws IOException, ClassNotFoundException {
        byte [] data = Base64Coder.decode(s);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        Object o  = ois.readObject();
        ois.close();
        return o;
    }
}

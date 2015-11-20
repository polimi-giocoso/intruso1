package polimi.it.trovalintruso.helpers;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.joda.time.DateTime;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;

import polimi.it.trovalintruso.App;
import polimi.it.trovalintruso.MainActivity;
import polimi.it.trovalintruso.R;
import polimi.it.trovalintruso.ResultsActivity;
import polimi.it.trovalintruso.ScreenActivity;
import polimi.it.trovalintruso.model.Device;
import polimi.it.trovalintruso.model.Game;
import polimi.it.trovalintruso.model.GameMessage;
import polimi.it.trovalintruso.model.Settings;
import polimi.it.trovalintruso.multiplayer.MultiPlayerServiceHelper;
import polimi.it.trovalintruso.multiplayer.network.Base64Coder;
import polimi.it.trovalintruso.multiplayer.network.ConnectionHelper;
import polimi.it.trovalintruso.network.MultiPlayerDiscoveryActivity;

/**
 * Created by poool on 02/03/15.
 */
public class GameHelper {

    private MultiPlayerServiceHelper multiPlayerServiceHelper;
    private ConnectionHelper mConnection;
    private Context mContext;
    private ProgressDialog multiPlayerConnectionWaitDialog;
    private ProgressDialog multiPlayerInitializationWaitDialog;
    private Handler mHandler;
    private AlertDialog.Builder dialog;
    private Activity _currentActivity;
    private String deviceName;
    private boolean isServer;

    private ImageCacheHelper mCacheHelper;

    public GameHelper(Context context) {
        mCacheHelper = new ImageCacheHelper();
        mContext = context;
        mHandler = new MultiPlayerHandler();
    }

    private void init() {
        SharedPreferences sharedPref = mContext.getApplicationContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        deviceName = sharedPref.getString("dev_name", "");
        multiPlayerServiceHelper = new MultiPlayerServiceHelper(mContext);
        mConnection = new ConnectionHelper(mHandler);
        multiPlayerServiceHelper.initializeNsd();
        if(mConnection.getLocalPort() > -1) {
            multiPlayerServiceHelper.registerService(mConnection.getLocalPort());
        } else {
            Log.d(multiPlayerServiceHelper.TAG, "ServerSocket isn't bound.");
        }
    }

    public ImageCacheHelper getImageCacheHelper() {
        return mCacheHelper;
    }

    public boolean isServer() {
        return isServer;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void registerCurrentActivity(Activity activity) {
        _currentActivity = activity;
    }

    public void onActivityResume(Context context) {
        mContext = context;
    }

    public void onMainActivityCreate() {
        init();
        multiPlayerServiceHelper.discoverServices();
        SharedPreferences sharedPref = mContext.getApplicationContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        String s = sharedPref.getString("game_settings", null);
        if(s != null) {
            try {
                App.gameSettings = (Settings) fromString(s);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void onMainActivityDestroy() {
        multiPlayerServiceHelper.tearDown();
        multiPlayerServiceHelper.stopDiscovery();
        mConnection.tearDown();
    }

    public void restartDiscovery() {
        App.gameHelper.onMainActivityDestroy();
        App.gameHelper.onMainActivityCreate();
    }

    public void onAppTerminate() {
    }

    public void connectToClient(InetAddress address, int port) {
        mConnection.connectToServer(address, port);
        multiPlayerConnectionWaitDialog = ProgressDialog.show(mContext,
                mContext.getString(R.string.wait),
                mContext.getString(R.string.inviting_player), true);
        isServer = true;
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
                        isServer = false;
                        multiPlayerInitializationWaitDialog = ProgressDialog.show(mContext,
                                mContext.getString(R.string.wait),
                                mContext.getString(R.string.initializing_game), true);
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mConnection.disconnectClient();
                        //_connected = false;
                    }
                });
                dialog.show();
            }

            if(message.type == GameMessage.Type.ConnectionClosed) {
                ActivityManager am = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
                ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
                if(multiPlayerConnectionWaitDialog != null && multiPlayerConnectionWaitDialog.isShowing())
                    multiPlayerConnectionWaitDialog.dismiss();
                else if(_currentActivity instanceof ScreenActivity) {
                    dialog = new AlertDialog.Builder(mContext);
                    dialog.setTitle("Errore");
                    dialog.setCancelable(false);
                    dialog.setMessage("Connessione persa.");
                    dialog.setIcon(android.R.drawable.ic_dialog_alert);
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(mContext, MainActivity.class);
                            App.game.restart();
                            mContext.startActivity(intent);
                        }
                    });
                    dialog.show();
                }
                else if(_currentActivity instanceof ResultsActivity) {
                    Intent intent = new Intent(mContext, MainActivity.class);
                    String pkg = mContext.getPackageName();
                    App.game.restart();
                    mContext.startActivity(intent);
                }
                mConnection.disconnectClient();
                //_connected = false;
            }

            if(message.type == GameMessage.Type.ConnectionAccepted) {
                if(multiPlayerConnectionWaitDialog != null)
                    multiPlayerConnectionWaitDialog.dismiss();
                multiPlayerInitializationWaitDialog = ProgressDialog.show(mContext,
                        mContext.getString(R.string.wait),
                        mContext.getString(R.string.initializing_game), true);
                //_connected = true;
                sendGameInfo();
            }

            if(message.type == GameMessage.Type.SendGame) {
                //try {
                    Game g = (Game) message.msg; //fromString(message.msg);
                    App.game = g;
                    App.game.restart();
                    App.game.initializeMultiplayerSession(true);
                    GameMessage ack = new GameMessage(GameMessage.Type.SendGameAck);
                    mConnection.sendMessage(ack);
                /*} catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }*/
                mConnection.sendMessage(new GameMessage(GameMessage.Type.SendGameAck));
                if(multiPlayerInitializationWaitDialog != null && multiPlayerInitializationWaitDialog.isShowing())
                    multiPlayerInitializationWaitDialog.dismiss();
                startGameSession();
            }

            if(message.type == GameMessage.Type.SendGameAck) {
                if(multiPlayerInitializationWaitDialog != null && multiPlayerInitializationWaitDialog.isShowing())
                    multiPlayerInitializationWaitDialog.dismiss();
                startGameSession();
            }

            if(message.type == GameMessage.Type.NextScreen) {
                DateTime[] times = (DateTime[]) message.msg;
                App.game.getActiveScreen().setStart(times[0]);
                App.game.getActiveScreen().setEnd(times[1]);
                moveToNextScreen();
            }

            if(message.type == GameMessage.Type.ElementPressed && _currentActivity instanceof ScreenActivity) {
                int index = (int) message.msg;
                ((ScreenActivity)_currentActivity).elementFeedback(index);
            }
        }
    }

    private void sendGameInfo() {
        GameMessage message = new GameMessage(GameMessage.Type.SendGame);
        //try {
            message.msg = App.game;//toString(App.game);
            mConnection.sendMessage(message);
        /*} catch (IOException e) {
            e.printStackTrace();
        }*/
        App.game.initializeMultiplayerSession(false);
    }

    public void startGameSession() {
        Intent intent = new Intent(mContext, ScreenActivity.class);
        mContext.startActivity(intent);
    }

    public void startGame() {
        App.game.initialize();
        /*try {
            String s = toString(App.gameSettings);
            if(s != null) {
                SharedPreferences sharedPref = mContext.getApplicationContext()
                        .getSharedPreferences("settings", Context.MODE_PRIVATE);
                sharedPref.edit().putString("game_settings", s).commit();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        Intent intent;
        if(App.game.getSettings().singlePlayer())
            intent = new Intent(mContext, ScreenActivity.class);
        else
            intent = new Intent(mContext, MultiPlayerDiscoveryActivity.class);
        mContext.startActivity(intent);
    }

    public void restartGame() {
        App.game.restart();
        if(!App.game.getSettings().singlePlayer())
            sendGameInfo();
        else
            startGameSession();
    }

    public void quitGame() {
        App.game.restart();
        if(!App.game.getSettings().singlePlayer()) {
            mConnection.disconnectClient();
        }
        Intent intent = new Intent(mContext, MainActivity.class);
        mContext.startActivity(intent);
    }

    public void nextScreen() {
        if (!App.game.getSettings().singlePlayer()) {
            GameMessage message = new GameMessage(GameMessage.Type.NextScreen);
            DateTime[] times = new DateTime[2];
            times[0] = App.game.getActiveScreen().getStart();
            times[1] = App.game.getActiveScreen().getEnd();
            message.msg = times;
            mConnection.sendMessage(message);
        }
        moveToNextScreen();
    }


    public void elementPressed(int index) {
        GameMessage message = new GameMessage(GameMessage.Type.ElementPressed);
        message.msg = index;//String.valueOf(index);
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

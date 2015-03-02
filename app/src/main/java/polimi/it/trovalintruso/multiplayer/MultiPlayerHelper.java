package polimi.it.trovalintruso.multiplayer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.net.InetAddress;
import java.util.ArrayList;

import polimi.it.trovalintruso.App;
import polimi.it.trovalintruso.R;
import polimi.it.trovalintruso.ScreenActivity;
import polimi.it.trovalintruso.model.Device;
import polimi.it.trovalintruso.model.GameMessage;
import polimi.it.trovalintruso.multiplayer.network.MultiPlayerConnectionHelper;

/**
 * Created by poool on 02/03/15.
 */
public class MultiPlayerHelper {

    private final MultiPlayerServiceHelper multiPlayerServiceHelper;
    private final MultiPlayerConnectionHelper mConnection;
    private Context mContext;
    private ProgressDialog multiPlayerConnectionWaitDialog;
    private Handler mHandler;


    public MultiPlayerHelper(Context context) {
        mHandler = new MultiPlayerHandler();
        multiPlayerServiceHelper = new MultiPlayerServiceHelper(context);
        multiPlayerServiceHelper.setUpdateHandler(mHandler);
        mConnection = new MultiPlayerConnectionHelper(App.multiPlayerHelper.getHandler());
    }

    private void init() {
        multiPlayerServiceHelper.initializeNsd();
        if(mConnection.getLocalPort() > -1) {
            multiPlayerServiceHelper.registerService(mConnection.getLocalPort());
        } else {
            Log.d(multiPlayerServiceHelper.TAG, "ServerSocket isn't bound.");
        }
    }

    public void onActivityResume(Context context) {
        mContext = context;
    }

    public void onMainActivityCreate() {
        init();
        multiPlayerServiceHelper.discoverServices();
    }

    public void onMainActivityDestroy() {
        multiPlayerServiceHelper.stopDiscovery();
        multiPlayerServiceHelper.tearDown();
    }

    public void onAppTerminate() {
        mConnection.tearDown();
    }

    public Handler getHandler() {
        return mHandler;
    }

    public void connectToClient(InetAddress address, int port) {
        mConnection.connectToServer(address, port);
        multiPlayerConnectionWaitDialog = ProgressDialog.show(mContext,
                mContext.getString(R.string.wait),
                mContext.getString(R.string.inviting_player), true);
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
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setTitle("Richiesta di connessione");
                dialog.setCancelable(false);
                dialog.setMessage("Un dispositivo vuole giocare con te, accetti?");
                dialog.setIcon(android.R.drawable.ic_dialog_alert);
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mConnection.connectToClient();
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mConnection.disconnectClient();
                    }
                });
                dialog.show();
            }

            if(message.type == GameMessage.Type.ConnectionAccepted) {
                if(multiPlayerConnectionWaitDialog != null)
                    multiPlayerConnectionWaitDialog.dismiss();
                Intent intent = new Intent(mContext, ScreenActivity.class);
                mContext.startActivity(intent);
            }

            if (message.type == GameMessage.Type.ConnectionRequest) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setTitle("Richiesta di connessione");
                dialog.setCancelable(false);
                dialog.setMessage("Un dispositivo vuole giocare con te, accetti?");
                dialog.setIcon(android.R.drawable.ic_dialog_alert);
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mConnection.connectToClient();
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mConnection.disconnectClient();
                    }
                });
                dialog.show();
            }
        }
    }

}

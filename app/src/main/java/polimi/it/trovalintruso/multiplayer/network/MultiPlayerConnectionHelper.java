package polimi.it.trovalintruso.multiplayer.network;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import polimi.it.trovalintruso.model.GameMessage;

/**
 * Created by Paolo on 01/03/2015.
 */
public class MultiPlayerConnectionHelper {

    protected Handler mUpdateHandler;
    protected MultiPlayerServer mMultiPlayerServer;
    protected MultiPlayerClient mMultiPlayerClient;

    private static final String TAG = "MultiPlayerConnection";

    private Socket mSocket;
    private int mPort = -1;

    public void setmUpdateHandler(Handler handler) {
        mUpdateHandler = handler;
    }

    public MultiPlayerConnectionHelper(Handler handler) {
        mUpdateHandler = handler;
        mMultiPlayerServer = new MultiPlayerServer(handler, this);
    }

    public void tearDown() {
        if(mMultiPlayerServer != null)
            mMultiPlayerServer.tearDown();
        if(mMultiPlayerClient != null)
            mMultiPlayerClient.tearDown();
    }

    public void disconnectClient() {
        if(mMultiPlayerClient != null) {
            mMultiPlayerClient.tearDown();
            mMultiPlayerClient = null;
        }
        else {
            try {
                getSocket().close();
            } catch (IOException ioe) {
                Log.e("", "Error when closing server socket.");
            }
        }
    }

    public void connectToServer(InetAddress address, int port) {
        mMultiPlayerClient = new MultiPlayerClient(address, port, this);
    }

    public void connectToClient() {
        int port = mSocket.getPort();
        InetAddress address = mSocket.getInetAddress();
        connectToServer(address, port);
    }

    public void sendMessage(GameMessage msg) {
        if (mMultiPlayerClient != null) {
            mMultiPlayerClient.sendMessage(msg);
        }
    }

    public int getLocalPort() {
        return mPort;
    }

    protected void setLocalPort(int port) {
        mPort = port;
    }

    protected synchronized void setSocket(Socket socket) {
        Log.d(TAG, "setSocket being called.");
        if (socket == null) {
            Log.d(TAG, "Setting a null socket.");
        }
        if (mSocket != null) {
            if (mSocket.isConnected()) {
                try {
                    mSocket.close();
                } catch (IOException e) {
                    // TODO(alexlucas): Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        mSocket = socket;
    }

    protected Socket getSocket() {
        return mSocket;
    }

    protected void connectionRequest() {
        Bundle messageBundle = new Bundle();
        messageBundle.putSerializable("message", new GameMessage(GameMessage.Type.ConnectionRequest));
        Message msg = new Message();
        msg.setData(messageBundle);
        mUpdateHandler.sendMessage(msg);
    }

    protected void connectionAccepted() {
        Bundle messageBundle = new Bundle();
        messageBundle.putSerializable("message", new GameMessage(GameMessage.Type.ConnectionAccepted));
        Message msg = new Message();
        msg.setData(messageBundle);
        mUpdateHandler.sendMessage(msg);
    }
}

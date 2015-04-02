package polimi.it.trovalintruso.multiplayer.network;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by poool on 09/03/15.
 */
public class ServerHelper {

    private static final String TAG = "MultiPlayerConnection";

    ServerSocket mServerSocket = null;
    Thread mThread = null;
    ConnectionHelper mConnection;

    public ServerHelper(ConnectionHelper connection) {
        mConnection = connection;
        try {
            mServerSocket = new ServerSocket(0);
            mConnection.setLocalPort(mServerSocket.getLocalPort());
        } catch (IOException e) {
            Log.e(TAG, "Error creating ServerSocket: ", e);
            e.printStackTrace();
        }
        mThread = new Thread(new ServerThread());
        mThread.start();
    }

    public void tearDown() {
        mThread.interrupt();
        try {
            mServerSocket.close();
        } catch (IOException ioe) {
            Log.e(TAG, "Error when closing server socket.");
        }
    }

    class ServerThread implements Runnable {

        @Override
        public void run() {

            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Log.d(TAG, "ServerSocket Created, awaiting connection");
                    mConnection.setSocket(mServerSocket.accept());
                    Log.d(TAG, "Connected.");
                    if (mConnection.mMultiPlayerClient == null) {
                        mConnection.connectionRequest();
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "Error creating ServerSocket: ", e);
                e.printStackTrace();
            }
        }
    }
}

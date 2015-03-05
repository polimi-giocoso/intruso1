package polimi.it.trovalintruso.multiplayer.network;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by poool on 02/03/15.
 */
class MultiPlayerServer {

    private static final String TAG = "MultiPlayerConnection";

    ServerSocket mServerSocket = null;
    Thread mThread = null;
    MultiPlayerConnectionHelper mConnection;

    public MultiPlayerServer(Handler handler, MultiPlayerConnectionHelper connection) {
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
                // Since discovery will happen via Nsd, we don't need to care which port is
                // used.  Just grab an available one  and advertise it via Nsd.
                //mServerSocket = new ServerSocket(0);
                //mConnection.setLocalPort(mServerSocket.getLocalPort());

                while (!Thread.currentThread().isInterrupted()) {
                    Log.d(TAG, "ServerSocket Created, awaiting connection");
                    mConnection.setSocket(mServerSocket.accept());
                    Log.d(TAG, "Connected.");
                    if (mConnection.mMultiPlayerClient == null) {
                        mConnection.connectionRequest();
                            /*int port = mSocket.getPort();
                            InetAddress address = mSocket.getInetAddress();
                            connectToServer(address, port);*/
                    }
                    else {
                        mConnection.connectionAccepted();
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "Error creating ServerSocket: ", e);
                e.printStackTrace();
            }
        }
    }
}

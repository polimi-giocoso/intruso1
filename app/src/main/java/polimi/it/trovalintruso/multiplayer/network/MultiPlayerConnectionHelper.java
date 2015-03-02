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
        if(mMultiPlayerClient != null)
            mMultiPlayerClient.tearDown();
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

    /*public synchronized void updateMessages(String msg, boolean local) {
        Log.e(TAG, "Updating message: " + msg);

        if (local) {
            msg = "me: " + msg;
        } else {
            msg = "them: " + msg;
        }

        Bundle messageBundle = new Bundle();
        messageBundle.putString("msg", msg);

        Message message = new Message();
        message.setData(messageBundle);
        mUpdateHandler.sendMessage(message);

    }*/

    /*private class MultiPlayerServer {
        ServerSocket mServerSocket = null;
        Thread mThread = null;

        public MultiPlayerServer(Handler handler) {
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
                    mServerSocket = new ServerSocket(0);
                    setLocalPort(mServerSocket.getLocalPort());

                    while (!Thread.currentThread().isInterrupted()) {
                        Log.d(TAG, "ServerSocket Created, awaiting connection");
                        setSocket(mServerSocket.accept());
                        Log.d(TAG, "Connected.");
                        if (mMultiPlayerClient == null) {
                            connectionRequest();
                            //int port = mSocket.getPort();
                            //InetAddress address = mSocket.getInetAddress();
                            //connectToServer(address, port);
                        }
                        else {
                            connectionAccepted();
                        }
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error creating ServerSocket: ", e);
                    e.printStackTrace();
                }
            }
        }
    }*/

    /*private class MultiPlayerClient {

        private InetAddress mAddress;
        private int PORT;

        private final String CLIENT_TAG = "MultiPlayerClient";

        private Thread mSendThread;
        private Thread mRecThread;

        public MultiPlayerClient(InetAddress address, int port) {

            Log.d(CLIENT_TAG, "Creating multiPlayerClient");
            this.mAddress = address;
            this.PORT = port;

            mSendThread = new Thread(new SendingThread());
            mSendThread.start();
        }

        class SendingThread implements Runnable {

            BlockingQueue<GameMessage> mMessageQueue;
            private int QUEUE_CAPACITY = 10;

            public SendingThread() {
                mMessageQueue = new ArrayBlockingQueue<GameMessage>(QUEUE_CAPACITY);
            }

            @Override
            public void run() {
                try {
                    if (getSocket() == null) {
                        setSocket(new Socket(mAddress, PORT));
                        Log.d(CLIENT_TAG, "Client-side socket initialized.");

                    } else {
                        Log.d(CLIENT_TAG, "Socket already initialized. skipping!");
                    }

                    mRecThread = new Thread(new ReceivingThread());
                    mRecThread.start();

                } catch (UnknownHostException e) {
                    Log.d(CLIENT_TAG, "Initializing socket failed, UHE", e);
                } catch (IOException e) {
                    Log.d(CLIENT_TAG, "Initializing socket failed, IOE.", e);
                }

                while (true) {
                    try {
                        GameMessage msg = mMessageQueue.take();
                        sendMessage(msg);
                    } catch (InterruptedException ie) {
                        Log.d(CLIENT_TAG, "Message sending loop interrupted, exiting");
                    }
                }
            }
        }

        class ReceivingThread implements Runnable {

            @Override
            public void run() {

                ObjectInputStream input;
                try {
                    //input = new BufferedReader(new InputStreamReader(
                            //mSocket.getInputStream()));
                    input = new ObjectInputStream(mSocket.getInputStream());
                    while (!Thread.currentThread().isInterrupted()) {

                        /*String messageStr = null;
                        messageStr = input.readLine();
                        if (messageStr != null) {
                            Log.d(CLIENT_TAG, "Read from the stream: " + messageStr);
                            updateMessages(messageStr, false);
                        } else {
                            Log.d(CLIENT_TAG, "The nulls! The nulls!");
                            break;
                        }
                        GameMessage message = null;
                        try {
                            message = (GameMessage)input.readObject();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                            message = null;
                        }
                        if(message != null) {
                            Log.i(CLIENT_TAG, "message received: " + message.type.toString());
                            Message mex = new Message();
                            Bundle messageBundle = new Bundle();
                            messageBundle.putSerializable("message", message);
                            Message msg = new Message();
                            msg.setData(messageBundle);
                            mUpdateHandler.sendMessage(mex);
                        }
                    }
                    input.close();

                } catch (IOException e) {
                    Log.e(CLIENT_TAG, "Server loop error: ", e);
                }
            }
        }

        public void tearDown() {
            try {
                getSocket().close();
            } catch (IOException ioe) {
                Log.e(CLIENT_TAG, "Error when closing server socket.");
            }
        }

        public boolean sendMessage(GameMessage msg) {
            try {
                Socket socket = getSocket();
                if (socket == null) {
                    Log.d(CLIENT_TAG, "Socket is null, wtf?");
                    return false;
                } else if (socket.getOutputStream() == null) {
                    Log.d(CLIENT_TAG, "Socket output stream is null, wtf?");
                    return false;
                }
                ObjectOutputStream out = new ObjectOutputStream(getSocket().getOutputStream());
                out.writeObject(msg);
                out.flush();
                /*PrintWriter out = new PrintWriter(
                        new BufferedWriter(
                                new OutputStreamWriter(getSocket().getOutputStream())), true);
                out.println(msg);
                out.flush();
                updateMessages(msg, true);
            } catch (UnknownHostException e) {
                Log.d(CLIENT_TAG, "Unknown Host", e);
            } catch (IOException e) {
                Log.d(CLIENT_TAG, "I/O Exception", e);
            } catch (Exception e) {
                Log.d(CLIENT_TAG, "Error3", e);
            }
            Log.d(CLIENT_TAG, "Client sent message: " + msg);
            return true;
        }
    }*/
}

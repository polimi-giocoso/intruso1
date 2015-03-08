package polimi.it.trovalintruso.multiplayer.network;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import polimi.it.trovalintruso.model.Category;
import polimi.it.trovalintruso.model.GameMessage;

/**
 * Created by poool on 02/03/15.
 */
class MultiPlayerClient {
    private InetAddress mAddress;
    private int PORT;

    private final String CLIENT_TAG = "MultiPlayerClient";

    private Thread mSendThread;
    private Thread mRecThread;
    private MultiPlayerConnectionHelper mConnection;
    private ObjectOutputStream out;

    public MultiPlayerClient(InetAddress address, int port, MultiPlayerConnectionHelper connection) {
        mConnection = connection;
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
                if (mConnection.getSocket() == null) {
                    mConnection.setSocket(new Socket(mAddress, PORT));
                    Log.d(CLIENT_TAG, "Client-side socket initialized.");
                    out = new ObjectOutputStream(mConnection.getSocket().getOutputStream());

                } else {
                    out = new ObjectOutputStream(mConnection.getSocket().getOutputStream());
                    sendMessage(new GameMessage(GameMessage.Type.ConnectionAccepted));
                    Log.d(CLIENT_TAG, "Socket already initialized. skipping!");
                }
                mRecThread = new Thread(new ReceivingThread());
                mRecThread.start();

            } catch (UnknownHostException e) {
                Log.d(CLIENT_TAG, "Initializing socket failed, UHE", e);
            } catch (IOException e) {
                Log.d(CLIENT_TAG, "Initializing socket failed, IOE.", e);
                GameMessage message = new GameMessage(GameMessage.Type.ConnectionClosed);
                Bundle messageBundle = new Bundle();
                messageBundle.putSerializable("message", message);
                Message msg = new Message();
                msg.setData(messageBundle);
                mConnection.mUpdateHandler.sendMessage(msg);
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
                input = new ObjectInputStream(mConnection.getSocket().getInputStream());
                while (!Thread.currentThread().isInterrupted()) {
                    GameMessage message = null;
                    try {
                        message = (GameMessage)input.readObject();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        message = null;
                    }
                    if(message != null) {
                        Log.i(CLIENT_TAG, "message received: " + message.type.toString());
                        Bundle messageBundle = new Bundle();
                        messageBundle.putSerializable("message", message);
                        Message msg = new Message();
                        msg.setData(messageBundle);
                        mConnection.mUpdateHandler.sendMessage(msg);
                    }
                }
                input.close();

            } catch (IOException e) {
                Log.e(CLIENT_TAG, "Server loop error: ", e);
                GameMessage message = new GameMessage(GameMessage.Type.ConnectionClosed);
                Bundle messageBundle = new Bundle();
                messageBundle.putSerializable("message", message);
                Message msg = new Message();
                msg.setData(messageBundle);
                mConnection.mUpdateHandler.sendMessage(msg);
            }
        }
    }

    /*class IsAliveThread implements Runnable {

        @Override
        public void run() {

            BufferedInputStream input;
            try {
                input = new BufferedInputStream(mConnection.getSocket().getInputStream());
                while (!Thread.currentThread().isInterrupted()) {
                    GameMessage message = new GameMessage(GameMessage.Type.Ping);
                    ObjectOutputStream out = new ObjectOutputStream(mConnection.getSocket().getOutputStream());
                    out.writeObject(message);
                    out.flush();
                }
                input.close();
                wait(1000);

            } catch (Exception e) {
                Log.e(CLIENT_TAG, "Server loop error: ", e);
                GameMessage message = new GameMessage(GameMessage.Type.ConnectionClosed);
                Bundle messageBundle = new Bundle();
                messageBundle.putSerializable("message", message);
                Message msg = new Message();
                msg.setData(messageBundle);
                mConnection.mUpdateHandler.sendMessage(msg);
            }
        }
    }*/

    public void tearDown() {
        try {
            if(mConnection.getSocket() != null)
                mConnection.getSocket().close();
            mRecThread.interrupt();
            mSendThread.interrupt();
            //mAliveThread.interrupt();
        } catch (IOException ioe) {
            Log.e(CLIENT_TAG, "Error when closing server socket.");
        }
    }

    public boolean sendMessage(GameMessage msg) {
        try {
            Socket socket = mConnection.getSocket();
            if (socket == null) {
                Log.d(CLIENT_TAG, "Socket is null, wtf?");
                return false;
            } else if (socket.getOutputStream() == null) {
                Log.d(CLIENT_TAG, "Socket output stream is null, wtf?");
                return false;
            }
            out.writeObject(msg);
            out.flush();
                /*PrintWriter out = new PrintWriter(
                        new BufferedWriter(
                                new OutputStreamWriter(getSocket().getOutputStream())), true);
                out.println(msg);
                out.flush();
                updateMessages(msg, true);*/
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
}

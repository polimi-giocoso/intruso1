package polimi.it.trovalintruso.model;

import java.io.Serializable;

/**
 * Created by poool on 02/03/15.
 */
public class GameMessage implements Serializable {

    private final static long serialVersionUID = 1L;

    public enum Type {
        ConnectionRequest,
        ConnectionAccepted,
        ConnectionClosed,
        //Ping,
        SendGame,
        SendGameAck,
        StartGame,
        StartGameAck,
        TargetPressed,
        ElementPressed,
        NextScreen
    }

    public String msg;
    public Type type;

    public GameMessage(Type _type) {
        msg = "";
        type = _type;
    }
}

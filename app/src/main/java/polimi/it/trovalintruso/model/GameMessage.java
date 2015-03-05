package polimi.it.trovalintruso.model;

import java.io.Serializable;

/**
 * Created by poool on 02/03/15.
 */
public class GameMessage implements Serializable {

    public enum Type {
        ConnectionRequest,
        ConnectionAccepted,
        ConnectionClosed,
        Ping,
        SendGame,
        SendGameAck,
        ObjectPressed
    }

    public String msg;
    public Type type;

    public GameMessage(Type _type) {
        msg = "";
        type = _type;
    }
}

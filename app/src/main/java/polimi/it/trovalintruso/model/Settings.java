package polimi.it.trovalintruso.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by poool on 09/02/15.
 */
public class Settings implements Serializable {

    public static enum ObjectsForPage { Four, Five, Six }

    public static enum Category { Casuale, Colori, Forme}

    private ObjectsForPage _numOfObjects;
    private int _numOfScreens;
    private Boolean _timeLimitEnabled;
    private Category _category;

    private Boolean _singlePlayer;

    public Settings() {
        _numOfObjects = ObjectsForPage.Four;
        _numOfScreens = 2;
        _timeLimitEnabled = false;
        _category = Category.Casuale;
    }

    public ObjectsForPage getNumOfObjects() {
        return _numOfObjects;
    }

    public void setNumOfObjects(ObjectsForPage numOfObjects) {
        this._numOfObjects = numOfObjects;
    }

    public Boolean getTimeLimitEnabled() {
        return _timeLimitEnabled;
    }

    public void setTimeLimitEnabled(Boolean timeLimit) {
        this._timeLimitEnabled = timeLimit;
    }

    public int getNumOfScreens() {
        return _numOfScreens;
    }

    public void setNumOfScreens(int numOfScreens) {
        this._numOfScreens = numOfScreens;
    }

    public Category getCategory() {
        return _category;
    }

    public void setCategory(Category category) {
        this._category = category;
    }

    public Boolean get_singlePlayer() {
        return _singlePlayer;
    }

    public void set_singlePlayer(Boolean _singlePlayer) {
        this._singlePlayer = _singlePlayer;
    }

}
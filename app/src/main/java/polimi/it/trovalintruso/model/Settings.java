package polimi.it.trovalintruso.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by poool on 09/02/15.
 */
public class Settings implements Parcelable {

    //public static enum ObjectsForPage { Four, Five, Six }

    public static enum Category { Casuale, Colori, Forme}

    private int _numOfObjects;
    private int _numOfScreens;
    private Boolean _timeLimitEnabled;
    private int _timeLimit;
    //private Category _category;
    private Boolean _singlePlayer;

    public Settings() {
        _numOfObjects = 4;
        _numOfScreens = 2;
        _timeLimitEnabled = false;
        //_category = Category.Casuale;
    }

    public int getNumOfObjects() {
        return _numOfObjects;
    }

    public void setNumOfObjects(int numOfObjects) {
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

    public int get_timeLimit() {
        return _timeLimit;
    }

    public void set_timeLimit(int _timeLimit) {
        this._timeLimit = _timeLimit;
    }

    public Boolean get_singlePlayer() {
        return _singlePlayer;
    }

    public void set_singlePlayer(Boolean singlePlayer) {
        this._singlePlayer = singlePlayer;
    }

    //Parcelable implementation

    public int describeContents() {
        return 0;
    }

    public Settings(Parcel in ) {
        readFromParcel( in );
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Settings createFromParcel(Parcel in ) {
            return new Settings( in );
        }

        public Settings[] newArray(int size) {
            return new Settings[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(_numOfScreens);
        dest.writeInt(_numOfObjects);
        dest.writeByte((byte) (_timeLimitEnabled ? 1 : 0));
        dest.writeByte((byte) (_singlePlayer ? 1 : 0));
    }

    private void readFromParcel(Parcel in ) {
        _numOfScreens = in.readInt();
        _numOfObjects = in.readInt();
        _timeLimitEnabled = in.readByte() != 0;
        _singlePlayer = in.readByte() != 0;
    }

}
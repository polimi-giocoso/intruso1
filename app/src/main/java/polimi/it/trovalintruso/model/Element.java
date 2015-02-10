package polimi.it.trovalintruso.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by poool on 09/02/15.
 */
public class Element implements Parcelable {

    private String _drawableIdentifier;
    private Boolean _isTarget;

    public Element() {
        _isTarget = false;
    }

    //getters & setters

    public String get_drawable_name() {
        return _drawableIdentifier;
    }

    public void set_drawable_name(String _file) {
        this._drawableIdentifier = _file;
    }

    public Boolean get_isTarget() {
        return _isTarget;
    }

    public void set_isTarget(Boolean _isTarget) {
        this._isTarget = _isTarget;
    }

    //Parcelable implementation

    public int describeContents() {
        return 0;
    }

    public Element(Parcel in ) {
        readFromParcel( in );
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Element createFromParcel(Parcel in ) {
            return new Element( in );
        }

        public Element[] newArray(int size) {
            return new Element[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_drawableIdentifier);
        dest.writeByte((byte) (_isTarget ? 1 : 0));
    }

    private void readFromParcel(Parcel in ) {
        _drawableIdentifier = in.readString();
        _isTarget = in.readByte() != 0;
    }

}

package polimi.it.trovalintruso.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by poool on 09/02/15.
 */
public class Element implements Parcelable {

    private String _drawable_identifier;
    private Boolean _is_target;

    public Element() {
        _is_target = false;
    }

    //getters & setters

    public String get_drawable_name() {
        return _drawable_identifier;
    }

    public void set_drawable_name(String _file) {
        this._drawable_identifier = _file;
    }

    public Boolean get_is_target() {
        return _is_target;
    }

    public void set_is_target(Boolean _is_target) {
        this._is_target = _is_target;
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
        dest.writeString(_drawable_identifier);
        dest.writeByte((byte) (_is_target ? 1 : 0));
    }

    private void readFromParcel(Parcel in ) {
        _drawable_identifier = in.readString();
        _is_target = in.readByte() != 0;
    }

}

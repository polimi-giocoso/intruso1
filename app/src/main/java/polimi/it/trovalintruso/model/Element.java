package polimi.it.trovalintruso.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by poool on 09/02/15.
 */
public class Element implements Parcelable {

    private String _file;
    private Boolean _is_target;

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
        dest.writeString(_file);
        dest.writeByte((byte) (_is_target ? 1 : 0));
    }

    private void readFromParcel(Parcel in ) {
        _file = in.readString();
        _is_target = in.readByte() != 0;
    }

}

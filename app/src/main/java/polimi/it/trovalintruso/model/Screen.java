package polimi.it.trovalintruso.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by poool on 09/02/15.
 */
public class Screen implements Parcelable {

    private ArrayList<Element> _elements;
    private DateTime _start;
    private DateTime _end;
    private int _incorrect_attempts;

    public Screen() {
        _incorrect_attempts = 0;
    }

    public void initialize(ArrayList<Element> objects) {
        _elements = objects;
    }

    public void start() {
        _start = new DateTime();
    }

    public void error() {
        _incorrect_attempts++;
    }

    public void completed() {
        _end = new DateTime();
    }

    public Interval getScreenTime() {
        return new Interval(_start.getMillis(), _end.getMillis());
    }

    //parcelable implementation

    public int describeContents() {
        return 0;
    }

    public Screen(Parcel in ) {
        readFromParcel( in );
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Screen createFromParcel(Parcel in ) {
            return new Screen( in );
        }

        public Screen[] newArray(int size) {
            return new Screen[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(_start.getMillis());
        dest.writeLong(_end.getMillis());
        dest.writeInt(_incorrect_attempts);
        dest.writeList(_elements);
    }

    private void readFromParcel(Parcel in ) {
        _start = new DateTime(in.readLong());
        _end = new DateTime(in.readLong());
        _incorrect_attempts = in.readInt();
        _elements = in.readArrayList(Element.class.getClassLoader());
    }
}

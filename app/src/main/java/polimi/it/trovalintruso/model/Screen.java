package polimi.it.trovalintruso.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

/**
 * Created by poool on 09/02/15.
 */
public class Screen implements Parcelable {

    private ArrayList<Element> _elements;
    private DateTime _start;
    private DateTime _end;
    private int _incorrectAttempts;

    //getters & setters

    public ArrayList<Element> get_elements() {
        return _elements;
    }

    public void set_elements(ArrayList<Element> _elements) {
        this._elements = _elements;
    }

    public int getErrors() {
        return _incorrectAttempts;
    }

    public Screen() {
        _incorrectAttempts = 0;
    }

    //game methods

    /*public void initialize(ArrayList<Element> objects) {
        _elements = objects;
        initialize();
    }*/

    public void initialize(CategoryGroup group, int numOfObjects) {
        _elements = new ArrayList<Element>();
        _elements.add(new Element(true, group.getTarget()));
        ArrayList<String> others = new ArrayList<String>(Arrays.asList(group.getOthers()));
        Collections.shuffle(others, new Random());
        for(int i = 0; i < numOfObjects - 1; i++) {
            _elements.add(new Element(false, others.get(i)));
        }
        /*for(String s : group.getOthers()) {
            _elements.add(new Element(false, s));
        }*/
        Collections.shuffle(_elements, new Random());
        initialize();
    }

    public void initialize() {
        _start = null;
        _end = null;
        _incorrectAttempts = 0;
    }

    public void start() {
        _start = new DateTime();
    }

    public void error() {
        _incorrectAttempts++;
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
        dest.writeValue(_start);
        dest.writeValue(_end);
        dest.writeInt(_incorrectAttempts);
        dest.writeList(_elements);
    }

    private void readFromParcel(Parcel in ) {
        _start = (DateTime) in.readValue(DateTime.class.getClassLoader());
        _end = (DateTime) in.readValue(DateTime.class.getClassLoader());
        _incorrectAttempts = in.readInt();
        _elements = in.readArrayList(Element.class.getClassLoader());
    }
}

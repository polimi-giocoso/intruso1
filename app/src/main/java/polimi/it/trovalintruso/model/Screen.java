package polimi.it.trovalintruso.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import polimi.it.trovalintruso.R;

/**
 * Created by poool on 09/02/15.
 */
public class Screen implements Parcelable, Serializable {

    private final static long serialVersionUID = 3L;

    private ArrayList<Element> _elements;
    private DateTime _start_time;
    private DateTime _end_time;
    private int _incorrectAttempts;
    private boolean _yourTurn;
    private String _deviceName;

    //getters & setters

    protected void setDeviceName(String s) {
        _deviceName = s;
    }

    public String getDeviceName() {
        return _deviceName;
    }

    public ArrayList<Element> get_elements() {
        return _elements;
    }

    /*public void set_elements(ArrayList<Element> _elements) {
        this._elements = _elements;
    }*/

    public int getErrors() {
        return _incorrectAttempts;
    }

    public Screen() {
        _yourTurn = true;
        _incorrectAttempts = 0;
    }

    public boolean isYourTurn() {
        return _yourTurn;
    }

    public void setYourTurn(boolean value) {
        _yourTurn = value;
    }

    public DateTime getStart() {
        return _start_time;
    }

    public void setStart(DateTime value) {
        _start_time = value;
    }

    public DateTime getEnd() {
        return _end_time;
    }

    public void setEnd(DateTime value) {
        _end_time = value;
    }

    //game methods

    public void initialize(CategoryGroup group, int numOfObjects) {
        _elements = new ArrayList<Element>();
        _elements.add(new Element(true, group.getTarget()));
        ArrayList<String> others = new ArrayList<String>(Arrays.asList(group.getOthers()));
        Collections.shuffle(others, new Random());
        for(int i = 0; i < numOfObjects - 1; i++) {
            _elements.add(new Element(false, others.get(i)));
        }
        Collections.shuffle(_elements, new Random());
        init();
    }

    public void init() {
        _start_time = null;
        _end_time = null;
        _incorrectAttempts = 0;
        _yourTurn = true;
    }

    public void start() {
        _start_time = new DateTime();
    }

    public void error() {
        _incorrectAttempts++;
    }

    public void completed() {
        _end_time = new DateTime();
    }

    public Interval getScreenTime() {
        return new Interval(_start_time.getMillis(), _end_time.getMillis());
    }

    public String getParsedScreenTime(Context context) {
        Period p = getScreenTime().toPeriod();
        int s = p.getSeconds();
        int m = p.getMinutes();
        if(m == 0 && s == 0)
            return "< 1 " + context.getString(R.string.second);
        else {
            PeriodFormatter formatter = new PeriodFormatterBuilder()
                    .appendMinutes()
                    .appendSuffix(" " + context.getString(R.string.minute), " " + context.getString(R.string.minutes))
                    .appendSeparator(" " + context.getString(R.string.and) + " ")
                    .appendSeconds()
                    .appendSuffix(" " + context.getString(R.string.second), " " + context.getString(R.string.seconds))
                    .toFormatter();
            return formatter.print(p);
        }
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
        dest.writeValue(_start_time);
        dest.writeValue(_end_time);
        dest.writeInt(_incorrectAttempts);
        dest.writeList(_elements);
    }

    private void readFromParcel(Parcel in ) {
        _start_time = (DateTime) in.readValue(DateTime.class.getClassLoader());
        _end_time = (DateTime) in.readValue(DateTime.class.getClassLoader());
        _incorrectAttempts = in.readInt();
        _elements = in.readArrayList(Element.class.getClassLoader());
    }
}

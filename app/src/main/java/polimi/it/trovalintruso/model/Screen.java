package polimi.it.trovalintruso.model;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by poool on 09/02/15.
 */
public class Screen implements Serializable {

    private ArrayList<Element> _elements;
    private Interval _response_time;
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
        _response_time = new Interval(_start.getMillis(), _end.getMillis());
    }

    public Interval getScreenTime() {
        return _response_time;
    }
}

package polimi.it.trovalintruso.model;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.ArrayList;

/**
 * Created by poool on 09/02/15.
 */
public class Screen {

    private ArrayList<Element> _elements;
    private Interval _response_time;
    private DateTime _start;
    private DateTime _end;
    private int _incorrect_attempts;
}

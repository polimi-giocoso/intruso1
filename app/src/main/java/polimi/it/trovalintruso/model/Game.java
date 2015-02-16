package polimi.it.trovalintruso.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.Interval;

import java.util.ArrayList;

/**
 * Created by poool on 09/02/15.
 */
public class Game implements Parcelable {

    private Settings _settings;
    private ArrayList<Screen> _screens;
    private int _activeScreen;

    //TODO delete demo settings initialization
    public Game() {
        _settings = new Settings();
        _settings.set_singlePlayer(true);
        _settings.setNumOfObjects(5);
        _settings.setNumOfScreens(2);
        _settings.setTimeLimitEnabled(false);
    }

    public Game(Settings settings) {
        _settings = settings;
    }

    public Settings getSettings() {
        return _settings;
    }

    //methods

    public Interval getGameTime() {
        return null;
    }

    public Screen getActiveScreen() {
        return _screens.get(_activeScreen);
    }

    public ArrayList<Screen> getScreens() {
        return _screens;
    }

    public Boolean goToNextScreen() {
        if(_activeScreen < _settings.getNumOfScreens() - 1) {
            _activeScreen++;
            return true;
        }
        else
            return false;
    }

    public Boolean isLastScreen(){
        return _activeScreen == _settings.getNumOfScreens() - 1;
    }

    public void initialize() {

        //create the screens
        _screens = new ArrayList<Screen>();
        for(int i = 0; i < _settings.getNumOfScreens(); i++) {

            Screen screen = new Screen();
            _screens.add(screen);
        }
        DemoData();
        _activeScreen = 0;
    }

    //TODO change with Category manager
    private void DemoData() {
        for(int i = 0; i < _settings.getNumOfScreens(); i++) {

            Screen screen = _screens.get(i);
            ArrayList<Element> objects = new ArrayList<Element>();
            if(i == 0) {
                for(int j = 0; j < _settings.getNumOfObjects(); j++) {
                    Element el = new Element();
                    el.set_drawable_name("sample_" + j);
                    if(j == 0)
                        el.set_isTarget(true);
                    objects.add(el);
                }
            }
            else {
                for(int j = 0; j < _settings.getNumOfObjects(); j++) {
                    Element el = new Element();
                    int idx = j + 4;
                    el.set_drawable_name("sample_" + idx);
                    if(j == 0)
                        el.set_isTarget(true);
                    objects.add(el);
                }
            }
            screen.set_elements(objects);
        }
    }

    //parcelable implementation

    public int describeContents() {
        return 0;
    }

    public Game(Parcel in ) {
        readFromParcel( in );
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Game createFromParcel(Parcel in ) {
            return new Game( in );
        }

        public Game[] newArray(int size) {
            return new Game[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(_activeScreen);
        dest.writeList(_screens);
        dest.writeParcelable(_settings, flags);
    }

    private void readFromParcel(Parcel in ) {
        _activeScreen = in.readInt();
        _screens = in.readArrayList(Screen.class.getClassLoader());
        _settings = in.readParcelable(Settings.class.getClassLoader());
    }
}

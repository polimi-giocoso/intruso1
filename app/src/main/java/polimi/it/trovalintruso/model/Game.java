package polimi.it.trovalintruso.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by poool on 09/02/15.
 */
public class Game implements Parcelable {

    private Settings _settings;
    private ArrayList<Screen> _screens;
    private int _activeScreen;

    public Game() {
        _settings = new Settings();
        _settings.set_singlePlayer(true);
        _settings.setNumOfObjects(4);
        _settings.setNumOfScreens(2);
        _settings.setTimeLimitEnabled(false);
    }

    public Game(Settings settings) {
        _settings = settings;
    }

    public Settings getSettings() {
        return _settings;
    }

    public Screen getActiveScreen() {
        return _screens.get(_activeScreen);
    }

    public void goToNextScreen() {
        Screen s = _screens.get(_activeScreen);
        s.completed();
        _activeScreen++;
    }

    public void initialize() {

        //create the screens
        _screens = new ArrayList<Screen>();
        for(int i = 0; i < _settings.getNumOfScreens(); i++) {

           Screen screen = new Screen();
        }

        _activeScreen = 0;
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

package polimi.it.trovalintruso.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by poool on 09/02/15.
 */
public class Game implements Serializable {

    private Settings _settings;
    private ArrayList<Screen> _screens;
    private int _activeScreen;

    public Game() {
        _settings = new Settings();
        _settings.set_singlePlayer(true);
        _settings.setNumOfObjects(Settings.ObjectsForPage.Four);
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

}

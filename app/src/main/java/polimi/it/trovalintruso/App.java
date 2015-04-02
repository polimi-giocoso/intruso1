package polimi.it.trovalintruso;

import android.app.Application;

import polimi.it.trovalintruso.helpers.CategoryHelper;
import polimi.it.trovalintruso.model.Game;
import polimi.it.trovalintruso.model.Settings;
import polimi.it.trovalintruso.helpers.GameHelper;

/**
 * Created by poool on 01/03/15.
 */
public class App extends Application {

    public static GameHelper gameHelper;
    public static Game game;
    public static Settings gameSettings = null;
    private static CategoryHelper _categoryHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        _categoryHelper = new CategoryHelper(getBaseContext());
        App.gameHelper = new GameHelper(getBaseContext());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        gameHelper.onAppTerminate();
    }

    public static CategoryHelper getCategoryManager() {
        return _categoryHelper;
    }
}

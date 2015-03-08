package polimi.it.trovalintruso;

import android.app.Application;

import polimi.it.trovalintruso.helpers.CategoryManager;
import polimi.it.trovalintruso.model.Game;
import polimi.it.trovalintruso.multiplayer.GameHelper;

/**
 * Created by poool on 01/03/15.
 */
public class App extends Application {

    //public static MultiPlayerServiceHelper multiPlayerServiceHelper;
    //public static MultiPlayerConnectionHelper mConnection;
    public static GameHelper gameHelper;

    public static Game game;

    private static CategoryManager _categoryManager;

    @Override
    public void onCreate() {
        super.onCreate();
        _categoryManager = new CategoryManager(getBaseContext());
        App.gameHelper = new GameHelper(getBaseContext());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        gameHelper.onAppTerminate();
    }

    public static CategoryManager getCategoryManager() {
        return _categoryManager;
    }
}

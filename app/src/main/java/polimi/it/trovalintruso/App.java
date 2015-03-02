package polimi.it.trovalintruso;

import android.app.Application;
import android.util.Log;

import polimi.it.trovalintruso.helpers.CategoryManager;
import polimi.it.trovalintruso.model.Game;
import polimi.it.trovalintruso.multiplayer.MultiPlayerHelper;
import polimi.it.trovalintruso.multiplayer.network.MultiPlayerConnectionHelper;
import polimi.it.trovalintruso.multiplayer.MultiPlayerServiceHelper;

/**
 * Created by poool on 01/03/15.
 */
public class App extends Application {

    //public static MultiPlayerServiceHelper multiPlayerServiceHelper;
    //public static MultiPlayerConnectionHelper mConnection;
    public static MultiPlayerHelper multiPlayerHelper;

    public static Game game;

    private static CategoryManager _categoryManager;

    @Override
    public void onCreate() {
        super.onCreate();
        _categoryManager = new CategoryManager(getBaseContext());
        App.multiPlayerHelper = new MultiPlayerHelper(getBaseContext());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        multiPlayerHelper.onAppTerminate();
    }

    public static CategoryManager getCategoryManager() {
        return _categoryManager;
    }
}

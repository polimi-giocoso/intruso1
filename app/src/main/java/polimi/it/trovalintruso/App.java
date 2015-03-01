package polimi.it.trovalintruso;

import android.app.Application;

import java.io.IOException;

import polimi.it.trovalintruso.multiplayer.MultiPlayerHelper;

/**
 * Created by poool on 01/03/15.
 */
public class App extends Application {

    public static MultiPlayerHelper mHelper;

    @Override
    public void onCreate() {
        super.onCreate();
    }
}

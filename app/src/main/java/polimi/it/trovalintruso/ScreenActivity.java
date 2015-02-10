package polimi.it.trovalintruso;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import polimi.it.trovalintruso.model.Game;


public class ScreenActivity extends Activity {

    Game game;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen);
        
    }



}

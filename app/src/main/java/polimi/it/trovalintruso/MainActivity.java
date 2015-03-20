package polimi.it.trovalintruso;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Button;

import org.json.JSONException;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import polimi.it.trovalintruso.model.Category;
import polimi.it.trovalintruso.model.Game;
import polimi.it.trovalintruso.model.Settings;


public class MainActivity extends Activity {

    private ArrayList<Category> categories4;
    Context context;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        context = this;
        sharedPref = getApplicationContext()
                .getSharedPreferences("settings", Context.MODE_PRIVATE);
        App.gameHelper.onMainActivityCreate();
        initializeGame();
        initializeUI();
        App.gameHelper.registerCurrentActivity(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        App.gameHelper.onActivityResume(this);
        /*App.mConnection.setmUpdateHandler(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                GameMessage message = (GameMessage) msg.getData().getSerializable("message");

            }
        });*/
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        App.gameHelper.onMainActivityDestroy();
        super.onDestroy();
    }

    private void initializeUI() {
        ButterKnife.inject(this);
    }

    private void initializeGame() {
        if(App.gameSettings == null) {
            categories4 = App.getCategoryManager().getCategoryList4();
            App.gameSettings = new Settings(context);
            App.gameSettings.set_singlePlayer(true);
            App.gameSettings.setCategory(categories4.get(0));
            App.gameSettings.setNumOfObjects(4);
            App.gameSettings.setNumOfScreens(1);
        }

    }

    @OnClick(R.id.image_single_player) void singlePlayer() {
        App.gameSettings.set_singlePlayer(true);
        startGame();
    }

    @OnClick(R.id.image_multi_player) void multiPlayer() {
        App.gameSettings.set_singlePlayer(false);
        startGame();
    }

    @OnClick(R.id.image_settings) void openSettings() {
        Intent i = new Intent(context, SettingsActivity.class);
        startActivity(i);
    }

    private void startGame() {
        App.game = new Game(App.gameSettings);
        App.gameHelper.startGame();
    }
}

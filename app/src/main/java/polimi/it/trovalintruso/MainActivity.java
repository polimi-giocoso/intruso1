package polimi.it.trovalintruso;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

    //private Settings gameSettings;
    //Game game;
    private ArrayList<Category> categories4;
    //private ArrayList<Category> categories6;
    //private int categoryIndex = 0;
    Context context;

    //@InjectView(R.id.button_start_game)
    //Button start_game;

    /*@InjectView(R.id.button_num_obj_each_screen)
    Button button_num_objects_each_screen;

    @InjectView(R.id.button_num_of_screens)
    Button button_num_of_screens;

    @InjectView(R.id.button_config_category)
    Button config_category;*/

    /*@InjectView(R.id.button_single_player)
    Button config_single_player;*/

    //@InjectView(R.id.button_config_time_limit)
    //Button config_time_limit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        context = this;
        initializeGame();
        initializeUI();
        App.gameHelper.onMainActivityCreate();
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
        //config_category.setText(context.getString(R.string.category) + " " + gameSettings.getCategory().getName());
        /*if(gameSettings.singlePlayer())
            config_single_player.setText(R.string.single_player);
        else
            config_single_player.setText(R.string.multi_player);*/
        /*if(!gameSettings.getTimeLimitEnabled())
            config_time_limit.setText(R.string.no_time_limit);
        else
            config_time_limit.setText(context.getString(R.string.time_limit) + " " + (gameSettings.get_timeLimit() / 1000) + " " + getString(R.string.seconds));*/
        //button_num_of_screens.setText(getString(R.string.num_of_screens) + " " + gameSettings.getNumOfScreens());
        //button_num_objects_each_screen.setText(getString(R.string.num_of_objects) + " " + gameSettings.getNumOfObjects());
    }

    private void initializeGame() {
        categories4 = App.getCategoryManager().getCategoryList4();
        try {
            App.gameSettings = new Settings(context);
            App.gameSettings.set_singlePlayer(true);
            App.gameSettings.setCategory(categories4.get(0));
            App.gameSettings.setNumOfObjects(4);
            App.gameSettings.setNumOfScreens(1);
        }
        catch(JSONException e) {

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

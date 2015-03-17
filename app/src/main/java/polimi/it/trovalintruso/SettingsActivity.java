package polimi.it.trovalintruso;

import android.app.Activity;
import android.content.Context;
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


public class SettingsActivity extends Activity {

    private Settings gameSettings;
    //Game game;
    private ArrayList<Category> categories4;
    private ArrayList<Category> categories6;
    private int categoryIndex = 0;
    Context context;

    //@InjectView(R.id.button_start_game)
    //Button start_game;

    @InjectView(R.id.button_num_obj_each_screen)
    Button button_num_objects_each_screen;

    @InjectView(R.id.button_num_of_screens)
    Button button_num_of_screens;

    @InjectView(R.id.button_config_category)
    Button config_category;

    @InjectView(R.id.button_single_player)
    Button config_single_player;

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
        config_category.setText(context.getString(R.string.category) + " " + gameSettings.getCategory().getName());
        if(gameSettings.singlePlayer())
            config_single_player.setText(R.string.single_player);
        else
            config_single_player.setText(R.string.multi_player);
        /*if(!gameSettings.getTimeLimitEnabled())
            config_time_limit.setText(R.string.no_time_limit);
        else
            config_time_limit.setText(context.getString(R.string.time_limit) + " " + (gameSettings.get_timeLimit() / 1000) + " " + getString(R.string.seconds));*/
        button_num_of_screens.setText(getString(R.string.num_of_screens) + " " + gameSettings.getNumOfScreens());
        button_num_objects_each_screen.setText(getString(R.string.num_of_objects) + " " + gameSettings.getNumOfObjects());
    }

    private void initializeGame() {
        categories4 = App.getCategoryManager().getCategoryList4();
        categories6 = App.getCategoryManager().getCategoryList6();
        try {
            gameSettings = new Settings(context);
            gameSettings.set_singlePlayer(true);
            gameSettings.setCategory(categories4.get(0));
            gameSettings.setNumOfObjects(4);
            gameSettings.setNumOfScreens(1);
            //gameSettings.setTimeLimitEnabled(false);
        }
        catch(JSONException e) {

        }

    }

    @OnClick(R.id.button_num_obj_each_screen) void changeNumOfObjects() {
        int actual = gameSettings.getNumOfObjects();
        if(actual == 4) {
            actual = 6;
            categoryIndex = 0;
            gameSettings.setCategory(categories6.get(categoryIndex));
        }
        else {
            actual = 4;
            categoryIndex = 0;
            gameSettings.setCategory(categories4.get(categoryIndex));
        }
        gameSettings.setNumOfObjects(actual);
        checkNumOfScreens(false);
        initializeUI();
    }

    @OnClick(R.id.button_num_of_screens) void changeNumOfScreens() {
        /*int actual = gameSettings.getNumOfScreens();
        int max = gameSettings.getCategory().getGroups().size();
        if(actual < max)
            actual++;
        else
            actual = 1;
        gameSettings.setNumOfScreens(actual);*/
        checkNumOfScreens(true);
        initializeUI();
    }

    @OnClick(R.id.button_config_category) void changeCategory() {
        int numOfObjects = gameSettings.getNumOfObjects();
        if(numOfObjects == 4) {
            if(categoryIndex < categories4.size() - 1)
                categoryIndex ++;
            else
                categoryIndex = 0;
            gameSettings.setCategory(categories4.get(categoryIndex));
        }
        else {
            if(categoryIndex < categories6.size() - 1)
                categoryIndex ++;
            else
                categoryIndex = 0;
            gameSettings.setCategory(categories6.get(categoryIndex));
        }
        checkNumOfScreens(false);
        initializeUI();
    }

    private void checkNumOfScreens(boolean increment) {
        int max = 0;
        if(gameSettings.getCategory().getRandom()) {
            if(gameSettings.getNumOfObjects() == 4)
                for(Category c : App.getCategoryManager().getCategoryList4())
                    max += c.getGroups().size();
            else
                for(Category c : App.getCategoryManager().getCategoryList6())
                    max += c.getGroups().size();
        }
        else
            max = gameSettings.getCategory().getGroups().size();
        int actual = gameSettings.getNumOfScreens();
        if(actual < max) {
            if (increment)
                actual++;
        }
        else
            actual = 1;
        gameSettings.setNumOfScreens(actual);
    }

    @OnClick(R.id.button_single_player) void changeSinglePlayer() {
        gameSettings.set_singlePlayer(!gameSettings.singlePlayer());
        initializeUI();
    }

    @OnClick(R.id.button_start_game) void startGame() {
        App.game = new Game(gameSettings);
        App.gameHelper.startGame();
    }
}

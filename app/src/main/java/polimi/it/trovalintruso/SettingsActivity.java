package polimi.it.trovalintruso;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Button;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import polimi.it.trovalintruso.model.Category;
import polimi.it.trovalintruso.model.Settings;

/**
 * Created by poool on 20/03/15.
 */
public class SettingsActivity extends Activity {

    //private Settings gameSettings;
    //Game game;
    private ArrayList<Category> categories4;
    private ArrayList<Category> categories6;
    private int categoryIndex = 0;
    Context context;

    @InjectView(R.id.button_num_obj_each_screen)
    Button button_num_objects_each_screen;

    @InjectView(R.id.button_num_of_screens)
    Button button_num_of_screens;

    @InjectView(R.id.button_config_category)
    Button config_category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        context = this;
        categories4 = App.getCategoryManager().getCategoryList4();
        categories6 = App.getCategoryManager().getCategoryList6();
        initializeUI();
        App.gameHelper.registerCurrentActivity(this);
        if(App.gameSettings.getNumOfObjects() == 4)
            categoryIndex = categories4.indexOf(App.gameSettings.getCategory());
        else
            categoryIndex = categories6.indexOf(App.gameSettings.getCategory());
    }

    private void initializeUI() {
        ButterKnife.inject(this);
        config_category.setText(context.getString(R.string.category) + " " + App.gameSettings.getCategory().getName());
        button_num_of_screens.setText(getString(R.string.num_of_screens) + " " + App.gameSettings.getNumOfScreens());
        button_num_objects_each_screen.setText(getString(R.string.num_of_objects) + " " + App.gameSettings.getNumOfObjects());
    }

    @OnClick(R.id.button_num_obj_each_screen) void changeNumOfObjects() {
        int actual = App.gameSettings.getNumOfObjects();
        if(actual == 4) {
            actual = 6;
            categoryIndex = 0;
            App.gameSettings.setCategory(categories6.get(categoryIndex));
        }
        else {
            actual = 4;
            categoryIndex = 0;
            App.gameSettings.setCategory(categories4.get(categoryIndex));
        }
        App.gameSettings.setNumOfObjects(actual);
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
        int numOfObjects = App.gameSettings.getNumOfObjects();
        if(numOfObjects == 4) {
            if(categoryIndex < categories4.size() - 1)
                categoryIndex++;
            else
                categoryIndex = 0;
            App.gameSettings.setCategory(categories4.get(categoryIndex));
        }
        else {
            if(categoryIndex < categories6.size() - 1)
                categoryIndex ++;
            else
                categoryIndex = 0;
            App.gameSettings.setCategory(categories6.get(categoryIndex));
        }
        checkNumOfScreens(false);
        initializeUI();
    }

    private void checkNumOfScreens(boolean increment) {
        int max = 0;
        if(App.gameSettings.getCategory().getRandom()) {
            if(App.gameSettings.getNumOfObjects() == 4)
                for(Category c : App.getCategoryManager().getCategoryList4())
                    max += c.getGroups().size();
            else
                for(Category c : App.getCategoryManager().getCategoryList6())
                    max += c.getGroups().size();
        }
        else
            max = App.gameSettings.getCategory().getGroups().size();
        int actual = App.gameSettings.getNumOfScreens();
        if(actual < max) {
            if (increment)
                actual++;
        }
        else
            actual = 1;
        App.gameSettings.setNumOfScreens(actual);
    }

    @OnClick(R.id.button_config_device) void openConfigActivity() {
        Intent i = new Intent(context, ConfigDeviceActivity.class);
        startActivity(i);
    }
}

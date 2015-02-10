package polimi.it.trovalintruso;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;

import polimi.it.trovalintruso.model.Element;
import polimi.it.trovalintruso.model.Game;
import polimi.it.trovalintruso.model.Screen;
import polimi.it.trovalintruso.ui.ImageAdapter;

import static polimi.it.trovalintruso.R.string;


public class ScreenActivity extends Activity {

    Game game;
    Context context;
    ImageAdapter adapter;

    //@InjectView(R.id.game_grid_view)
    //GridView game_grid_view;

    //@InjectView(R.id.game_view)
    //LinearLayout game_view;

    @InjectView(R.id.game_layout1)
    LinearLayout layout1;

    @InjectView(R.id.game_layout2)
    LinearLayout layout2;

    @InjectView(R.id.next_screen_button)
    Button next_screen_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        context = this;
        String pkg = context.getPackageName();
        game = getIntent().getExtras().getParcelable(pkg + ".game");
        initializeUI();
        gameScreenInitialization();
    }

    private void initializeUI() {
        ButterKnife.inject(this);
        if(game.isLastScreen())
            next_screen_button.setText(string.end_game);
        else
            next_screen_button.setText(string.next_level);
        adapter = new ImageAdapter(this, game.getActiveScreen().get_elements());
        //game_grid_view.setAdapter(adapter);
        //game_grid_view.setNumColumns(2);
        populateGameArea();
    }

    private void gameScreenInitialization() {
        game.getActiveScreen().start();
        if(game.getSettings().getTimeLimitEnabled()) {
            //TODO set timer here
        }
    }

    /*@OnItemClick(R.id.game_grid_view) void ImageClick(AdapterView<?> parent, View v, int position, long id) {
        Element el = adapter.getItem(position);
        if(el.get_isTarget()) {
            game.getActiveScreen().completed();
            YoYo.with(Techniques.Bounce)
                    .duration(700)
                    .playOn(v);
            next_screen_button.setEnabled(true);
        }
        else {
            YoYo.with(Techniques.Shake)
                    .duration(700)
                    .playOn(v);
            game.getActiveScreen().error();
        }
    }*/

    @OnClick(R.id.next_screen_button) void screenCompleted() {
        if(game.goToNextScreen()) {
            if (game.getSettings().get_singlePlayer()) {
                Intent intent = new Intent(context, ScreenActivity.class);
                String pkg = context.getPackageName();
                intent.putExtra(pkg + ".game", game);
                context.startActivity(intent);
            } else {

            }
        }
        else {

        }
    }

    private void populateGameArea() {
        Screen s = game.getActiveScreen();
        int num = s.get_elements().size();
        for(int i = 0; i < num; i++) {
            Element el = s.get_elements().get(i);
            ImageView image = new ImageView(context);
            image.setImageResource(context.getResources().getIdentifier(el.get_drawable_name(), "drawable", context.getPackageName()));
            image.setTag(i);
            if(num % 2 == 0) {
                if(num > 4)
                    image.setPadding(20, 20, 20, 20);
                else
                    image.setPadding(60, 20, 60, 20);
                if (i < num / 2)
                    layout1.addView(image);
                else
                    layout2.addView(image);
            }
            else {
                image.setPadding(20, 20, 20, 20);
                int x = num / 2;
                if (i < x + 1)
                    layout1.addView(image);
                else
                    layout2.addView(image);
            }
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = (int) v.getTag();
                    Element el = game.getActiveScreen().get_elements().get(index);
                    if(el.get_isTarget()) {
                        game.getActiveScreen().completed();
                        YoYo.with(Techniques.Bounce)
                                .duration(700)
                                .playOn(v);
                        next_screen_button.setEnabled(true);
                    }
                    else {
                        YoYo.with(Techniques.Shake)
                                .duration(700)
                                .playOn(v);
                        game.getActiveScreen().error();
                    }
                }
            });
        }
    }
}

package polimi.it.trovalintruso;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.view.View;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;

import polimi.it.trovalintruso.model.Element;
import polimi.it.trovalintruso.model.Game;
import polimi.it.trovalintruso.ui.ImageAdapter;


public class ScreenActivity extends Activity {

    Game game;
    Context context;
    ImageAdapter adapter;

    @InjectView(R.id.game_grid_view)
    GridView game_grid_view;

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
            next_screen_button.setText(R.string.end_game);
        else
            next_screen_button.setText(R.string.next_level);
        adapter = new ImageAdapter(this, game.getActiveScreen().get_elements());
        game_grid_view.setAdapter(adapter);
        game_grid_view.setNumColumns(2);
    }

    private void gameScreenInitialization() {
        game.getActiveScreen().start();
        if(game.getSettings().getTimeLimitEnabled()) {
            //TODO set timer here
        }
    }

    @OnItemClick(R.id.game_grid_view) void ImageClick(AdapterView<?> parent, View v, int position, long id) {
        Element el = adapter.getItem(position);
        if(el.get_is_target()) {
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
}

package polimi.it.trovalintruso;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import polimi.it.trovalintruso.model.Game;
import polimi.it.trovalintruso.ui.ResultsAdapter;


public class ResultsActivity extends Activity {

    Context context;
    Game game;
    ResultsAdapter adapter;

    @InjectView(R.id.results_list_view)
    ListView list;

    @InjectView(R.id.button_restart_game)
    Button restart_game;

    @InjectView(R.id.button_quit_game)
    Button quit_game;

    @InjectView(R.id.total_game_time)
    TextView total_game_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        context = this;
        String pkg = context.getPackageName();
        game = getIntent().getExtras().getParcelable(pkg + ".game");
        initializeUi();
    }

    private void initializeUi() {
        ButterKnife.inject(this);
        adapter = new ResultsAdapter(game, context);
        list.setAdapter(adapter);
        total_game_time.setText(getString(R.string.game_time) + " " + game.getGameTime(context));
    }

    @OnClick(R.id.button_quit_game) void quit() {
        Intent intent = new Intent(context, SettingsActivity.class);
        String pkg = context.getPackageName();
        //intent.putExtra(pkg + ".game", game);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    @OnClick(R.id.button_restart_game) void restart() {
        Intent intent = new Intent(context, ScreenActivity.class);
        String pkg = context.getPackageName();
        game.restart();
        intent.putExtra(pkg + ".game", game);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }
}

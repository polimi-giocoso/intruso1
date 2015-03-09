package polimi.it.trovalintruso;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import polimi.it.trovalintruso.ui.ResultsAdapter;


public class ResultsActivity extends Activity {

    Context context;
    //Game game;
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
        //game = getIntent().getExtras().getParcelable(pkg + ".game");
        initializeUi();
        if(!App.gameHelper.isServer()) {
            restart_game.setEnabled(false);
            quit_game.setEnabled(false);
        }
        App.gameHelper.registerCurrentActivity(this);
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        App.gameHelper.onActivityResume(this);
    }

    private void initializeUi() {
        ButterKnife.inject(this);
        adapter = new ResultsAdapter(App.game, context);
        list.setAdapter(adapter);
        total_game_time.setText(getString(R.string.game_time) + " " + App.game.getGameTime(context));
    }

    @OnClick(R.id.button_quit_game) void quit() {
        App.gameHelper.quitGame();
    }

    @OnClick(R.id.button_restart_game) void restart() {
        App.gameHelper.restartGame();
    }
}

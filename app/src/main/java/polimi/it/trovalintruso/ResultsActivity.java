package polimi.it.trovalintruso;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
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
        initializeUi();
        App.gameHelper.registerCurrentActivity(this);
        if(!App.gameHelper.isServer() && !App.game.getSettings().singlePlayer())
            new MailTask().execute();
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
        if(!App.gameHelper.isServer() && !App.game.getSettings().singlePlayer()) {
            restart_game.setEnabled(false);
            quit_game.setEnabled(false);
        }
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

    private class MailTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {
            /*try {
                EmailHelper sender = new EmailHelper("username@gmail.com", "password");
                sender.sendMail("This is Subject",
                        "This is Body",
                        "user@gmail.com",
                        "user@yahoo.com");
            } catch (Exception e) {
                Log.e("SendMail", e.getMessage(), e);
            }*/
            return null;
        }
    }
}

package polimi.it.trovalintruso;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import polimi.it.trovalintruso.multiplayer.EmailHelper;
import polimi.it.trovalintruso.ui.ResultsAdapter;


public class ResultsActivity extends Activity {

    Context context;
    ResultsAdapter adapter;

    @InjectView(R.id.results_list_view)
    ListView list;

    @InjectView(R.id.button_restart_game)
    ImageView restart_game;

    @InjectView(R.id.button_quit_game)
    ImageView quit_game;

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
        if(App.gameHelper.isServer() || App.game.getSettings().singlePlayer())
            //new MailTask().execute();
            sendMail();
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
            restart_game.setVisibility(View.INVISIBLE);
            quit_game.setVisibility(View.INVISIBLE);
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

    private void sendMail() {
        try {
            SharedPreferences sharedPrefs = context.getApplicationContext()
                    .getSharedPreferences("settings", Context.MODE_PRIVATE);
            String email = sharedPrefs.getString("account_email", null);
            String password = sharedPrefs.getString("account_password", null);
            String to = sharedPrefs.getString("email", null);
            if(email != null && !email.isEmpty() && password != null && !password.isEmpty() && to != null && !to.isEmpty()) {
                EmailHelper sender = new EmailHelper(email, password, context);
                sender.sendMail("Tova Intruso 1 - Risultato",
                        App.game.GameToString(context),
                        email,
                        to);
            }
        } catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);
        }
    }

    /*private class MailTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {

            return null;
        }
    }*/
}

package polimi.it.trovalintruso;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import butterknife.ButterKnife;

import butterknife.InjectView;
import polimi.it.trovalintruso.model.Game;
import polimi.it.trovalintruso.model.Settings;


public class SettingsActivity extends Activity {

    Settings gameSettings;
    Game game;
    Context context;

    @InjectView(R.id.button_start_game)Button start_game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        initializeUI();
        initializeGame();
    }

    private void initializeGame() {
        gameSettings = new Settings();
        game = new Game(gameSettings);
    }

    private void initializeUI() {
        start_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame();
            }
        });
    }

    private void startGame() {
        game.initialize();
        if(gameSettings.get_singlePlayer()) {
            Intent intent = new Intent(context, ScreenActivity.class);
            String pkg = context.getPackageName();
            intent.putExtra(pkg + ".game", game);
            context.startActivity(intent);
        }
        else {

        }

    }


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}

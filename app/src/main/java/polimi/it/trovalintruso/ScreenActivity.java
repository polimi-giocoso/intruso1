package polimi.it.trovalintruso;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.speech.tts.TextToSpeech;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import polimi.it.trovalintruso.model.Element;
import polimi.it.trovalintruso.model.Game;
import polimi.it.trovalintruso.model.Screen;

import static polimi.it.trovalintruso.R.string;


public class ScreenActivity extends Activity {

    Game game;
    Context context;
    Boolean inGame;
    TextToSpeech ttobj;

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
        ttobj=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR){
                    ttobj.setLanguage(Locale.ITALIAN);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onPause(){
        if(ttobj !=null){
            ttobj.stop();
            ttobj.shutdown();
        }
        super.onPause();
    }

    private void initializeUI() {
        ButterKnife.inject(this);
        if(game.isLastScreen())
            next_screen_button.setText(string.end_game);
        else
            next_screen_button.setText(string.next_level);
        populateGameArea();
    }

    private void gameScreenInitialization() {
        game.getActiveScreen().start();
        inGame = true;
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
            Intent intent = new Intent(context, ResultsActivity.class);
            String pkg = context.getPackageName();
            intent.putExtra(pkg + ".game", game);
            context.startActivity(intent);
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
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            if(num % 2 == 0) {
                if(num > 4) {
                    lp.setMargins(25, 10, 25, 10);
                    image.setLayoutParams(lp);
                }
                else {
                    lp.setMargins(100, 10, 100, 10);
                    image.setLayoutParams(lp);
                }
                if (i < num / 2)
                    layout1.addView(image);
                else
                    layout2.addView(image);
            }
            else {
                image.setPadding(20, 20, 20, 20);
                int x = num / 2;
                if (i < x + 1) {
                    lp.setMargins(25, 10, 25, 10);
                    image.setLayoutParams(lp);
                    layout1.addView(image);
                }
                else {
                    lp.setMargins(80, 10, 80, 10);
                    image.setLayoutParams(lp);
                    layout2.addView(image);
                }
            }
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = (int) v.getTag();
                    Element el = game.getActiveScreen().get_elements().get(index);
                    if(inGame) {
                        if(el.get_isTarget()) {
                            game.getActiveScreen().completed();
                            ttobj.speak("ottimo", TextToSpeech.QUEUE_FLUSH, null);
                            YoYo.with(Techniques.Bounce)
                                    .duration(700)
                                    .playOn(v);
                            next_screen_button.setEnabled(true);
                            inGame = false;
                        }
                        else {
                            ttobj.speak("hai sbagliato", TextToSpeech.QUEUE_FLUSH, null);
                            YoYo.with(Techniques.Shake)
                                    .duration(700)
                                    .playOn(v);
                            game.getActiveScreen().error();
                        }
                    }
                }
            });
        }
    }
}

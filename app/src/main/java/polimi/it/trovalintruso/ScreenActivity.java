package polimi.it.trovalintruso;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import polimi.it.trovalintruso.helpers.BitmapWorkerTask;
import polimi.it.trovalintruso.helpers.ImageCacheHelper;
import polimi.it.trovalintruso.model.Element;
import polimi.it.trovalintruso.model.Screen;

import static polimi.it.trovalintruso.R.string;


public class ScreenActivity extends Activity {

    //Game game;
    private Context context;
    private Boolean inGame;
    private boolean yourTurn;
    private TextToSpeech ttobj;
    private ArrayList<ImageView> _imageList;
    private ImageCacheHelper mCacheHelper;

    @InjectView(R.id.game_layout1)
    LinearLayout layout1;

    @InjectView(R.id.game_layout2)
    LinearLayout layout2;

    @InjectView(R.id.next_screen_button)
    ImageView next_screen_button;

    @InjectView(R.id.turno_giocatore)
    ImageView turno_giocatore;

    @InjectView(R.id.turno_avversario)
    ImageView turno_avversario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCacheHelper = App.gameHelper.getImageCacheHelper();
        setContentView(R.layout.activity_screen);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        context = this;
        String pkg = context.getPackageName();
        //game = getIntent().getExtras().getParcelable(pkg + ".game");
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
        App.gameHelper.registerCurrentActivity(this);
        if(!App.game.getSettings().singlePlayer()) {
            if(yourTurn)
                turno_giocatore.setVisibility(View.VISIBLE);
            else
                turno_avversario.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        App.gameHelper.onActivityResume(this);
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
        next_screen_button.setVisibility(View.INVISIBLE);
        /*if(App.game.isLastScreen())
            next_screen_button.setText(string.end_game);
        else
            next_screen_button.setText(string.next_level);*/
    }

    private void gameScreenInitialization() {
        yourTurn = App.game.getActiveScreen().isYourTurn();
        App.game.getActiveScreen().start();
        inGame = true;
        populateGameArea();
    }

    @OnClick(R.id.next_screen_button) void screenCompleted() {
        App.gameHelper.nextScreen();
    }

    private void loadBitmap(int resId, ImageView imageView) {
        final String imageKey = String.valueOf(resId);

        final Bitmap bitmap = mCacheHelper.getBitmapFromMemCache(imageKey);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageResource(resId);
            BitmapWorkerTask task = new BitmapWorkerTask(imageView, context);
            task.execute(resId);
        }
    }

    private void populateGameArea() {
        _imageList = new ArrayList<>();
        Screen s = App.game.getActiveScreen();
        int num = s.get_elements().size();
        for(int i = 0; i < num; i++) {
            Element el = s.get_elements().get(i);
            ImageView image = new ImageView(context);
            /*BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getResources(), context.getResources().getIdentifier(el.get_drawable_name(), "drawable", context.getPackageName()), options);
            int imageHeight = options.outHeight;
            int imageWidth = options.outWidth;
            String imageType = options.outMimeType;*/
            loadBitmap(context.getResources().getIdentifier(el.get_drawable_name(), "drawable", context.getPackageName()), image);
            //image.setImageResource(context.getResources().getIdentifier(el.get_drawable_name(), "drawable", context.getPackageName()));
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
            _imageList.add(image);
            if(yourTurn)
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int index = (int) v.getTag();
                        elementFeedback(index);
                        App.gameHelper.elementPressed(index);
                    }
                });
        }
    }

    public void elementFeedback(int index) {
        View v = null;
        for(ImageView img : _imageList) {
            Object tag = img.getTag();
            if (tag == index)
                v = img;
        }
        Element el = App.game.getActiveScreen().get_elements().get(index);
        if(inGame) {
            if(el.get_isTarget()) {
                App.game.getActiveScreen().completed();
                ttobj.speak("ottimo", TextToSpeech.QUEUE_FLUSH, null);
                YoYo.with(Techniques.Bounce)
                        .duration(700)
                        .playOn(v);
                if(yourTurn) {
                    next_screen_button.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.BounceIn)
                            .duration(700)
                            .playOn(next_screen_button);
                }
                inGame = false;
            }
            else {
                ttobj.speak("sbagliato", TextToSpeech.QUEUE_FLUSH, null);
                YoYo.with(Techniques.Shake)
                        .duration(700)
                        .playOn(v);
                App.game.getActiveScreen().error();
            }
        }
    }
}

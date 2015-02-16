package polimi.it.trovalintruso;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.ListView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import polimi.it.trovalintruso.model.Game;
import polimi.it.trovalintruso.ui.ResultsAdapter;


public class ResultsActivity extends Activity {

    Context context;
    Game game;
    ResultsAdapter adapter;

    @InjectView(R.id.results_list_view)
    ListView list;

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
    }
}

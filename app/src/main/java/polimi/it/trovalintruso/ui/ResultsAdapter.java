package polimi.it.trovalintruso.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import butterknife.ButterKnife;
import butterknife.InjectView;
import polimi.it.trovalintruso.R;
import polimi.it.trovalintruso.model.Game;
import polimi.it.trovalintruso.model.Screen;

/**
 * Created by poool on 16/02/15.
 */
public class ResultsAdapter extends BaseAdapter {


    Game _game;
    Context _context;
    LayoutInflater _inflater;

    public ResultsAdapter(Game game, Context context) {
        _game = game;
        _context = context;
        _inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return _game.getScreens().size();
    }

    @Override
    public Object getItem(int position) {
        return _game.getScreens().get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = _inflater.inflate(R.layout.results_row, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        Screen s = _game.getScreens().get(position);
        int level = position + 1;
        holder.title.setText(_context.getString(R.string.level) + " " + level);
        holder.errors.setText("" + s.getErrors());
        /*PeriodFormatter formatter = new PeriodFormatterBuilder()
                .appendMinutes()
                .appendSuffix(" " + _context.getString(R.string.minute), " " +  _context.getString(R.string.minutes))
                .appendSeparator(" " + _context.getString(R.string.and) + " ")
                .appendSeconds()
                .appendSuffix(" " + _context.getString(R.string.second), " " + _context.getString(R.string.seconds))
                .toFormatter();*/
        holder.time.setText("" + s.getParsedScreenTime(_context));

        return view;
    }

    static class ViewHolder {

        @InjectView(R.id.screen_title)
        TextView title;

        @InjectView(R.id.screen_errors)
        TextView errors;

        @InjectView(R.id.screen_time)
        TextView time;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}

package polimi.it.trovalintruso.ui;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import polimi.it.trovalintruso.R;
import polimi.it.trovalintruso.model.Element;

/**
 * Created by Paolo on 10/02/2015.
 */
public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Element> mElements;

    public ImageAdapter(Context c, ArrayList<Element> els) {
        mContext = c;
        mElements = els;
    }

    public int getCount() {
        return mElements.size();
    }

    public Element getItem(int position) {
        return mElements.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }
        imageView.setImageResource(mContext.getResources().getIdentifier(mElements.get(position).get_drawable_name(), "drawable", mContext.getPackageName()));
        return imageView;
    }

}

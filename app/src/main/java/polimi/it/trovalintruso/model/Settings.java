package polimi.it.trovalintruso.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import polimi.it.trovalintruso.R;

/**
 * Created by poool on 09/02/15.
 */
public class Settings implements Parcelable {

    //public static enum ObjectsForPage { Four, Five, Six }

    //public static enum Category { Casuale, Colori, Forme}

    private ArrayList<Category> _categoryList;

    private int _numOfObjects;
    private int _numOfScreens;
    private Boolean _timeLimitEnabled;
    private int _timeLimit;
    private Boolean _randomCategory;
    private Category _category;
    private Boolean _singlePlayer;

    public Settings(Context context) throws JSONException {
        _numOfObjects = 4;
        _numOfScreens = 2;
        _randomCategory = false;
        _timeLimitEnabled = false;
        _timeLimit = 0;
        _categoryList = new ArrayList<Category>();
        String categories = readCategoriesFile(context);
        if(categories != null) {
            JSONArray arr = new JSONArray(categories);
            for(int i = 0; i < arr.length(); i++) {
                _categoryList.add(new Category(arr.getJSONObject(i)));
            }
        }
    }

    public Category getCategory() {
        return _category;
    }

    public void setCategory(Category category) {
        _category = category;
    }

    public ArrayList<Category> getCategoryList() {
        return _categoryList;
    }

    public Boolean getRandomCategory() {
        return _randomCategory;
    }

    public void setRandomCategory(Boolean random) {
        _randomCategory = random;
    }

    public int getNumOfObjects() {
        return _numOfObjects;
    }

    public void setNumOfObjects(int numOfObjects) {
        this._numOfObjects = numOfObjects;
    }

    public Boolean getTimeLimitEnabled() {
        return _timeLimitEnabled;
    }

    public void setTimeLimitEnabled(Boolean timeLimit) {
        this._timeLimitEnabled = timeLimit;
    }

    public int getNumOfScreens() {
        return _numOfScreens;
    }

    public void setNumOfScreens(int numOfScreens) {
        this._numOfScreens = numOfScreens;
    }

    public int get_timeLimit() {
        return _timeLimit;
    }

    public void set_timeLimit(int _timeLimit) {
        this._timeLimit = _timeLimit;
    }

    public Boolean get_singlePlayer() {
        return _singlePlayer;
    }

    public void set_singlePlayer(Boolean singlePlayer) {
        this._singlePlayer = singlePlayer;
    }

    //Parcelable implementation

    public int describeContents() {
        return 0;
    }

    public Settings(Parcel in ) {
        readFromParcel( in );
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Settings createFromParcel(Parcel in ) {
            return new Settings( in );
        }

        public Settings[] newArray(int size) {
            return new Settings[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(_numOfScreens);
        dest.writeInt(_numOfObjects);
        dest.writeByte((byte) (_timeLimitEnabled ? 1 : 0));
        dest.writeByte((byte) (_singlePlayer ? 1 : 0));
        dest.writeInt(_timeLimit);
    }

    private void readFromParcel(Parcel in ) {
        _numOfScreens = in.readInt();
        _numOfObjects = in.readInt();
        _timeLimitEnabled = in.readByte() != 0;
        _singlePlayer = in.readByte() != 0;
        _timeLimit = in.readInt();
    }

    private String readCategoriesFile(Context ctx)
    {
        InputStream inputStream = ctx.getResources().openRawResource(R.raw.categories);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int i;
        try {
            i = inputStream.read();
            while (i != -1)
            {
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            return null;
        }
        return byteArrayOutputStream.toString();
    }
}
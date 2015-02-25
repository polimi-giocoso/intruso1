package polimi.it.trovalintruso.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.Duration;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import polimi.it.trovalintruso.R;

/**
 * Created by poool on 09/02/15.
 */
public class Game implements Parcelable {

    private Settings _settings;
    private ArrayList<Screen> _screens;
    private int _activeScreen;

    public Game(Settings settings) {
        _settings = settings;
    }

    public Settings getSettings() {
        return _settings;
    }

    //methods

    public String getGameTime(Context context) {
        Duration i = _screens.get(0).getScreenTime().toDuration();
        for(int x = 1; x < _screens.size(); x++) {
            i = i.plus(_screens.get(x).getScreenTime().toDuration());
        }
        //Interval i = new Interval(getScreens().get(0).getScreenTime().getStart(),
        //        getScreens().get(getScreens().size()-1).getScreenTime().getEnd());
        PeriodFormatter formatter = new PeriodFormatterBuilder()
                .appendMinutes()
                .appendSuffix(" " + context.getString(R.string.minute), " " +  context.getString(R.string.minutes))
                .appendSeparator(" " + context.getString(R.string.and) + " ")
                .appendSeconds()
                .appendSuffix(" " + context.getString(R.string.second), " " + context.getString(R.string.seconds))
                .toFormatter();
        return formatter.print(i.toPeriod());
    }

    public Screen getActiveScreen() {
        return _screens.get(_activeScreen);
    }

    public ArrayList<Screen> getScreens() {
        return _screens;
    }

    public Boolean goToNextScreen() {
        if(_activeScreen < _settings.getNumOfScreens() - 1) {
            _activeScreen++;
            return true;
        }
        else
            return false;
    }

    public Boolean isLastScreen(){
        return _activeScreen == _settings.getNumOfScreens() - 1;
    }

    public void initialize() {
        //create the screens
        _screens = new ArrayList<Screen>();
        for(int i = 0; i < _settings.getNumOfScreens(); i++) {

            Screen screen = new Screen();
            _screens.add(screen);
        }
        //DemoData();
        fillScreens();
        _activeScreen = 0;
    }

    private void fillScreens() {
        Random rand = new Random();
        if(_settings.getRandomCategory()) {
            HashMap<Integer, ArrayList<Integer>> used = new HashMap<Integer, ArrayList<Integer>>();
            for(int i = 0; i < _settings.getNumOfScreens(); i++) {
                int max1 = _settings.getCategoryList().size() - 1;
                int min1 = 0;
                int randomCat = rand.nextInt((max1 - min1) + 1) + min1;
                if(!used.keySet().contains(randomCat))
                    used.put(randomCat, new ArrayList<Integer>());
                Category category = _settings.getCategoryList().get(randomCat);
                ArrayList<CategoryGroup> groups = category.getGroups();
                int max = groups.size() - 1;
                int min = 0;
                int random = rand.nextInt((max - min) + 1) + min;
                while(used.get(randomCat).contains(random))
                    random = rand.nextInt((max - min) + 1) + min;
                CategoryGroup cg = groups.get(random);
                Screen screen = _screens.get(i);
                screen.initialize(cg, _settings.getNumOfObjects());
            }
        }
        else {
            Category category = _settings.getCategory();
            ArrayList<CategoryGroup> groups = category.getGroups();
            ArrayList<Integer> used = new ArrayList<Integer>();
            for(int i = 0; i < _settings.getNumOfScreens(); i++) {
                int max = groups.size() - 1;
                int min = 0;
                int random = rand.nextInt((max - min) + 1) + min;
                while(used.contains(random))
                    random = rand.nextInt((max - min) + 1) + min;
                used.add(random);
                CategoryGroup cg = groups.get(random);
                Screen screen = _screens.get(i);
                screen.initialize(cg, _settings.getNumOfObjects());
            }
        }
    }

    /*//TODO change with Category manager
    private void DemoData() {
        for(int i = 0; i < _settings.getNumOfScreens(); i++) {

            Screen screen = _screens.get(i);
            ArrayList<Element> objects = new ArrayList<Element>();
            if(i == 0) {
                for(int j = 0; j < _settings.getNumOfObjects(); j++) {
                    Element el = new Element();
                    el.set_drawable_name("sample_" + j);
                    if(j == 0)
                        el.set_isTarget(true);
                    objects.add(el);
                }
            }
            else {
                for(int j = 0; j < _settings.getNumOfObjects(); j++) {
                    Element el = new Element();
                    int idx = j + 4;
                    el.set_drawable_name("sample_" + idx);
                    if(j == 0)
                        el.set_isTarget(true);
                    objects.add(el);
                }
            }
            screen.set_elements(objects);
        }
    }*/

    //parcelable implementation

    public int describeContents() {
        return 0;
    }

    public Game(Parcel in ) {
        readFromParcel( in );
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Game createFromParcel(Parcel in ) {
            return new Game( in );
        }

        public Game[] newArray(int size) {
            return new Game[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(_activeScreen);
        dest.writeList(_screens);
        dest.writeParcelable(_settings, flags);
    }

    private void readFromParcel(Parcel in ) {
        _activeScreen = in.readInt();
        _screens = in.readArrayList(Screen.class.getClassLoader());
        _settings = in.readParcelable(Settings.class.getClassLoader());
    }

    public void restart() {
        _activeScreen = 0;
        for(Screen s : _screens) {
            s.initialize();
        }
    }
}

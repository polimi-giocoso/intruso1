package polimi.it.trovalintruso.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.Duration;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import polimi.it.trovalintruso.App;
import polimi.it.trovalintruso.R;

/**
 * Created by poool on 09/02/15.
 */
public class Game implements Parcelable, Serializable {

    private final static long serialVersionUID = 2L;

    private Settings _settings;
    private ArrayList<Screen> _screens;
    private int _activeScreen;

    public Game(Settings settings) {
        _settings = settings;
    }

    public Settings getSettings() {
        return _settings;
    }

    public ArrayList<Screen> getScreens() {
        return _screens;
    }

    public String getGameTime(Context context) {
        Duration i = _screens.get(0).getScreenTime().toDuration();
        for(int x = 1; x < _screens.size(); x++) {
            i = i.plus(_screens.get(x).getScreenTime().toDuration());
        }
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

    //methods

    public void initialize() {
        //create the screens
        _screens = new ArrayList<Screen>();
        for(int i = 0; i < _settings.getNumOfScreens(); i++) {

            Screen screen = new Screen();
            _screens.add(screen);
        }
        fillScreens();
        _activeScreen = 0;
    }

    public void initializeMultiplayerSession(boolean guest) {
        int start;
        int startOther;
        if(!guest) {
            start = 1;
            startOther = 0;
        }
        else {
            start = 0;
            startOther = 1;
        }
        for(int i = start; i < _screens.size(); i+=2) {
            _screens.get(i).setYourTurn(false);
        }
        for(int i = startOther; i < _screens.size(); i+=2) {
            _screens.get(i).setDeviceName(App.gameHelper.getDeviceName());
        }
    }

    public Boolean goToNextScreen() {
        if(_activeScreen < _settings.getNumOfScreens() - 1) {
            _activeScreen++;
            return true;
        }
        else
            return false;
    }

    private void fillScreens() {
        Random rand = new Random();
        if(_settings.getCategory().getRandom()) {
            List<Category> categories;
            if(_settings.getNumOfObjects() == 4)
                categories = App.getCategoryManager().getCategoryList4();
            else
                categories = App.getCategoryManager().getCategoryList6();
            //Map<Integer, List<Integer>> used = new HashMap();
            Map<Integer, List<Integer>> free = new HashMap();
            for(int i = 0; i < categories.size(); i++) {
                free.put(i, new ArrayList());
                for(int j = 0; j < categories.get(i).getGroups().size(); j++) {
                    free.get(i).add(j);
                }
            }
            for(int i = 0; i < _settings.getNumOfScreens(); i++) {
                int max1 = categories.size() - 1;
                int min1 = 1;
                int randomCat = rand.nextInt((max1 - min1) + 1) + min1;
                while(free.get(randomCat).size() == 0) {
                    randomCat = rand.nextInt((max1 - min1) + 1) + min1;
                }
                Category category = categories.get(randomCat);
                List<CategoryGroup> groups = category.getGroups();
                int max = free.get(randomCat).size() - 1;
                int min = 0;
                int random = max == 0 ? 0 : rand.nextInt((max - min) + 1) + min;
                //while(used.get(randomCat).contains(random))
                    //random = rand.nextInt((max - min) + 1) + min;
                Integer groupIndex = free.get(randomCat).get(random);
                //used.get(randomCat).add(groupIndex);
                free.get(randomCat).remove(random);
                CategoryGroup cg = groups.get(groupIndex);
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

    public String CreateGameDescription(Context context) {
        String body = "";
        int level = 1;
        if(App.game.getSettings().singlePlayer())
            body += context.getString(R.string.game_single_player);
        else
            body += context.getString(R.string.game_multi_player);
        body += "\n";
        body += context.getString(R.string.category);
        body += " ";
        body += App.game.getSettings().getCategory().getName();
        body += "\n";
        body += context.getString(R.string.num_of_screens);
        body += " ";
        body += App.game.getSettings().getNumOfScreens();
        body += "\n";
        body += context.getString(R.string.num_of_objects);
        body += " ";
        body += App.game.getSettings().getNumOfObjects();
        body += "\n";
        body += "\n";
        body += "\n";
        for(Screen s : App.game.getScreens()) {
            body += context.getString(R.string.level) + " " + level;
            body += "\n";
            body += "   " + context.getString(R.string.errors) + " " + s.getErrors();
            body += "\n";
            body += "   " + context.getString(R.string.time) + " " + s.getParsedScreenTime(context);
            if(!App.game.getSettings().singlePlayer()) {
                body += "\n";
                body += "   " + context.getString(R.string.device) + " " + s.getDeviceName();
            }
            body += "\n";
            body += "\n";
            level++;
        }
        body += "\n";
        body += "\n";
        body += context.getString(R.string.game_time) + " " + App.game.getGameTime(context);
        return body;
    }

    public void restart() {
        _activeScreen = 0;
        for(Screen s : _screens) {
            s.init();
        }
    }

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

    /*private void fillScreensOld() {
        Random rand = new Random();
        if(_settings.getCategory().getRandom()) {
            List<Category> categories;
            if(_settings.getNumOfObjects() == 4)
                categories = App.getCategoryManager().getCategoryList4();
            else
                categories = App.getCategoryManager().getCategoryList6();
            Map<Integer, List<Integer>> used = new HashMap();
            for(int i = 0; i < _settings.getNumOfScreens(); i++) {
                int max1 = categories.size() - 1;
                int min1 = 1; //at position 0 there is random category
                int randomCat = rand.nextInt((max1 - min1) + 1) + min1;
                if(!used.keySet().contains(randomCat))
                    used.put(randomCat, new ArrayList());
                Category category = categories.get(randomCat);
                List<CategoryGroup> groups = category.getGroups();
                int max = groups.size() - 1;
                int min = 0;
                int random = rand.nextInt((max - min) + 1) + min;
                while(used.get(randomCat).contains(random))
                    random = rand.nextInt((max - min) + 1) + min;
                used.get(randomCat).add(random);
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
    }*/
}

package polimi.it.trovalintruso.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by poool on 24/02/15.
 */
public class Category implements Serializable {

    private final static long serialVersionUID = 8L;

    private ArrayList<CategoryGroup> _groups;
    private String _name;
    private boolean _random;

    public String getName() {
        return _name;
    }

    public boolean getRandom(){
        return _random;
    }

    public Category(JSONObject definition) throws JSONException {
        _random = false;
        _groups = new ArrayList<CategoryGroup>();
        if(!definition.isNull("name"))
            _name = definition.getString("name");
        if(!definition.isNull("groups")) {
            JSONArray groups = definition.getJSONArray("groups");
            for(int i = 0; i < groups.length(); i++) {
                JSONObject group = groups.getJSONObject(i);
                _groups.add(new CategoryGroup(group));
            }
        }
        else
            _random = true;
    }

    public ArrayList<CategoryGroup> getGroups() {
        return _groups;
    }
}

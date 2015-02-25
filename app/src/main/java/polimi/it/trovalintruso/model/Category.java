package polimi.it.trovalintruso.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by poool on 24/02/15.
 */
public class Category {

    private ArrayList<CategoryGroup> _groups;
    private String _name;

    public String getName() {
        return _name;
    }

    public Category(JSONObject definition) throws JSONException {
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
    }

    public ArrayList<CategoryGroup> getGroups() {
        return _groups;
    }
}
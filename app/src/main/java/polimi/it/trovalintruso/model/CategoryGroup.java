package polimi.it.trovalintruso.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by poool on 24/02/15.
 */
public class CategoryGroup {

    private String _target;
    private String[] _others;

    public CategoryGroup(JSONObject json) throws JSONException {
        if(!json.isNull("target"))
            _target = json.getString("target");
        if(!json.isNull("others")) {
            JSONArray arr = json.getJSONArray("others");
            _others = new String[arr.length()];
            for(int i = 0; i < arr.length(); i++) {
                _others[i] = arr.optString(i);
            }
        }
    }

    public String getTarget() {
        return _target;
    }

    public String[] getOthers() {
        return _others;
    }
}

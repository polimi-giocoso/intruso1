package polimi.it.trovalintruso.helpers;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import polimi.it.trovalintruso.R;
import polimi.it.trovalintruso.model.Category;

/**
 * Created by Paolo on 10/02/2015.
 */
public class CategoryHelper {

    private ArrayList<Category> _categoryList4;
    private ArrayList<Category> _categoryList6;

    public CategoryHelper(Context context) {
        _categoryList4 = new ArrayList<Category>();
        _categoryList6 = new ArrayList<>();
        try {
            String categories = readCategories4File(context);
            if(categories != null) {
                JSONArray arr = new JSONArray(categories);
                for(int i = 0; i < arr.length(); i++) {
                    _categoryList4.add(new Category(arr.getJSONObject(i)));
                }
            }
            categories = readCategories6File(context);
            if(categories != null) {
                JSONArray arr = new JSONArray(categories);
                for(int i = 0; i < arr.length(); i++) {
                    _categoryList6.add(new Category(arr.getJSONObject(i)));
                }
            }
        }
        catch (JSONException e) {

        }
    }

    public ArrayList<Category> getCategoryList4() {
        return _categoryList4;
    }

    public ArrayList<Category> getCategoryList6() {
        return _categoryList6;
    }

    private String readCategories4File(Context ctx)
    {
        InputStream inputStream = ctx.getResources().openRawResource(R.raw.categories4);
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

    private String readCategories6File(Context ctx)
    {
        InputStream inputStream = ctx.getResources().openRawResource(R.raw.categories6);
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

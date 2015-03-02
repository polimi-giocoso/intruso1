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
public class CategoryManager {

    private ArrayList<Category> _categoryList;

    public CategoryManager(Context context) {
        _categoryList = new ArrayList<Category>();
        try {
            String categories = readCategoriesFile(context);
            if(categories != null) {
                JSONArray arr = new JSONArray(categories);
                for(int i = 0; i < arr.length(); i++) {
                    _categoryList.add(new Category(arr.getJSONObject(i)));
                }
            }
        }
        catch (JSONException e) {

        }
    }

    public ArrayList<Category> getCategoryList() {
        return _categoryList;
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

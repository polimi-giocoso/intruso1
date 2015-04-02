package polimi.it.trovalintruso.helpers;

import android.graphics.Bitmap;
import android.util.LruCache;

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by poool on 01/04/15.
 */
public class ImageCacheHelper {

    Set<SoftReference<Bitmap>> mReusableBitmaps;
    private LruCache<String, Bitmap> mMemoryCache;

    public ImageCacheHelper() {

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mReusableBitmaps =
                Collections.synchronizedSet(new HashSet<SoftReference<Bitmap>>());

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }

            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);
            }
        };

    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }
}

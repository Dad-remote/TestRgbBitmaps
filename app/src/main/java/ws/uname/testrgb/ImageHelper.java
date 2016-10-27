package ws.uname.testrgb;

import android.app.ActivityManager;
import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.MemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class ImageHelper {

    private static final int MEGABYTE = 1024 * 1024;
    private static final float AVAILABLE_MEMORY_PERCENT = 1 / 8f;

    public static ImageLoaderConfiguration getImageLoaderConfig(Context context) {
        return new ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(getImageDefaultOptions())
                .memoryCache(newMemoryCache(context))
                .diskCache((new UnlimitedDiscCache(context.getCacheDir())))
                .writeDebugLogs()
                .build();
    }

    private static DisplayImageOptions getImageDefaultOptions() {
        return new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300))
                .resetViewBeforeLoading(true)
                .build();
    }

    private static MemoryCache newMemoryCache(Context context) {
        int memoryClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        int memoryCacheSize = (int) (MEGABYTE * memoryClass * AVAILABLE_MEMORY_PERCENT);
        return new UsingFreqLimitedMemoryCache(memoryCacheSize);
    }
}

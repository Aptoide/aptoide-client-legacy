package com.aptoide.amethyst;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.module.GlideModule;

import java.io.File;

/**
 * Created by rmateus on 08/06/15.
 */
public class AptoideGlideModule implements GlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        builder.setDiskCache(new DiskCache.Factory() {
            @Override
            public DiskCache build() {
                File pathIcons = new File(Configuration.PATH_CACHE_ICONS);
                pathIcons.mkdirs();
                return DiskLruCacheWrapper.get(pathIcons, DEFAULT_DISK_CACHE_SIZE);
            }
        });

        final MemorySizeCalculator calculator = new MemorySizeCalculator(context);
        final int defaultMemoryCacheSize = calculator.getMemoryCacheSize();
        builder.setMemoryCache(new LruResourceCache(defaultMemoryCacheSize / 2));
        final int defaultBitmapPoolSize = calculator.getBitmapPoolSize();
        builder.setBitmapPool(new LruBitmapPool(defaultBitmapPoolSize / 2));
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
    }
}

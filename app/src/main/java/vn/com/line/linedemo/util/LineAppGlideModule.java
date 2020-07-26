package vn.com.line.linedemo.util;

import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

import okhttp3.OkHttpClient;


@GlideModule
public class LineAppGlideModule extends AppGlideModule {


//    @Override
//    public void applyOptions(Context context, GlideBuilder builder) {
//        int memoryCacheSizeBytes = 1024 * 1024 * 20; // 20mb
//        builder.setMemoryCache(new LruResourceCache(memoryCacheSizeBytes));
//        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, memoryCacheSizeBytes));
//        builder.setDefaultRequestOptions(requestOptions());
//        builder.build(context);
//    }

//    private static RequestOptions requestOptions(){
//        return new RequestOptions()
//                .signature(new ObjectKey(
//                        System.currentTimeMillis() / (24 * 60 * 60 * 1000)))
//                .override(600, 200)
//                .centerCrop()
//                .encodeFormat(Bitmap.CompressFormat.PNG)
//                .encodeQuality(100)
//                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
//                .format(PREFER_ARGB_8888)
//                .skipMemoryCache(false);
//    }
}

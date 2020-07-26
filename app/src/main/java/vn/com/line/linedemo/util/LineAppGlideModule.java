package vn.com.line.linedemo.util;

import android.content.Context;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Response;


@GlideModule
public class LineAppGlideModule extends AppGlideModule {

    private OkHttpClient mOkHttpClient;


//    @Override
//    public void applyOptions(Context context, GlideBuilder builder) {
//        int memoryCacheSizeBytes = 1024 * 1024 * 20; // 20mb
//        builder.setMemoryCache(new LruResourceCache(memoryCacheSizeBytes));
//        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, memoryCacheSizeBytes));
//        builder.setDefaultRequestOptions(requestOptions());
//        builder.build(context);
//    }

    @Override
    public void registerComponents(@NotNull Context context, Glide glide, @NotNull Registry registry) {
        mOkHttpClient = new OkHttpClient();

        final ProgressListener progressListener = (bytesRead, contentLength, done) -> {
            int progress = (int) ((100 * bytesRead) / contentLength);
            // Enable if you want to see the progress with logcat
            Log.v("LOG_TAG", "Progress: " + progress + "%");
            if (done) {
                Log.i("LOG_TAG", "Done loading");
            }
        };

        OkHttpClient.Builder builder;
        if (mOkHttpClient != null) {
            builder = mOkHttpClient.newBuilder();
        } else {
            builder = new OkHttpClient.Builder();
        }
        builder.addInterceptor(chain -> {
            Response originalResponse = chain.proceed(chain.request());
            return originalResponse.newBuilder()
                    .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                    .build();
        });
        glide.getRegistry().replace(GlideUrl.class, InputStream.class,
                new OkHttpUrlLoader.Factory(builder.build()));
    }

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

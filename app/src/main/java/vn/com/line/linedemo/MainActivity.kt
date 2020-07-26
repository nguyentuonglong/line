package vn.com.line.linedemo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import vn.com.line.linedemo.databinding.ActivityMainBinding
import vn.com.line.linedemo.util.GlideApp
import vn.com.line.linedemo.util.ProgressListener
import vn.com.line.linedemo.util.ProgressResponseBody
import java.io.InputStream


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        init()
        GlideApp.with(this)
            .load("https://i.imgur.com/mYBXl6X.jpg")
            // Disabling cache to see download progress with every app load
            // You may want to enable caching again in production
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(binding.ivImage)
    }

    private fun init() {
        val mOkHttpClient = OkHttpClient()
        val progressListener =
            object : ProgressListener {
                override fun update(bytesRead: Long, contentLength: Long, done: Boolean) {
                    val progress = (100 * bytesRead / contentLength).toInt()
                    // Enable if you want to see the progress with logcat
                    Log.v("LOG_TAG", "Progress: $progress%")
                    if (done) {
                        Log.i("LOG_TAG", "Done loading")
                    }
                }
            }

        val builder: OkHttpClient.Builder
        builder = mOkHttpClient.newBuilder()
        builder.addInterceptor(Interceptor { chain: Interceptor.Chain ->
            val originalResponse = chain.proceed(chain.request())
            originalResponse.newBuilder()
                .body(ProgressResponseBody(originalResponse.body, progressListener))
                .build()
        })

        GlideApp.get(this).registry.replace(
            GlideUrl::class.java,
            InputStream::class.java,
            OkHttpUrlLoader.Factory(builder.build())
        )
    }
}
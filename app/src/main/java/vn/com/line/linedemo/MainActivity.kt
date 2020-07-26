package vn.com.line.linedemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.load.engine.DiskCacheStrategy
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import vn.com.line.linedemo.databinding.ActivityMainBinding
import vn.com.line.linedemo.network.ApiService
import vn.com.line.linedemo.util.GlideApp
import vn.com.line.linedemo.util.ProgressListener
import vn.com.line.linedemo.util.ProgressResponseBody
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

//        val progressListener: ProgressListener = object : ProgressListener {
//            override fun update(bytesRead: Long, contentLength: Long, done: Boolean) {
//                val progress = (100 * bytesRead / contentLength).toInt()
//
//                // Enable if you want to see the progress with logcat
//                // Log.v(LOG_TAG, "Progress: " + progress + "%");
//                binding.progressBar.progress = progress
//                if (done) {
//                    Timber.d("Done loading")
//                }
//            }
//        }
//
//        ApiService.getOkHttpClient().toMutableList().add(object : Interceptor {
//            @Throws(IOException::class)
//            override fun intercept(chain: Interceptor.Chain): Response {
//                val originalResponse: Response = chain.proceed(chain.request())
//                return originalResponse.newBuilder()
//                    .body(ProgressResponseBody(originalResponse.body, progressListener))
//                    .build()
//            }
//        })
//        Thread.sleep(3000)
        GlideApp.with(this)
            .load("https://i.imgur.com/mYBXl6X.jpg")
            // Disabling cache to see download progress with every app load
            // You may want to enable caching again in production
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(binding.ivImage)
    }
}
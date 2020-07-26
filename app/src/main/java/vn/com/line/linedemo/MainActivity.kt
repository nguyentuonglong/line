package vn.com.line.linedemo

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.load.engine.DiskCacheStrategy
import io.reactivex.Emitter
import okhttp3.Call
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okio.BufferedSink
import okio.IOException
import okio.Okio
import vn.com.line.linedemo.databinding.ActivityMainBinding
import vn.com.line.linedemo.network.model.Movie
import vn.com.line.linedemo.util.GlideApp
import vn.com.line.linedemo.util.ProgressListener
import vn.com.line.linedemo.util.ProgressResponseBody


class MainActivity : BaseActivity(), ProgressListener {

    private lateinit var binding: ActivityMainBinding
    private val movieData by lazy { genDummyData() }
    private var indexOfImage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        init()
    }

    private fun loadImage(url: String) {
        GlideApp.with(this)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(binding.ivImage)
    }

    private fun init() {
        setProgressListener(this)
        binding.tvTitle.text = movieData.title
        binding.ivImage.setOnClickListener {
            if (indexOfImage < movieData.image?.size ?: 0) {
                movieData.image?.get(indexOfImage)?.let { url -> loadImage(url) }
                indexOfImage++
            } else {
                Toast.makeText(this, getString(R.string.out_of_image), Toast.LENGTH_SHORT).show()
            }
        }
        loadFirstImage()
    }

    private fun loadFirstImage() {
        movieData.image?.get(indexOfImage)?.let { loadImage(it) }
        indexOfImage++
    }

    private fun genDummyData(): Movie {
        val listOfImage = listOf(
            "http://movie.phinf.naver.net/20151127_272/1448585271749MCMVs_JPEG/movie_image.jpg?type=m665_443_2",
            "http://movie.phinf.naver.net/20151127_84/1448585272016tiBsF_JPEG/movie_image.jpg?type=m665_443_2",
            "http://movie.phinf.naver.net/20151125_36/1448434523214fPmj0_JPEG/movie_image.jpg?type=m665_443_2"
        )
        return Movie(title = "Civil War", image = listOfImage)
    }

    override fun update(bytesRead: Long, contentLength: Long, done: Boolean) {
        val progress = (100 * bytesRead / contentLength).toInt()
        // Enable if you want to see the progress with logcat
        Log.v("LOG_TAG", "Progress: $progress%")
        runOnUiThread {
            binding.progressContainer.isVisible = !done
            val downloaded = String.format(getString(R.string.downloaded_holder), bytesRead / 1000)
            binding.tvDownloaded.text = downloaded
        }
    }
}
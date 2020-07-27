package vn.com.line.linedemo

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vn.com.line.linedemo.databinding.ActivityMainBinding
import vn.com.line.linedemo.network.model.Movie
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private val movieData by lazy { genDummyData() }
    private var indexOfImage = 0
    private val uiScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        init()
    }

    private fun loadImage(url: String) {
        uiScope.launch {
            downloadImage(url)
        }
    }

    private fun init() {
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


    private suspend fun downloadImage(imageUrl: String?) {
        withContext(Dispatchers.Default) {
            withContext(Dispatchers.Main) {
                binding.progressContainer.isVisible = true
            }
            var inputStream: InputStream? = null
            var os: OutputStream? = null
            var con: HttpURLConnection? = null
            val length: Int
            try {
                val url = URL(imageUrl)
                con = url.openConnection() as? HttpURLConnection
                con?.connect()
                if (con?.responseCode != HttpURLConnection.HTTP_OK) {
                    return@withContext
                }
                length = con.contentLength
                inputStream = con.inputStream
                val randomId = UUID.randomUUID().toString()
                val name = "$randomId.png"
                val path =
                    this@MainActivity.getExternalFilesDir(null).toString() + File.separator + name
                os = FileOutputStream(path)
                val data = ByteArray(4096)
                var total: Long = 0
                var count: Int
                while (inputStream.read(data).also { count = it } != -1) {
                    total += count.toLong()
                    if (length > 0) {
                        withContext(Dispatchers.Main) {
                            val downloaded = String.format(
                                getString(R.string.downloaded_holder),
                                total.toInt() / 1000
                            )
                            binding.tvDownloaded.text = downloaded
                        }
                    }
                    os.write(data, 0, count)
                }
                withContext(Dispatchers.Main) {
                    binding.progressContainer.isVisible = false
                    val b = BitmapFactory.decodeFile(path)
                    binding.ivImage.setImageBitmap(b)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    os?.close()
                    inputStream?.close()
                } catch (ioe: IOException) {
                }
                con?.disconnect()
            }
        }
    }
}
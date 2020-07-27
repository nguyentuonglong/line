package vn.com.line.linedemo

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import vn.com.line.linedemo.base.BaseActivity
import vn.com.line.linedemo.databinding.ActivityMainBinding
import vn.com.line.linedemo.network.model.Movie
import vn.com.line.linedemo.viewmodel.DownloadImageViewModel


class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private val movieData by lazy { genDummyData() }
    private var indexOfImage = 0
    private val viewModel by viewModels<DownloadImageViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        init()
        setUpViewModels()
        loadFirstImage()
    }

    private fun loadImage(url: String) {
        viewModel.downloadImage(url)
    }

    private fun setUpViewModels() {
        viewModel.donwloadState.observe(this, Observer { state ->
            binding.progressContainer.isVisible =
                state == DownloadImageViewModel.DownloadState.STATE_STARTED
        })

        viewModel.progressDownload.observe(this, Observer { downloadedKb ->
            val downloadedAsString =
                String.format(getString(R.string.downloaded_holder), downloadedKb)
            binding.tvDownloaded.text = downloadedAsString
        })

        viewModel.imagePath.observe(this, Observer { path ->
            viewModel.loadImageFromPath(path)
        })

        viewModel.bitmapFromPath.observe(this, Observer { bitmap ->
            binding.ivImage.setImageBitmap(bitmap)
        })
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
}
package vn.com.line.linedemo.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import vn.com.line.linedemo.LineApp
import vn.com.line.linedemo.network.model.Movie
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class DownloadImageViewModel(application: Application) : AndroidViewModel(application) {

    val downloadState = MutableLiveData<DownloadState>()
    val progressDownload = MutableLiveData<Int>()
    val imageBitmap = MutableLiveData<Bitmap>()
    val movieTitle = MutableLiveData<String>()
    val isOutOfImages = MutableLiveData<Boolean>()
    private var movieData: Movie? = null
    private var indexOfImage = 0
    private var isDownloaded = false

    enum class DownloadState {
        STATE_STARTED, STATE_FAILED, STATE_ENDED
    }

    private fun downloadImage(imageUrl: String) {
        Completable.fromAction {
            downloadState.postValue(DownloadState.STATE_STARTED)
            var inputStream: InputStream? = null
            var os: OutputStream? = null
            var con: HttpURLConnection? = null
            val length: Int
            try {
                val url = URL(imageUrl)
                con = url.openConnection() as? HttpURLConnection
                con?.connect()
                if (con?.responseCode != HttpURLConnection.HTTP_OK) {
                    downloadState.postValue(DownloadState.STATE_FAILED)
                    return@fromAction
                }
                length = con.contentLength
                inputStream = con.inputStream
                val randomId = UUID.randomUUID().toString()
                val name = "$randomId.png"
                val path =
                    getApplication<LineApp>().getDir(DIRECTORY_OUTPUTS, Context.MODE_PRIVATE)
                        .toString() + File.separator + name
                os = FileOutputStream(path)
                val data = ByteArray(4096)
                var total: Long = 0
                var count: Int
                while (inputStream.read(data).also { count = it } != -1) {
                    total += count.toLong()
                    if (length > 0) {
                        progressDownload.postValue(total.toInt() / 1000)
                    }
                    os.write(data, 0, count)
                }
                val file = File(path)
                if (file.exists()) {
                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    imageBitmap.postValue(bitmap)
                }
                downloadState.postValue(DownloadState.STATE_ENDED)
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
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()

    }

    private fun loadLocalImage() {
        Completable.fromAction {
            movieData?.image?.let { list ->
                if (indexOfImage < list.size) {
                    val path = list[indexOfImage] ?: ""
                    if (path.isNotEmpty()) {
                        val file = File(path)
                        if (file.exists()) {
                            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                            indexOfImage++
                            imageBitmap.postValue(bitmap)
                        }
                    }
                } else {
                    isOutOfImages.postValue(true)
                }
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    private fun loadRemoteImage() {
        Completable.fromAction {
            movieData?.image?.let { list ->
                if (indexOfImage < list.size) {
                    val url = movieData?.image?.get(indexOfImage) ?: ""
                    if (url.isNotEmpty()) {
                        downloadImage(url)
                        indexOfImage++
                    }
                } else {
                    isOutOfImages.postValue(true)
                }
            }

        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    fun checkLocalImageFolder() {
        val fileDirectory =
            File(
                getApplication<LineApp>().getDir(DIRECTORY_OUTPUTS, Context.MODE_PRIVATE)
                    .toString()
            )
        val dirFiles = fileDirectory.listFiles()
        if (dirFiles != null && dirFiles.isNotEmpty()) {
            val listOfPath = dirFiles.map {
                it.absolutePath
            }
            movieData = Movie(title = "Civil War", image = listOfPath)
            movieTitle.postValue(movieData?.title)
        } else {
            genDummyData()
        }
        isDownloaded = dirFiles != null && dirFiles.isNotEmpty()
        fetchNextImage()
    }

    fun fetchNextImage() {
        if (isDownloaded) loadLocalImage() else loadRemoteImage()
    }

    private fun genDummyData() {
        val listOfImage = listOf(
            "http://movie.phinf.naver.net/20151127_272/1448585271749MCMVs_JPEG/movie_image.jpg?type=m665_443_2",
            "http://movie.phinf.naver.net/20151127_84/1448585272016tiBsF_JPEG/movie_image.jpg?type=m665_443_2",
            "http://movie.phinf.naver.net/20151125_36/1448434523214fPmj0_JPEG/movie_image.jpg?type=m665_443_2"
        )
        movieData = Movie(title = "Civil War", image = listOfImage)
        movieTitle.postValue(movieData?.title)
    }

    companion object {
        private const val DIRECTORY_OUTPUTS = "outputs"
    }
}
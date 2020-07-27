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
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class DownloadImageViewModel(application: Application) : AndroidViewModel(application) {

    val donwloadState = MutableLiveData<DownloadState>()
    val progressDownload = MutableLiveData<Int>()
    val imagePath = MutableLiveData<String>()
    val bitmapFromPath = MutableLiveData<Bitmap>()

    //TO save local
    private val bitmapStore = MutableLiveData<List<Bitmap>>()

    enum class DownloadState {
        STATE_STARTED, STATE_FAILED, STATE_ENDED
    }

    fun downloadImage(imageUrl: String) {
        Completable.fromAction {
            donwloadState.postValue(DownloadState.STATE_STARTED)
            var inputStream: InputStream? = null
            var os: OutputStream? = null
            var con: HttpURLConnection? = null
            val length: Int
            try {
                val url = URL(imageUrl)
                con = url.openConnection() as? HttpURLConnection
                con?.connect()
                if (con?.responseCode != HttpURLConnection.HTTP_OK) {
                    donwloadState.postValue(DownloadState.STATE_FAILED)
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
                imagePath.postValue(path)
                donwloadState.postValue(DownloadState.STATE_ENDED)
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

    fun loadImageFromPath(path: String) {
        Completable.fromAction {
            val file = File(path)
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                bitmapFromPath.postValue(bitmap)
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    companion object {
        private const val DIRECTORY_OUTPUTS = "outputs"
    }


}
package vn.com.line.linedemo.util

import android.app.ProgressDialog
import android.content.Context
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import vn.com.line.linedemo.R
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class DownloadActivity : AppCompatActivity() {
    private var pd: ProgressDialog? = null
    private var iv: ImageView? = null
    private var di: DownloadImage? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        iv = findViewById(R.id.ivImage)
        pd = ProgressDialog(this@DownloadActivity)
        pd!!.setMessage("Downloading image, please wait ...")
        pd!!.isIndeterminate = true
        pd!!.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        pd!!.setCancelable(false)
        pd!!.setProgressNumberFormat("%1d KB/%2d KB")
        di = DownloadImage(this@DownloadActivity)
        di!!.execute("https://images.unsplash.com/photo-1536998533868-95cde0d71742?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=69a455127db97a5cc05e2d3c9c9ef245&auto=format&fit=crop&w=4000&q=80")
        pd!!.setOnCancelListener { di!!.cancel(true) }
    }

    private open inner class DownloadImage(private val c: Context) :
        AsyncTask<String?, Int?, String?>() {
        override fun doInBackground(vararg sUrl: String?): String? {
            var `is`: InputStream? = null
            var os: OutputStream? = null
            var con: HttpURLConnection? = null
            val length: Int
            try {
                val url = URL(sUrl[0])
                con = url.openConnection() as HttpURLConnection
                con.connect()
                if (con!!.responseCode != HttpURLConnection.HTTP_OK) {
                    return "HTTP CODE: " + con.responseCode + " " + con.responseMessage
                }
                length = con.contentLength
                pd!!.max = length / 1000
                `is` = con.inputStream
                os = FileOutputStream(
                    Environment.getExternalStorageDirectory()
                        .toString() + File.separator + "a-computer-engineer.jpg"
                )
                val data = ByteArray(4096)
                var total: Long = 0
                var count: Int
                while (`is`.read(data).also { count = it } != -1) {
                    if (isCancelled) {
                        `is`.close()
                        return null
                    }
                    total += count.toLong()
                    if (length > 0) {
                        publishProgress(total.toInt())
                    }
                    os.write(data, 0, count)
                }
            } catch (e: Exception) {
                return e.toString()
            } finally {
                try {
                    os?.close()
                    `is`?.close()
                } catch (ioe: IOException) {
                }
                con?.disconnect()
            }
            return null
        }

        override fun onPreExecute() {
            super.onPreExecute()
            pd!!.show()
        }

        override fun onProgressUpdate(vararg progress: Int?) {
            super.onProgressUpdate(*progress)
            pd!!.isIndeterminate = false
            pd!!.progress = progress[0]!! / 1000
        }

        override fun onPostExecute(result: String?) {
            pd!!.dismiss()
            if (result != null) {
                Toast.makeText(c, "Download error: $result", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(c, "Image downloaded successfully!", Toast.LENGTH_SHORT).show()
                val b = BitmapFactory.decodeFile(
                    Environment.getExternalStorageDirectory()
                        .toString() + File.separator + "a-computer-engineer.jpg"
                )
                iv!!.setImageBitmap(b)
            }
        }

    }
}
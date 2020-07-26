package vn.com.line.linedemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import vn.com.line.linedemo.util.GlideApp
import vn.com.line.linedemo.util.ProgressListener
import vn.com.line.linedemo.util.ProgressResponseBody
import java.io.InputStream

open class BaseActivity : AppCompatActivity() {

    private var progressListener: ProgressListener? = null

    protected fun setProgressListener(listener: ProgressListener) {
        this.progressListener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        val mOkHttpClient = OkHttpClient()
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
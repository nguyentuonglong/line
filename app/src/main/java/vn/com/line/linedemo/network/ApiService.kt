package vn.com.line.linedemo.network


import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


interface ApiService {


//    @POST(API_SEND_QR_CODE)
//    fun postQrCode(
//        @Body request: QrRequest
//    ): Single<BaseResponse>


    companion object {

        private const val DEFAULT_URL = "https://enduser.sunsoftware.vn/"

        private lateinit var apiService: ApiService
        private lateinit var client: OkHttpClient

        fun getInstance(): ApiService = apiService

        fun getOkHttpClient() = client

        fun create() {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            client = OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(DEFAULT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build()
            apiService = retrofit.create(ApiService::class.java)
        }
    }
}
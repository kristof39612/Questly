package hu.bme.aut.szoftverarch.questly.data.networking

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import hu.bme.aut.szoftverarch.questly.data.utils.gcf

object RetrofitInstance {
    private const val BASE_URL = "https://questly.lovacsi.me"
    val api: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ApiService::class.java)
    }

    fun getAuthorizedApi(context: Context): ApiService {
        val sharedPreferences = context.getSharedPreferences("UserData", Context.MODE_PRIVATE)

        // Create an OkHttpClient with the AuthInterceptor
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(sharedPreferences))
            .build()

        // Create the Retrofit instance and return the API service
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)  // Add the OkHttp client with the interceptor
            .addConverterFactory(gcf())
            .build()

        return retrofit.create(ApiService::class.java)
    }

}
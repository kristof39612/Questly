package hu.bme.aut.szoftverarch.questly.data.networking

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.POST
import okhttp3.Interceptor
import android.content.SharedPreferences
import hu.bme.aut.szoftverarch.questly.data.TaskPoint
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @POST("/auth/login")
    suspend fun login(@Body request: LoginRequest) : retrofit2.Response<LoginResponse>

    @POST("/auth/register")
    suspend fun register(@Body request: RegisterRequest) : retrofit2.Response<RegisterResponse>

    @GET("/api/v1/test")
    suspend fun tokenTest(/*@Body request: TokenTestRequest*/) : retrofit2.Response<TokenTestResponse>

    @GET("/taskpoint/{id}")
    suspend fun getTaskPointById(@Path("id") id: String): retrofit2.Response<TaskPoint>

    @POST("/taskpoint")
    suspend fun createTaskPoint(@Body taskPoint: TaskPoint): retrofit2.Response<TaskPoint>
}

@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
)

@Serializable
data class LoginResponse(
    val token: String,
)

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
)

@Serializable
data class RegisterResponse(
    val token: String,
    val errorMessage: String
)

@Serializable
data class TokenTestRequest(
    val message: String
)

@Serializable
data class TokenTestResponse(
    val token: String,
    val errorMessage: String
)

class AuthInterceptor(private val sharedPreferences: SharedPreferences) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        // Retrieve the JWT token from SharedPreferences
        val token = sharedPreferences.getString("userToken", null)

        // If the token exists, add it to the request headers
        val newRequest = chain.request().newBuilder().apply {
            token?.let {
                header("Authorization", "Bearer $it")
            }
        }.build()

        return chain.proceed(newRequest)
    }
}

package com.example.univents.core.network

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.Preferences.preferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.http.*
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType

// Функция preferencesDataStore — это property delegate для Context.
// Объявлять надо так (обычно в Network.kt или отдельном файле DataStore.kt):
private val Context.dataStore by preferencesDataStore("auth")

object TokenStore {
    private val KEY = preferencesKey<String>("jwt")
    fun flow(context: Context): Flow<String?> = context.dataStore.data.map { it[KEY] }
    suspend fun save(context: Context, token: String) { context.dataStore.edit { it[KEY] = token } }
    suspend fun clear(context: Context) { context.dataStore.edit { it.remove(KEY) } }
}

class AuthInterceptor(val tokenProvider: () -> String?) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val req = chain.request().newBuilder().apply {
            tokenProvider()?.let { addHeader("Authorization", "Bearer $it") }
        }.build()
        return chain.proceed(req)
    }
}

@SuppressLint("UnsafeOptInUsageError")
@Serializable data class AuthRequest(val email: String, val password: String)
@SuppressLint("UnsafeOptInUsageError")
@Serializable data class AuthResponse(val token: String)
@SuppressLint("UnsafeOptInUsageError")
@Serializable data class MeDto(val email: String, val displayName: String? = null)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class EventItem(
    val id: Long,
    val title: String,
    val description: String? = null,
    val date: String,
    val latitude: Double? = null,
    val longitude: Double? = null
)

interface ApiServiceV1 {
    @POST("/api/v1/auth/register") suspend fun register(@Body req: AuthRequest): AuthResponse
    @POST("/api/v1/auth/login") suspend fun login(@Body req: AuthRequest): AuthResponse
    @GET("/api/v1/me") suspend fun me(): MeDto

    @GET("/api/v1/events")
    suspend fun events(
        @Query("bbox") bbox: String? = null,
        @Query("lat") lat: Double? = null,
        @Query("lng") lng: Double? = null,
        @Query("radius") radius: Double? = null,
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null
    ): List<EventItem>
}

object NetworkModule {
    private val json = Json { ignoreUnknownKeys = true }
    fun retrofit(baseUrl: String, tokenProvider: () -> String?): Retrofit {
        val logging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(AuthInterceptor(tokenProvider))
            .build()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }
}

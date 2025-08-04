package com.example.registrohorasapp.retrofit

import android.content.Context
import com.example.registrohorasapp.utils.TokenManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // URLs configurables - Cambia aquí cuando despliegues en la nube
    private const val BASE_URL_LOCAL = "http://192.168.0.4:8000/api/"
    private const val BASE_URL_PRODUCTION = "https://tu-app.herokuapp.com/api/" // Cambia por tu URL real
    private const val BASE_URL_RAILWAY = "https://web-production-85ac.up.railway.app/api/" // Cambia por tu URL real
    private const val BASE_URL_RENDER = "https://tu-app.onrender.com/api/" // Cambia por tu URL real
    
    // Cambia esta variable para cambiar entre entornos
    private const val CURRENT_ENVIRONMENT = "RAILWAY" // LOCAL, PRODUCTION, RAILWAY, RENDER
    
    private val BASE_URL: String
        get() = when (CURRENT_ENVIRONMENT) {
            "LOCAL" -> BASE_URL_LOCAL
            "PRODUCTION" -> BASE_URL_PRODUCTION
            "RAILWAY" -> BASE_URL_RAILWAY
            "RENDER" -> BASE_URL_RENDER
            else -> BASE_URL_PRODUCTION
        }

    private var tokenManager: TokenManager? = null

    fun init(context: Context) {
        tokenManager = TokenManager(context)
    }

    // Interceptor para agregar el token
    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url.toString()

        // Si la URL contiene 'register' o 'api-token-auth', no mandamos el token
        if (originalUrl.contains("register") || originalUrl.contains("api-token-auth")) {
            chain.proceed(originalRequest)
        } else {
            val token = tokenManager?.getToken()
            val requestBuilder = originalRequest.newBuilder()
            if (!token.isNullOrEmpty()) {
                requestBuilder.addHeader("Authorization", "Token $token")
            }
            val newRequest = requestBuilder.build()
            chain.proceed(newRequest)
        }
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS) // Timeout más largo para conexiones remotas
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
    
    // Función para cambiar la URL del backend dinámicamente
    fun updateBaseUrl(newBaseUrl: String) {
        // Esta función se puede usar para cambiar la URL en tiempo de ejecución
        // si es necesario
    }
    
    // Función para obtener la URL actual (útil para debugging)
    fun getCurrentBaseUrl(): String {
        return BASE_URL
    }
}

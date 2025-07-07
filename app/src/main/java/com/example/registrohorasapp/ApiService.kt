package com.example.registrohorasapp.retrofit
import com.example.registrohorasapp.models.RegistroRequest
import com.example.registrohorasapp.models.RegistroResponse
import com.example.registrohorasapp.models.ResumenResponse
import com.example.registrohorasapp.models.LoginResponse
import retrofit2.Response
import retrofit2.http.*



interface ApiService {

    @GET("registros/")
    suspend fun getRegistros(): List<RegistroResponse>

    @POST("registros/")
    suspend fun crearRegistro(@Body registro: RegistroRequest): RegistroResponse

    @PUT("registros/{id}/")
    suspend fun editarRegistro(@Path("id") id: Int, @Body registro: RegistroRequest): RegistroResponse

    @DELETE("registros/{id}/")
    suspend fun borrarRegistro(@Path("id") id: Int): Response<Unit>

    @GET("registros/resumen-semanal/")
    suspend fun getResumenSemanal(@Query("fecha") fecha: String): ResumenResponse

    // Crear un nuevo usuario (registro)
    @FormUrlEncoded
    @POST("register/")
    suspend fun register(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("email") email: String
    ): Response<Unit>

    // Login y obtención de token
    @FormUrlEncoded
    @POST("api-token-auth/")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): LoginResponse
}

package mx.edu.uttt.planeat.network

import mx.edu.uttt.planeat.models.BandejaReceta
import mx.edu.uttt.planeat.models.Comentario
import mx.edu.uttt.planeat.models.Favorito
import mx.edu.uttt.planeat.models.FechaCalendario
import mx.edu.uttt.planeat.models.Ingrediente
import mx.edu.uttt.planeat.models.Platillo
import mx.edu.uttt.planeat.models.PlatilloPaso
import mx.edu.uttt.planeat.models.Puntuaciones
import mx.edu.uttt.planeat.models.Recomendacion
import mx.edu.uttt.planeat.models.Usuario
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    // USURIOS
    @GET("usuarios/get")
    suspend fun getUsuarios(): List<Usuario>

    @GET("usuarios/{id}")
    suspend fun getUsuarioById(@Path("id") id: Int): Usuario

    @POST("usuarios")
    suspend fun createUsuario(@Body usuario: Usuario): Response<Usuario>

    @PUT("usuarios/{id}")
    suspend fun updateUsuario(@Path("id") id: Int, @Body usuario: Usuario): Usuario

    @DELETE("usuarios/{id}")
    suspend fun deleteUsuario(@Path("id") id: Int)


    //Ingredientes

    @GET("ingredientes")
    suspend fun getIngredientes(): List<Ingrediente>

    @GET("ingredientes/{id}")
    suspend fun getIngredienteById(@Path("id") id: Int): Ingrediente

    @POST("ingredientes")
    suspend fun createIngrediente(@Body ingrediente: Ingrediente): Ingrediente

    @DELETE("ingredientes/{id}")
    suspend fun deleteIngrediente(@Path("id") id: Int)


    //pasos

    @GET("platillosPasos")
    suspend fun getPlatillosPasos(): List<PlatilloPaso>

    @GET("platillosPasos/{id}")
    suspend fun getPlatilloPasoById(@Path("id") id: Int): PlatilloPaso

    @POST("platillosPasos")
    suspend fun createPlatilloPaso(@Body platilloPaso: PlatilloPaso): PlatilloPaso

    @DELETE("platillosPasos/{id}")
    suspend fun deletePlatilloPaso(@Path("id") id: Int)



    //Platillos
    @GET("platillos/ListaPlatillos")
    suspend fun getPlatillos(): List<Platillo>

    @GET("platillos/{id}")
    suspend fun getPlatilloById(@Path("id") id: Int): Platillo




        @Multipart
        @POST("platillos")
        suspend fun uploadPlatillo(
            @Part("Titulo") titulo: RequestBody,
            @Part("Descripcion") descripcion: RequestBody,
            @Part("IdUsuario") idUsuario: RequestBody,
            @Part imagen: MultipartBody.Part
        ): Response<Platillo>

    // Add this method to your ApiService interface
    @POST("platillos")
    suspend fun createPlatillo(@Body platillo: Platillo): Response<Platillo>

    @PUT("platillos/{id}")
    suspend fun updatePlatillo(@Path("id") id: Int, @Body platillo: Platillo): Platillo

    @DELETE("platillos/{id}")
    suspend fun deletePlatillo(@Path("id") id: Int)


    //comentarios


    @GET("comentarios")
    suspend fun getComentarios(): List<Comentario>


    @GET("comentarios/{id}")
    suspend fun getComentarioById(@Path("id") id: Int): Comentario


    @POST("comentarios")
    suspend fun postComentario(@Body comentario: Comentario): Response<Comentario>


    @DELETE("comentarios/{id}")
    suspend fun deleteComentario(@Path("id") id: Int): Response<Unit>

    //favoritos


    @GET("Favoritos")
    suspend fun getFavoritos(): List<Favorito>


    @GET("Favoritos/{id}")
    suspend fun getFavoritoById(@Path("id") id: Int): Favorito


    @POST("Favoritos")
    suspend fun postFavorito(@Body favorito: Favorito): Response<Favorito>


    @DELETE("Favoritos/{id}")
    suspend fun deleteFavorito(@Path("id") id: Int): Response<Unit>

    //calendario

    @POST("FechaCalendarios")
    suspend fun postFecha(@Body fecha: FechaCalendario): FechaCalendario

    @GET("FechaCalendarios")
    suspend fun getFechas(): List<FechaCalendario>


    //bandeja

    @GET("BandejaRecetas")
    suspend fun getBandejaRecetas(): List<BandejaReceta>

    @POST("BandejaRecetas")
    suspend fun guardarBandejaReceta(@Body bandeja: BandejaReceta): Response<Unit>

    @GET("BandejaRecetas/{id}")
    suspend fun getBandejaRecetaById(@Path("id") id: Int): BandejaReceta

    @DELETE("BandejaRecetas/{id}")
    suspend fun deleteBandejaReceta(@Path("id") id: Int)

    @PUT("BandejaRecetas/{id}")
    suspend fun updateBandejaReceta(@Path("id") id: Int, @Body bandejaReceta: BandejaReceta)




    @GET("Recomendaciones")
    suspend fun getRecomendaciones(): List<Recomendacion>

    @GET("Recomendaciones/{id}")
    suspend fun getRecomendacionById(@Path("id") id: Int): Recomendacion

    @POST("Recomendaciones")
    suspend fun agregarRecomendacion(@Body recomendacion: Recomendacion): Response<Unit>

    @PUT("Recomendaciones/{id}")
    suspend fun actualizarRecomendacion(
        @Path("id") id: Int,
        @Body recomendacion: Recomendacion
    ): Response<Unit>

    @DELETE("Recomendaciones/{id}")
    suspend fun eliminarRecomendacion(@Path("id") id: Int): Response<Unit>

    //Puntuaciones

    @GET("Puntuaciones")
    suspend fun getPuntuaciones(): List<Puntuaciones>

    @GET("Puntuaciones/{id}")
    suspend fun getPuntuacionById(@Path("id") id: Int): Puntuaciones

    @POST("Puntuaciones")
    suspend fun agregarPuntuacion(@Body puntuacion: Puntuaciones): Response<Unit>


    @PUT("Puntuaciones/{id}")
    suspend fun actualizarPuntuacion(
        @Path("id") id: Int,
        @Body puntuacion: Puntuaciones
    ): Response<Unit>

    @DELETE("Puntuaciones/{id}")
    suspend fun eliminarPuntuacion(@Path("id") id: Int): Response<Unit>



}
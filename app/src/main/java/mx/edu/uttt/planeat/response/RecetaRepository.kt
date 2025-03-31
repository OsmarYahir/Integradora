package mx.edu.uttt.planeat.response
import mx.edu.uttt.planeat.models.Ingrediente
import mx.edu.uttt.planeat.models.Platillo
import mx.edu.uttt.planeat.models.PlatilloPaso
import mx.edu.uttt.planeat.network.ApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class RecetaRepository(private val api: ApiService) {

    suspend fun uploadPlatillo(
        titulo: String,
        descripcion: String,
        idUsuario: Int,
        fechaCreacion: String,
        imagenFile: File
    ): Platillo {
        // Crear las partes de RequestBody para los campos de texto
        val tituloBody = titulo.toRequestBody("text/plain".toMediaTypeOrNull())
        val descripcionBody = descripcion.toRequestBody("text/plain".toMediaTypeOrNull())
        val idUsuarioBody = idUsuario.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val fechaCreacionBody = fechaCreacion.toRequestBody("text/plain".toMediaTypeOrNull())

        // Crear la parte Multipart para la imagen
        val requestFile = imagenFile.asRequestBody("image/*".toMediaTypeOrNull()) // El tipo de archivo puede ser "image/*" o el tipo específico de tu imagen
        val imagenBody = MultipartBody.Part.createFormData("imagen", imagenFile.name, requestFile)

        // Realizar la llamada a la API
        val response = api.uploadPlatillo(
            titulo = tituloBody,
            descripcion = descripcionBody,
            idUsuario = idUsuarioBody,
            fechaCreacion = fechaCreacionBody,
            imagen = imagenBody
        )

        // Verificar si la respuesta fue exitosa
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Error: Platillo no encontrado")
        } else {
            throw Exception("Error al subir el platillo: ${response.message()}")
        }
    }

    suspend fun createIngrediente(ingrediente: Ingrediente): Ingrediente {
        return api.createIngrediente(ingrediente)
    }

    suspend fun createPlatilloPaso(paso: PlatilloPaso): PlatilloPaso {
        return api.createPlatilloPaso(paso)
    }
}

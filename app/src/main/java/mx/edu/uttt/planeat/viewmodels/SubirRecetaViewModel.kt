package mx.edu.uttt.planeat.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mx.edu.uttt.planeat.models.Platillo
import mx.edu.uttt.planeat.network.ApiClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class SubirRecetaViewModel : ViewModel() {

    fun uploadPlatilloWithImage(
        titulo: String,
        descripcion: String,
        idUsuario: Int,
        imagenFile: File,
        onSuccess: (Platillo) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val tituloBody = titulo.toRequestBody("text/plain".toMediaTypeOrNull())
                val descripcionBody = descripcion.toRequestBody("text/plain".toMediaTypeOrNull())
                val idUsuarioBody = idUsuario.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val fechaBody = obtenerFechaActual().toRequestBody("text/plain".toMediaTypeOrNull())

                val requestFile = imagenFile.asRequestBody("image/*".toMediaTypeOrNull())
                val imagenPart = MultipartBody.Part.createFormData("imagen", imagenFile.name, requestFile)

                val response: Response<Platillo> = ApiClient.apiService.uploadPlatillo(
                    titulo = tituloBody,
                    descripcion = descripcionBody,
                    idUsuario = idUsuarioBody,
                    imagen = imagenPart,
                    fechaCreacion = fechaBody // asegúrate que el backend lo acepte
                )

                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) }
                        ?: onError("Error: Respuesta vacía del servidor")
                } else {
                    onError("Error: ${response.message()}")
                }

            } catch (e: Exception) {
                onError("Excepción: ${e.localizedMessage}")
            }
        }
    }

    private fun obtenerFechaActual(): String {
        val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formato.format(Date())
    }
}

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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import android.net.Uri

class SubirRecetaViewModel : ViewModel() {

    // Keep this method for Firebase storage if needed
    fun uploadImageToFirebase(
        imageFile: File,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val storage = FirebaseStorage.getInstance()
        val storageRef: StorageReference = storage.reference
        val imageRef = storageRef.child("images/${UUID.randomUUID()}.jpg")

        val uploadTask = imageRef.putFile(Uri.fromFile(imageFile))

        uploadTask.addOnSuccessListener { taskSnapshot ->
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                val imageUrl = uri.toString()
                onSuccess(imageUrl)
            }.addOnFailureListener { exception ->
                onError("Error al obtener la URL de la imagen: ${exception.localizedMessage}")
            }
        }.addOnFailureListener { exception ->
            onError("Error al subir la imagen: ${exception.localizedMessage}")
        }
    }

    // Method to directly upload to your API without Firebase
    fun uploadPlatilloDirectly(
        titulo: String,
        descripcion: String,
        idUsuario: Int,
        imageFile: File,
        onSuccess: (Platillo) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Create RequestBody instances for text data
                val tituloBody = RequestBody.create("text/plain".toMediaTypeOrNull(), titulo)
                val descripcionBody = RequestBody.create("text/plain".toMediaTypeOrNull(), descripcion)
                val idUsuarioBody = RequestBody.create("text/plain".toMediaTypeOrNull(), idUsuario.toString())

                // Create image part - trying with the name the server might be expecting
                val requestFile = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())

                // Use the exact name that your API is expecting in the controller
                // The backend is looking for a file where content disposition filename is not null
                val imagePart = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

                // Add some debugging
                println("Uploading file: ${imageFile.name}, size: ${imageFile.length()}")

                // Call the API
                val response = ApiClient.apiService.uploadPlatillo(
                    titulo = tituloBody,
                    descripcion = descripcionBody,
                    idUsuario = idUsuarioBody,
                    imagen = imagePart
                )

                if (response.isSuccessful) {
                    response.body()?.let(onSuccess) ?: onError("Respuesta vacía del servidor")
                } else {
                    // Try to get the error body for more details
                    val errorBody = response.errorBody()?.string() ?: "Sin detalles de error"
                    onError("Error: ${response.code()} - ${response.message()} - $errorBody")
                }
            } catch (e: Exception) {
                // Log the full exception for debugging
                e.printStackTrace()
                onError("Excepción: ${e.localizedMessage ?: e.toString()}")
            }
        }
    }

    // Only if you want to keep using Firebase and then send URL to API
    // (requires API modification)
//    fun uploadPlatilloWithImageUrl(
//        titulo: String,
//        descripcion: String,
//        idUsuario: Int,
//        imageUrl: String,
//        onSuccess: (Platillo) -> Unit,
//        onError: (String) -> Unit
//    ) {
//        viewModelScope.launch {
//            try {
//                val tituloBody = titulo.toRequestBody("text/plain".toMediaTypeOrNull())
//                val descripcionBody = descripcion.toRequestBody("text/plain".toMediaTypeOrNull())
//                val idUsuarioBody = idUsuario.toString().toRequestBody("text/plain".toMediaTypeOrNull())
//                val imageUrlBody = imageUrl.toRequestBody("text/plain".toMediaTypeOrNull())
//
//                // This would only work if your API is modified to accept URLs
//                val response: Response<Platillo> = ApiClient.apiService.uploadPlatilloWithUrl(
//                    titulo = tituloBody,
//                    descripcion = descripcionBody,
//                    idUsuario = idUsuarioBody,
//                    imagenUrl = imageUrlBody
//                )
//
//                if (response.isSuccessful) {
//                    response.body()?.let(onSuccess) ?: onError("Error: Respuesta vacía del servidor")
//                } else {
//                    onError("Error: ${response.code()} - ${response.message()}")
//                }
//            } catch (e: Exception) {
//                onError("Excepción: ${e.localizedMessage}")
//            }
//        }
//    }

    private fun obtenerFechaActual(): String {
        val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formato.format(Date())
    }
}
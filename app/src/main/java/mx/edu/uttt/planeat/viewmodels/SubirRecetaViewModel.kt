package mx.edu.uttt.planeat.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mx.edu.uttt.planeat.models.Platillo
import mx.edu.uttt.planeat.network.ApiClient
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import android.net.Uri
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SubirRecetaViewModel : ViewModel() {

    // Upload image to Firebase and then save the recipe with URL to API
    fun uploadPlatillo(
        titulo: String,
        descripcion: String,
        idUsuario: Int,
        imageFile: File,
        onSuccess: (Platillo) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // First upload image to Firebase
                val imageUrl = uploadImageToFirebaseAsync(imageFile)

                // Then send the recipe data with image URL to API
                val platillo = uploadPlatilloWithUrlToApi(
                    titulo = titulo,
                    descripcion = descripcion,
                    idUsuario = idUsuario,
                    imageUrl = imageUrl
                )

                onSuccess(platillo)
            } catch (e: Exception) {
                e.printStackTrace()
                onError("Error: ${e.localizedMessage ?: e.toString()}")
            }
        }
    }

    // Coroutine-friendly Firebase upload
    private suspend fun uploadImageToFirebaseAsync(imageFile: File): String = withContext(Dispatchers.IO) {
        val storage = FirebaseStorage.getInstance()
        val storageRef: StorageReference = storage.reference
        val imageRef = storageRef.child("images/${UUID.randomUUID()}.jpg")

        try {
            // Upload the file
            val uploadTask = imageRef.putFile(Uri.fromFile(imageFile)).await()

            // Get download URL
            return@withContext imageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            throw Exception("Error al subir imagen a Firebase: ${e.localizedMessage}")
        }
    }

    // Upload platillo data with image URL to API
    private suspend fun uploadPlatilloWithUrlToApi(
        titulo: String,
        descripcion: String,
        idUsuario: Int,
        imageUrl: String
    ): Platillo {
        return withContext(Dispatchers.IO) {
            try {
                // Clean the URL if needed
                val cleanImageUrl = imageUrl.replace("\\u003d", "=").replace("\\u0026", "&")

                // Create the platillo object
                val platillo = Platillo(
                    IdReceta = 0,
                    Titulo = titulo,
                    Descripcion = descripcion,
                    Imagen = cleanImageUrl,
                    Fecha_Creacion = obtenerFechaActual(),
                    IdUsuario = idUsuario
                )

                // Log the request for debugging
                println("Sending to API: ${platillo.toString()}")

                val response = ApiClient.apiService.createPlatillo(platillo)

                if (response.isSuccessful) {
                    return@withContext response.body() ?: throw Exception("Respuesta vacía del servidor")
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Sin detalles de error"
                    throw Exception("Error API: ${response.code()} - ${response.message()} - $errorBody")
                }
            } catch (e: Exception) {
                throw Exception("Error al guardar receta en API: ${e.localizedMessage}")
            }
        }
    }

    // Keep this for backward compatibility if needed
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

    private fun obtenerFechaActual(): String {
        val formato = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        formato.timeZone = TimeZone.getTimeZone("UTC")
        return formato.format(Date())
    }
}
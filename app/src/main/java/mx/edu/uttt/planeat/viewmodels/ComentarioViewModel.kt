package mx.edu.uttt.planeat.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import mx.edu.uttt.planeat.models.Comentario
import mx.edu.uttt.planeat.network.ApiClient
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ComentarioViewModel : ViewModel() {

    private val _comentarios = MutableStateFlow<List<Comentario>>(emptyList())
    val comentarios: StateFlow<List<Comentario>> = _comentarios

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadComentariosByReceta(idReceta: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val allComentarios = ApiClient.apiService.getComentarios()
                // Filtra solo los comentarios que corresponden a la receta actual
                _comentarios.value = allComentarios.filter { it.IdReceta == idReceta }

                // Verifica si los comentarios se han cargado correctamente
                println("Comentarios cargados para IdReceta $idReceta: ${_comentarios.value}")
            } catch (e: Exception) {
                _errorMessage.value = "Error cargando comentarios: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addComentario(texto: String, idUsuario: Int, idReceta: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                // Obtenemos la fecha actual y la formateamos según el formato de la API
                val currentDateTime = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") // Formato ISO 8601
                val formattedDate = currentDateTime.format(formatter)

                // Creamos el comentario con el formato adecuado
                val nuevoComentario = Comentario(
                    IdComentario = 0, // Debería ser 0 porque el backend lo genera automáticamente
                    Texto = texto,
                    Fecha = formattedDate, // Usamos la fecha formateada
                    IdUsuario = idUsuario,
                    IdReceta = idReceta
                )

                // Realizamos la petición a la API para agregar el comentario
                val response = ApiClient.apiService.postComentario(nuevoComentario)

                if (response.isSuccessful) {
                    // Recargamos los comentarios después de agregar el nuevo
                    loadComentariosByReceta(idReceta)
                    onSuccess() // Llamamos a onSuccess para actualizar la UI
                } else {
                    _errorMessage.value = "Error al agregar comentario: ${response.code()}"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error de red: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }




    fun deleteComentario(idComentario: Int, idReceta: Int) {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.deleteComentario(idComentario)

                if (response.isSuccessful) {
                    loadComentariosByReceta(idReceta)
                } else {
                    _errorMessage.value = "Error eliminando comentario: ${response.code()}"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error de red: ${e.localizedMessage}"
            }
        }
    }
}

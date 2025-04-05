package mx.edu.uttt.planeat.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.edu.uttt.planeat.models.Puntuaciones
import mx.edu.uttt.planeat.network.ApiClient
import retrofit2.HttpException
import java.io.IOException

class PuntuacionesViewModel : ViewModel() {

    private val _puntuaciones = MutableStateFlow<List<Puntuaciones>>(emptyList())
    val puntuaciones = _puntuaciones.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _puntuacionSeleccionada = MutableStateFlow<Puntuaciones?>(null)
    val puntuacionSeleccionada = _puntuacionSeleccionada.asStateFlow()

    fun selectPuntuacion(puntuacion: Puntuaciones) {
        _puntuacionSeleccionada.value = puntuacion
    }

    fun loadPuntuaciones() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = ApiClient.apiService.getPuntuaciones()
                _puntuaciones.value = response
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar puntuaciones: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadPuntuacionById(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = ApiClient.apiService.getPuntuacionById(id)
                _puntuacionSeleccionada.value = response
            } catch (e: Exception) {
                _errorMessage.value = "Error al obtener la puntuación: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun agregarPuntuacion(puntuacion: Puntuaciones) {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.agregarPuntuacion(puntuacion)
                if (response.isSuccessful) {
                    loadPuntuaciones()
                } else {
                    _errorMessage.value = "No se pudo agregar la puntuación"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al agregar la puntuación: ${e.localizedMessage}"
            }
        }
    }

    fun actualizarPuntuacion(id: Int, puntuacion: Puntuaciones) {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.actualizarPuntuacion(id, puntuacion)
                if (response.isSuccessful) {
                    loadPuntuaciones()
                } else {
                    _errorMessage.value = "No se pudo actualizar la puntuación"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al actualizar la puntuación: ${e.localizedMessage}"
            }
        }
    }

    fun eliminarPuntuacion(id: Int) {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.eliminarPuntuacion(id)
                if (response.isSuccessful) {
                    loadPuntuaciones()
                } else {
                    _errorMessage.value = "No se pudo eliminar la puntuación"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al eliminar la puntuación: ${e.localizedMessage}"
            }
        }
    }
}

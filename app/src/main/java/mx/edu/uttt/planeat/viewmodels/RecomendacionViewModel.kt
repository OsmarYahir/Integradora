package mx.edu.uttt.planeat.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

import mx.edu.uttt.planeat.models.Recomendacion
import mx.edu.uttt.planeat.network.ApiClient
import mx.edu.uttt.planeat.network.ApiService
import retrofit2.HttpException
import java.io.IOException

class RecomendacionViewModel : ViewModel() {

    private val _recomendaciones = MutableStateFlow<List<Recomendacion>>(emptyList())
    val recomendaciones: StateFlow<List<Recomendacion>> = _recomendaciones

    private val api = ApiClient.apiService



    fun cargarRecomendaciones() {
        viewModelScope.launch {
            try {
                val data = api.getRecomendaciones()
                _recomendaciones.value = data
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: HttpException) {
                e.printStackTrace()
            }
        }
    }

    fun agregarRecomendacion(recomendacion: Recomendacion, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                api.agregarRecomendacion(recomendacion)
                cargarRecomendaciones()
                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun actualizarRecomendacion(id: Int, recomendacion: Recomendacion, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                api.actualizarRecomendacion(id, recomendacion)
                cargarRecomendaciones()
                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun eliminarRecomendacion(id: Int, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                api.eliminarRecomendacion(id)
                cargarRecomendaciones()
                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

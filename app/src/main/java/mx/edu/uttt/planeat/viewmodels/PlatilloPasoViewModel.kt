package mx.edu.uttt.planeat.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import mx.edu.uttt.planeat.models.PlatilloPaso
import mx.edu.uttt.planeat.network.ApiClient

class PlatilloPasoViewModel : ViewModel() {

    private val _pasos = MutableStateFlow<List<PlatilloPaso>>(emptyList())
    val pasos: StateFlow<List<PlatilloPaso>> = _pasos

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadPasos() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = ApiClient.apiService.getPlatillosPasos()
                _pasos.value = response
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addPaso(platilloPaso: PlatilloPaso, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                ApiClient.apiService.createPlatilloPaso(platilloPaso)
                loadPasos()
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deletePaso(id: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                ApiClient.apiService.deletePlatilloPaso(id)
                loadPasos()
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

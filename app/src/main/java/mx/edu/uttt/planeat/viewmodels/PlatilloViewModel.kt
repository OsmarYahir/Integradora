package mx.edu.uttt.planeat.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.edu.uttt.planeat.models.Platillo
import mx.edu.uttt.planeat.network.ApiClient

class PlatilloViewModel : ViewModel() {



    private val _platillos = MutableStateFlow<List<Platillo>>(emptyList())
    val platillos = _platillos.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    // Platillo seleccionado para detalle
    private val _platilloSeleccionado = MutableStateFlow<Platillo?>(null)
    val platilloSeleccionado = _platilloSeleccionado.asStateFlow()


    fun selectPlatillo(platillo: Platillo) {
        _platilloSeleccionado.value = platillo
    }

    fun loadPlatillos() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = ApiClient.apiService.getPlatillos() // Ajusta a tu método de API
                _platillos.value = response
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }


}

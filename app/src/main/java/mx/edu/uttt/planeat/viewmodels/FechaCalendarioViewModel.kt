package mx.edu.uttt.planeat.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.edu.uttt.planeat.models.FechaCalendario
import mx.edu.uttt.planeat.network.ApiClient

class FechaCalendarioViewModel : ViewModel() {

    private val _fechas = MutableStateFlow<List<FechaCalendario>>(emptyList())
    val fechas: StateFlow<List<FechaCalendario>> = _fechas.asStateFlow()

    fun loadFechas() {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getFechas()
                _fechas.value = response
            } catch (_: Exception) {}
        }
    }

    fun guardarFecha(anio: Int, mes: Int, dia: Int, onSuccess: (FechaCalendario) -> Unit) {
        viewModelScope.launch {
            try {
                val nueva = FechaCalendario(Anio = anio, Mes = mes, Dia = dia)
                val creada = ApiClient.apiService.postFecha(nueva)
                _fechas.value = _fechas.value + creada
                onSuccess(creada)
            } catch (_: Exception) {}
        }
    }
}

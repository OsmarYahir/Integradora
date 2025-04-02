package mx.edu.uttt.planeat.viewmodels

import androidx.compose.runtime.Composable
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

    // Cargar las fechas desde la API
    fun loadFechas() {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getFechas()
                _fechas.value = response
            } catch (_: Exception) {}
        }
    }

    fun guardarFechaSiNoExiste(anio: Int, mes: Int, dia: Int, onSuccess: (Int) -> Unit) {
        viewModelScope.launch {
            try {
                // Verifica si ya existe la fecha
                val fechaExistente = _fechas.value.find {
                    it.Anio == anio && it.Mes == mes && it.Dia == dia
                }

                if (fechaExistente != null) {
                    // Si existe, pasamos el ID del calendario ya creado
                    onSuccess(fechaExistente.IdCalendario)
                } else {
                    // Si no existe, crea una nueva
                    val nuevaFecha = FechaCalendario(Anio = anio, Mes = mes, Dia = dia)
                    val creada = ApiClient.apiService.postFecha(nuevaFecha)
                    _fechas.value = _fechas.value + creada
                    onSuccess(creada.IdCalendario) // Pasa el ID de la nueva fecha
                }
            } catch (_: Exception) {}
        }
    }




}



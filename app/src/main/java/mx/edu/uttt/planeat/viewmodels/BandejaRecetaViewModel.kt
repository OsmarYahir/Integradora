package mx.edu.uttt.planeat.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.edu.uttt.planeat.models.BandejaReceta

import mx.edu.uttt.planeat.response.BandejaRecetaRepository

class BandejaRecetaViewModel : ViewModel() {
    private val repository = BandejaRecetaRepository()

    private val _resultado = MutableStateFlow<Boolean?>(null)
    val resultado = _resultado.asStateFlow()

    private val _bandejaRecetas = MutableStateFlow<List<BandejaReceta>>(emptyList())
    val bandejaRecetas: StateFlow<List<BandejaReceta>> = _bandejaRecetas


    fun guardarBandejaReceta(bandeja: BandejaReceta) {
        viewModelScope.launch {
            val exito = repository.guardarBandejaReceta(bandeja)
            _resultado.value = exito
        }
    }

    fun obtenerTodasLasBandejas() {
        viewModelScope.launch {
            val lista = repository.obtenerTodasLasBandejas()
            _bandejaRecetas.value = lista
        }
    }


}

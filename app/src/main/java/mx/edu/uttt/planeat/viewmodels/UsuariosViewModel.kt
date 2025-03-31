package mx.edu.uttt.planeat.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import mx.edu.uttt.planeat.models.Usuario
import mx.edu.uttt.planeat.network.ApiClient

class UsuariosViewModel: ViewModel() {
    private val _usuarios = MutableStateFlow<List<Usuario>>(emptyList())
    val usuarios: StateFlow<List<Usuario>> = _usuarios.asStateFlow()

    // ✅ Mapa dinámico para obtener el Nombre a partir del IdUsuario
    val usuariosMap: StateFlow<Map<Int, String>> = usuarios
        .map { lista -> lista.associateBy({ it.IdUsuario }, { it.Nombre }) }
        .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.Eagerly, emptyMap())

    fun loadUsuarios() {
        viewModelScope.launch {
            try {
                val usuariosApi = ApiClient.apiService.getUsuarios()
                _usuarios.value = usuariosApi
            } catch (e: Exception) {
                // Manejar el error si es necesario
            }
        }
    }
}
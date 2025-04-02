package mx.edu.uttt.planeat.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import mx.edu.uttt.planeat.models.Usuario
import mx.edu.uttt.planeat.network.ApiClient
import mx.edu.uttt.planeat.response.UserPreferences

class LoginViewModel(private val context: Context) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _usuarioLogueado = MutableStateFlow<Usuario?>(null)
    val usuarioLogueado: StateFlow<Usuario?> = _usuarioLogueado

    private val _userId = MutableStateFlow<Int>(-1)
    val userId: StateFlow<Int> = _userId

    // Verificamos el idUsuario
    fun getUserId(): Int {
        val preferences = UserPreferences(context)
        return preferences.getUserId() // Recuperamos el id guardado desde SharedPreferences
    }

    fun login(email: String, password: String) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                // Primero revisamos si el idUsuario está guardado
                val savedUserId = getUserId()
                if (savedUserId != -1) {
                    // Si ya está guardado, simplemente navegamos
                    _usuarioLogueado.value = Usuario(savedUserId, "", email, "", password)
                    return@launch
                }

                // Si no está guardado, verificamos en la API
                val usuarios = ApiClient.apiService.getUsuarios()
                val usuario = usuarios.find {
                    it.Email.equals(email, ignoreCase = true) && it.Password == password
                }

                if (usuario != null) {
                    // Si el usuario existe, lo guardamos en las preferencias
                    _usuarioLogueado.value = usuario
                    _userId.value = usuario.IdUsuario

                    // Guardamos el idUsuario en las preferencias
                    val preferences = UserPreferences(context)
                    preferences.saveUserId(usuario.IdUsuario)
                } else {
                    _errorMessage.value = "Credenciales incorrectas"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de red: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Función para cerrar sesión
    fun logout() {
        val preferences = UserPreferences(context)
        preferences.clearUserId()  // Limpiar el id del usuario
        _usuarioLogueado.value = null  // Limpiar el usuario logueado
        _userId.value = -1  // Reiniciar el userId
    }
}
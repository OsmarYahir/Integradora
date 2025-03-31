package mx.edu.uttt.planeat.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mx.edu.uttt.planeat.network.ApiClient   // Importa ApiClient aquí
import mx.edu.uttt.planeat.models.Usuario      // Si quieres usar Usuario directamente
import mx.edu.uttt.planeat.response.UserRequest  // Si tienes el UserRequest para registrar

class RegisterViewModel : ViewModel() {

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    fun register(username: String, email: String, password: String, onSuccess: () -> Unit) {
        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Todos los campos son obligatorios."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val currentDate = java.time.Instant.now().toString()
                // ✅ Genera la fecha

                val usuario = Usuario(
                    IdUsuario = 0,
                    Nombre = username,
                    Email = email,
                    Password = password,
                    Fecha_Registro = currentDate // ✅ Enviar la fecha correcta
                )

                val response = ApiClient.apiService.createUsuario(usuario)

                if (response != null) {
                    onSuccess()
                } else {
                    _errorMessage.value = "Error al registrar el usuario"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Ocurrió un error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

}


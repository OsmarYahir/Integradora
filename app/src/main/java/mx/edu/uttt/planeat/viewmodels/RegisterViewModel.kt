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

//    fun register(username: String, email: String, password: String, onSuccess: () -> Unit) {
//        if (username.isBlank() || email.isBlank() || password.isBlank()) {
//            _errorMessage.value = "Todos los campos son obligatorios."
//            return
//        }
//
//        viewModelScope.launch {
//            _isLoading.value = true
//            _errorMessage.value = null
//
//            try {
//                val currentDate = java.time.Instant.now().toString()
//                // ✅ Genera la fecha
//
//                val usuario = Usuario(
//                    IdUsuario = 0,
//                    Nombre = username,
//                    Email = email,
//                    Password = password,
//                    Fecha_Registro = currentDate // ✅ Enviar la fecha correcta
//                )
//
//                val response = ApiClient.apiService.createUsuario(usuario)
//
//                if (response != null) {
//                    onSuccess()
//                } else {
//                    _errorMessage.value = "Error al registrar el usuario"
//                }
//
//            } catch (e: Exception) {
//                _errorMessage.value = "Ocurrió un error: ${e.localizedMessage}"
//            } finally {
//                _isLoading.value = false
//            }
//        }
//    }

    fun register(nombre: String, email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val usuarios = ApiClient.apiService.getUsuarios()
                val currentDate = java.time.Instant.now().toString()
                // Verificar si el nombre o correo ya existen
                val nombreExistente = usuarios.any {
                    it.Nombre.equals(nombre, ignoreCase = true)
                }

                val correoExistente = usuarios.any {
                    it.Email.equals(email, ignoreCase = true)
                }

                if (nombreExistente || correoExistente) {
                    _errorMessage.value = when {
                        nombreExistente && correoExistente -> "El nombre y correo ya están registrados"
                        nombreExistente -> "El nombre ya está registrado"
                        else -> "El correo electrónico ya está registrado"
                    }
                    return@launch
                }

                // Crear el usuario con la fecha actual (puedes ajustar esto si tu backend la asigna)
                val nuevoUsuario = Usuario(
                    IdUsuario = 0,
                    Nombre = nombre,
                    Email = email,
                    Password = password,
                    Fecha_Registro = currentDate // Puedes mandar "" si la API lo genera
                )

                val response = ApiClient.apiService.createUsuario(nuevoUsuario)

                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    _errorMessage.value = "Error al registrar: ${response.code()}"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error de red: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }


}


package mx.edu.uttt.planeat.viewmodels

import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import mx.edu.uttt.planeat.models.Favorito
import mx.edu.uttt.planeat.models.Platillo
import mx.edu.uttt.planeat.network.ApiClient
import mx.edu.uttt.planeat.response.UserPreferences



class FavoritoViewModel : ViewModel() {

    private val _favoritos = MutableStateFlow<List<Favorito>>(emptyList())
    val favoritos: StateFlow<List<Favorito>> = _favoritos

    private val _platillos = MutableStateFlow<List<Platillo>>(emptyList())  // Lista de platillos
    val platillos: StateFlow<List<Platillo>> = _platillos

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Método para cargar todos los favoritos sin filtrar por idUsuario
    fun loadFavoritosSinFiltro() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                // Llamada GET a la API para obtener todos los favoritos
                val responseFavoritos = ApiClient.apiService.getFavoritos()
                // Actualizar el estado con todos los favoritos (sin filtro)
                _favoritos.value = responseFavoritos

            } catch (e: Exception) {
                _errorMessage.value = "Error cargando favoritos: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Método para cargar los favoritos filtrados por idUsuario
    fun loadFavoritosPorUsuario(idUsuarioActual: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                // Llamada GET a la API para obtener todos los favoritos
                val responseFavoritos = ApiClient.apiService.getFavoritos()

                // Filtra los favoritos por el idUsuarioActual
                val favoritosUsuario = responseFavoritos.filter { it.IdUsuario == idUsuarioActual }

                // Actualizar el estado con los favoritos filtrados por idUsuario
                _favoritos.value = favoritosUsuario

            } catch (e: Exception) {
                _errorMessage.value = "Error cargando favoritos: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addFavorito(idUsuarioActual: Int, idReceta: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                // No asignamos el IdFavorito, ya que se autogenera en el servidor
                val nuevoFavorito = Favorito(
                    IdFavorito = 0,  // Esto lo dejamos en 0, el servidor lo generará
                    IdUsuario = idUsuarioActual,  // Usamos el idUsuarioActual
                    IdReceta = idReceta  // Usamos el idReceta de la receta seleccionada
                )

                val response = ApiClient.apiService.postFavorito(nuevoFavorito)

                if (response.isSuccessful) {
                    loadFavoritosPorUsuario(idUsuarioActual)  // Recargar favoritos después de agregar
                } else {
                    _errorMessage.value = "Error al agregar favorito: ${response.code()}"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error de red: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }


    // Método para eliminar favoritos
    fun removeFavorito(idFavorito: Int, idUsuarioActual: Int) {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.deleteFavorito(idFavorito)

                if (response.isSuccessful) {
                    loadFavoritosPorUsuario(idUsuarioActual)  // Recargar favoritos después de eliminar
                } else {
                    _errorMessage.value = "Error eliminando favorito: ${response.code()}"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error de red: ${e.localizedMessage}"
            }
        }
    }

    fun loadFavoritosByUsuario(idUsuarioActual: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                // Llamada GET a la API para obtener todos los favoritos
                val responseFavoritos = ApiClient.apiService.getFavoritos()

                // Filtrar los favoritos por el idUsuarioActual
                val favoritosUsuario = responseFavoritos.filter { it.IdUsuario == idUsuarioActual }

                // Actualizar el estado con los favoritos filtrados por idUsuario
                _favoritos.value = favoritosUsuario

            } catch (e: Exception) {
                _errorMessage.value = "Error cargando favoritos: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }



    // ✅ Verifica si una receta es favorita (por su id)
    fun isRecetaFavorita(idReceta: Int): Boolean {
        return _favoritos.value.any { it.IdReceta == idReceta }
    }

    // ✅ Obtiene el IdFavorito correspondiente para eliminar
    fun getIdFavoritoByReceta(idReceta: Int): Int? {
        return _favoritos.value.find { it.IdReceta == idReceta }?.IdFavorito
    }
}
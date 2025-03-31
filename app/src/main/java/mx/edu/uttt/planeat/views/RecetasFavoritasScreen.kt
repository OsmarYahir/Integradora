package mx.edu.uttt.planeat.views

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.edu.uttt.planeat.models.Platillo
import mx.edu.uttt.planeat.models.RecetaDetalle
import mx.edu.uttt.planeat.models.toRecetaSocial
import mx.edu.uttt.planeat.response.UserPreferences
import mx.edu.uttt.planeat.viewmodels.FavoritoViewModel
import mx.edu.uttt.planeat.viewmodels.PlatilloViewModel
import mx.edu.uttt.planeat.viewmodels.UsuariosViewModel

@Composable
fun RecetasFavoritasScreen(
    onNavigateToDetail: (Platillo) -> Unit,
    platilloViewModel: PlatilloViewModel = viewModel(),
    favoritoViewModel: FavoritoViewModel = viewModel(),
    usuarioViewModel: UsuariosViewModel = viewModel()
) {
    val favoritos by favoritoViewModel.favoritos.collectAsState(initial = emptyList())
    val platillos by platilloViewModel.platillos.collectAsState()
    val isLoading by platilloViewModel.isLoading.collectAsState()
    val errorMessage by platilloViewModel.errorMessage.collectAsState()
    val usuariosMap by usuarioViewModel.usuariosMap.collectAsState()

    val context = LocalContext.current
    val userPreferences = UserPreferences(context)
    val idUsuarioActual = userPreferences.getUserId()

    LaunchedEffect(idUsuarioActual) {
        favoritoViewModel.loadFavoritosPorUsuario(idUsuarioActual)
        platilloViewModel.loadPlatillos()
        usuarioViewModel.loadUsuarios()
    }

    val platillosFavoritos = platillos.filter { platillo ->
        favoritos.any { it.IdReceta == platillo.IdReceta && it.IdUsuario == idUsuarioActual }
    }

    Scaffold(
        containerColor = Color(0xFFF9F9F9)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Recetas Favoritas",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4E3629)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator(color = Color(0xFF4E3629))
                Spacer(modifier = Modifier.height(16.dp))
            }

            errorMessage?.let {
                Text(text = it, color = Color.Red)
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (platillosFavoritos.isEmpty()) {
                Text(
                    text = "No tienes platillos favoritos aún.",
                    fontSize = 16.sp,
                    color = Color(0xFF4E3629)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(platillosFavoritos) { platillo ->
                        val nombreUsuario = usuariosMap[platillo.IdUsuario] ?: "Usuario desconocido"

                        val recetaSocial = platillo.toRecetaSocial(nombreUsuario).copy(
                            imagenUsuario = mx.edu.uttt.planeat.R.drawable.logo, // asegúrate de tener una imagen válida
                            imagenReceta = mx.edu.uttt.planeat.R.drawable.logo
                        )

                        RecetaSocialCard(
                            receta = recetaSocial,
                            amarilloFuerte = Color(0xFFFFD94C),
                            cafeOscuro = Color(0xFF4E3629),
                            blanco = Color.White,
                            onNavigateToDetail = {
                                Log.d("FAVORITOS", "Navegando a detalle de ${platillo.IdReceta}")
                                onNavigateToDetail(platillo)
                            }
                        )
                    }
                }
            }
        }
    }
}






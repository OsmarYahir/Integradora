package mx.edu.uttt.planeat.views

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FoodBank
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.edu.uttt.planeat.models.Platillo
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
    val colorPrimary = Color(0xFFFA9600) // Naranja elegante
    val colorSecondary = Color(0xFF2D3142) // Azul oscuro casi negro
    val colorBackground = Color(0xFFF8F9FA) // Gris muy claro
    val colorSurface = Color.White
    val colorAccent = Color(0xFFE76F51) // Coral para acentos

    val favoritos by favoritoViewModel.favoritos.collectAsState(initial = emptyList())
    val platillos by platilloViewModel.platillos.collectAsState()
    val isLoading by platilloViewModel.isLoading.collectAsState()
    val errorMessage by platilloViewModel.errorMessage.collectAsState()
    val usuariosMap by usuarioViewModel.usuariosMap.collectAsState()

    val context = LocalContext.current
    val userPreferences = UserPreferences(context)
    val idUsuarioActual = userPreferences.getUserId()

    Log.d("DEBUG", "ID de usuario actual desde UserPreferences: $idUsuarioActual")


    LaunchedEffect(idUsuarioActual) {
        favoritoViewModel.loadFavoritosPorUsuario(idUsuarioActual)
        platilloViewModel.loadPlatillos()
        usuarioViewModel.loadUsuarios()
    }

    // Filtrar solo los favoritos del usuario actualval platillosFavoritos = platillos.filter { platillo ->
    //    favoritos.any { it.IdUsuario == idUsuarioActual && it.IdReceta == platillo.IdReceta }
    //}
    val platillosFavoritos = if (favoritos.isNotEmpty() && platillos.isNotEmpty()) {
        val favoritosDelUsuario = favoritos.filter { it.IdUsuario == idUsuarioActual }
        val idsFavoritos = favoritosDelUsuario.map { it.IdReceta }

        val resultado = platillos.filter { it.IdReceta in idsFavoritos }
        Log.d("DEBUG", "Platillos favoritos encontrados: $resultado")
        resultado
    } else {
        Log.d("DEBUG", "Favoritos o platillos vacíos")
        emptyList()
    }


    Scaffold(
        containerColor = colorBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Encabezado con estilo moderno
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Favorite,
                    contentDescription = null,
                    tint = colorAccent,
                    modifier = Modifier.size(32.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Mis Favoritas",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = colorSecondary
                    )

                    Text(
                        text = "Recetas que has guardado",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = colorSecondary.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Estado de carga con animación
            AnimatedVisibility(
                visible = isLoading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = colorPrimary,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            // Mensaje de error con estilo moderno
            errorMessage?.let {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFEBEE)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.FoodBank,
                            contentDescription = null,
                            tint = Color(0xFFB71C1C)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = it,
                            color = Color(0xFFB71C1C),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Mensaje cuando no hay favoritos
            if (platillosFavoritos.isEmpty() && !isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = mx.edu.uttt.planeat.R.drawable.logo),
                            contentDescription = "No hay favoritos",
                            modifier = Modifier.size(100.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Aún no tienes recetas favoritas",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorSecondary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Guarda tus recetas preferidas para acceder rápidamente",
                            fontSize = 14.sp,
                            color = colorSecondary.copy(alpha = 0.7f),
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Botón para explorar recetas
                        Button(
                            onClick = { /* Navegar a explorar */ },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorPrimary
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = "Explorar recetas",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            } else {
                // Lista de recetas favoritas
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    items(platillosFavoritos) { platillo ->
                        val nombreUsuario = usuariosMap[platillo.IdUsuario] ?: "Usuario creativo"

                        // Verifica si la imagen está vacía o es nula antes de asignarla
                        val recetaSocial = platillo.toRecetaSocial(nombreUsuario).copy(
                            imagenReceta = if (platillo.Imagen.isNullOrEmpty()) "" else platillo.Imagen // Evitar null
                        )

                        RecetaSocialCard(
                            receta = recetaSocial,
                            colorPrimary = colorPrimary,
                            colorSecondary = colorSecondary,
                            colorSurface = colorSurface,
                            colorAccent = colorAccent,
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

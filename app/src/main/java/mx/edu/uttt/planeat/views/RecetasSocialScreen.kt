package mx.edu.uttt.planeat.views

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.BookmarkAdd
import androidx.compose.material.icons.rounded.FoodBank
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.uttt.planeat.R
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import mx.edu.uttt.planeat.models.Platillo
import mx.edu.uttt.planeat.models.RecetaDetalle
import mx.edu.uttt.planeat.models.toRecetaSocial
import mx.edu.uttt.planeat.viewmodels.PlatilloViewModel
import mx.edu.uttt.planeat.viewmodels.UsuariosViewModel
import coil.compose.rememberImagePainter
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage

@Composable
fun RecetasSocialScreen(
    onNavigateToDetail: (Platillo) -> Unit,
    platilloViewModel: PlatilloViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    usuarioViewModel: UsuariosViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    // Paleta de colores más moderna y elegante
    val colorPrimary = Color(0xFFFA9600) // Naranja elegante
    val colorSecondary = Color(0xFF2D3142) // Azul oscuro casi negro
    val colorBackground = Color(0xFFF8F9FA) // Gris muy claro
    val colorSurface = Color.White
    val colorAccent = Color(0xFFE76F51) // Coral para acentos

    val platillos by platilloViewModel.platillos.collectAsState()
    val isLoading by platilloViewModel.isLoading.collectAsState()
    val errorMessage by platilloViewModel.errorMessage.collectAsState()
    val usuariosMap by usuarioViewModel.usuariosMap.collectAsState()

    LaunchedEffect(Unit) {
        usuarioViewModel.loadUsuarios()
        platilloViewModel.loadPlatillos()
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

            // Título con estilo moderno
            Text(
                text = "Descubre",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = colorSecondary
            )

            Text(
                text = "Recetas de la comunidad",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = colorSecondary.copy(alpha = 0.7f)
            )

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

            // Lista de recetas con espaciado mejorado
            LazyColumn(
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                items(platillos) { platillo ->
                    val nombreUsuario = usuariosMap[platillo.IdUsuario] ?: "Usuario creativo"

                    RecetaSocialCard(
                        receta = platillo.toRecetaSocial(nombreUsuario),
                        colorPrimary = colorPrimary,
                        colorSecondary = colorSecondary,
                        colorSurface = colorSurface,
                        colorAccent = colorAccent,
                        onNavigateToDetail = {
                            onNavigateToDetail(platillo)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RecetaSocialCard(
    receta: RecetaSocial,
    colorPrimary: Color,
    colorSecondary: Color,
    colorSurface: Color,
    colorAccent: Color,
    onNavigateToDetail: () -> Unit
) {
    var liked by remember { mutableStateOf(false) }
    var saved by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = colorSecondary.copy(alpha = 0.1f)
            )
            .clickable { onNavigateToDetail() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colorSurface)
    ) {
        Box {
            // Cargar la imagen con AsyncImage
            AsyncImage(
                model = receta.imagenReceta.ifEmpty { "url/de/imagen/default.png" },  // Usa imagen por defecto si está vacía
                contentDescription = receta.titulo,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentScale = ContentScale.Crop,
                onLoading = {
                    // Aquí puedes manejar lo que sucede mientras se carga la imagen
                },
                onSuccess = {
                    // Aquí puedes manejar lo que pasa cuando la imagen se carga correctamente
                },
                onError = {
                    // Aquí puedes manejar los errores de carga de la imagen
                    // Puede ser útil mostrar un ícono o imagen predeterminada
                }
            )

            // Gradiente sobre la imagen para mejorar legibilidad
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                colorSecondary.copy(alpha = 0.8f)
                            ),
                            startY = 100f
                        )
                    )
            )

            // Información de la receta sobre la imagen
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = receta.titulo,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        // Resto del código sin cambios...
        Column(modifier = Modifier.padding(16.dp)) {
            // Información del autor
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = receta.usuario,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorSecondary
                    )

                    Text(
                        text = "Chef casero",
                        fontSize = 12.sp,
                        color = colorSecondary.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = receta.descripcion,
                fontSize = 14.sp,
                color = colorSecondary.copy(alpha = 0.7f),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Acciones de la receta
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón Ver receta
                Button(
                    onClick = { onNavigateToDetail() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorPrimary
                    ),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Ver receta",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

// Estado para el manejo de carga de imágenes
enum class ImageLoadingState {
    Loading,
    Success,
    Error
}

// Función mejorada para convertir Base64 a Bitmap con mejor manejo de errores
fun base64ToBitmap(base64String: String): Bitmap? {
    return try {
        // Eliminar prefijos comunes de Base64 si existen
        val cleanBase64 = when {
            base64String.contains(",") -> base64String.split(",")[1]
            base64String.startsWith("data:") -> {
                val commaIndex = base64String.indexOf(",")
                if (commaIndex != -1) base64String.substring(commaIndex + 1) else base64String
            }
            else -> base64String
        }

        // Asegurarse de que la string tenga un múltiplo de 4 caracteres (requisito para Base64)
        val paddedBase64 = when (cleanBase64.length % 4) {
            0 -> cleanBase64
            1 -> cleanBase64.substring(0, cleanBase64.length - 1) + "==="
            2 -> cleanBase64 + "=="
            3 -> cleanBase64 + "="
            else -> cleanBase64
        }

        val decodedString = Base64.decode(paddedBase64, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
// Asegúrate de tener una imagen placeholder en tus recursos
// Añade esto a tu res/drawable folder

data class RecetaSocial(
    val usuario: String,
    val imagenReceta: String,
    val titulo: String,
    val descripcion: String,
    val puntuacion: Float
)
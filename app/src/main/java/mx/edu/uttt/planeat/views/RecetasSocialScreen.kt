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
import mx.edu.uttt.planeat.viewmodels.PuntuacionesViewModel

@Composable
fun RecetasSocialScreen(
    onNavigateToDetail: (Platillo) -> Unit,
    platilloViewModel: PlatilloViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    usuarioViewModel: UsuariosViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    puntuacionesViewModel: PuntuacionesViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    // Paleta de colores actualizada para un aspecto más premium
    val colorPrimary = Color(0xFFFDDC58) // Naranja vibrante
    val colorSecondary = Color(0xFF1E2A3A) // Azul oscuro profundo
    val colorBackground = Color(0xFFF9FAFB) // Gris claro con toque de azul
    val colorSurface = Color.White
    val colorAccent = Color(0xFFFF5252) // Rojo coral para acentos

    val platillos by platilloViewModel.platillos.collectAsState()
    val isLoading by platilloViewModel.isLoading.collectAsState()
    val errorMessage by platilloViewModel.errorMessage.collectAsState()
    val usuariosMap by usuarioViewModel.usuariosMap.collectAsState()
    val puntuaciones by puntuacionesViewModel.puntuaciones.collectAsState()

    LaunchedEffect(Unit) {
        usuarioViewModel.loadUsuarios()
        platilloViewModel.loadPlatillos()
        puntuacionesViewModel.loadPuntuaciones()
    }

    Scaffold(
        containerColor = colorBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp) // Aumentado el padding horizontal
        ) {
            Spacer(modifier = Modifier.height(24.dp)) // Aumentado el espaciado superior

            // Título con animación y estilo premium
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
            ) {
                Text(
                    text = "Descubre",
                    fontSize = 36.sp, // Título más grande
                    fontWeight = FontWeight.Black, // Más énfasis
                    color = colorSecondary,
                    letterSpacing = (-0.5).sp // Espaciado negativo para aspecto premium
                )

                Text(
                    text = "Recetas de la comunidad",
                    fontSize = 18.sp, // Subtítulo más grande
                    fontWeight = FontWeight.Medium,
                    color = colorSecondary.copy(alpha = 0.6f), // Más sutil
                    letterSpacing = 0.2.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp)) // Más espacio antes del contenido

            // Estado de carga con animación mejorada
            AnimatedVisibility(
                visible = isLoading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = colorPrimary,
                        modifier = Modifier.size(56.dp),
                        strokeWidth = 4.dp // Línea más gruesa
                    )
                }
            }

            // Mensaje de error con estilo moderno
            errorMessage?.let {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFEEEE) // Rojo más suave
                    ),
                    shape = RoundedCornerShape(16.dp), // Bordes más redondeados
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 2.dp // Sutil elevación
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp), // Más padding
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.FoodBank,
                            contentDescription = null,
                            tint = Color(0xFFE53935), // Rojo más brillante
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = it,
                            color = Color(0xFFB71C1C),
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp // Texto más grande
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Lista de recetas con espaciado mejorado
            LazyColumn(
                contentPadding = PaddingValues(bottom = 32.dp), // Más padding en la parte inferior
                verticalArrangement = Arrangement.spacedBy(32.dp) // Mayor separación entre elementos
            ) {
                items(platillos) { platillo ->
                    val nombreUsuario = usuariosMap[platillo.IdUsuario] ?: "Usuario creativo"

                    // Calcular la puntuación media para esta receta
                    val recetaPuntuaciones = puntuaciones.filter { it.IdReceta == platillo.IdReceta }
                    val puntuacionMedia = if (recetaPuntuaciones.isNotEmpty()) {
                        recetaPuntuaciones.map { it.Numeracion }.average().toFloat()
                    } else {
                        0f
                    }

                    val receta = platillo.toRecetaSocial(nombreUsuario).copy(puntuacion = puntuacionMedia)

                    RecetaSocialCard(
                        receta = receta,
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
                elevation = 12.dp, // Sombra más profunda
                shape = RoundedCornerShape(24.dp), // Bordes más redondeados
                spotColor = colorSecondary.copy(alpha = 0.15f), // Sombra más sutil
                ambientColor = colorSecondary.copy(alpha = 0.05f) // Sombra ambiental
            )
            .clickable { onNavigateToDetail() },
        shape = RoundedCornerShape(24.dp), // Bordes más redondeados
        colors = CardDefaults.cardColors(
            containerColor = colorSurface,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp // Sin elevación adicional
        )
    ) {
        Box {
            // Imagen con mejor manejo y estilo
            if (receta.imagenReceta.isNotEmpty()) {
                Box {
                    AsyncImage(
                        model = receta.imagenReceta,
                        contentDescription = receta.titulo,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp), // Imagen más grande
                        contentScale = ContentScale.Crop,
                        error = painterResource(id = R.drawable.logo)
                    )

                    // Gradiente mejorado sobre la imagen
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.1f), // Punto medio sutil
                                        Color.Black.copy(alpha = 0.7f) // Más oscuro en la parte inferior
                                    ),
                                    startY = 50f
                                )
                            )
                    )

                    // Badge de puntuación en la esquina superior derecha
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .shadow(8.dp, CircleShape)
                            .clip(CircleShape)
                            .background(colorPrimary)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Puntuación",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = String.format("%.1f", receta.puntuacion),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            } else {
                // Placeholder mejorado
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp) // Placeholder más grande
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    colorSecondary.copy(alpha = 0.05f),
                                    colorSecondary.copy(alpha = 0.15f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.FoodBank,
                        contentDescription = null,
                        tint = colorSecondary.copy(alpha = 0.3f),
                        modifier = Modifier.size(72.dp) // Icono más grande
                    )
                }

                // Badge de puntuación para placeholder
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .shadow(8.dp, CircleShape)
                        .clip(CircleShape)
                        .background(colorPrimary)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Puntuación",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = String.format("%.1f", receta.puntuacion),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Información de la receta sobre la imagen con mejor posicionamiento
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(20.dp) // Más padding
            ) {
                Text(
                    text = receta.titulo,
                    fontSize = 24.sp, // Título más grande
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    letterSpacing = (-0.3).sp // Espaciado negativo para aspecto premium
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Mostrar puntuación con estrellas con mejor diseño
                RatingDisplay(
                    rating = receta.puntuacion,
                    colorAccent = colorPrimary
                )
            }
        }

        // Contenido de la tarjeta mejorado
        Column(modifier = Modifier.padding(20.dp)) { // Más padding
            // Información del autor con mejor diseño
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar del usuario (puedes usar un placeholder circular)
                Box(
                    modifier = Modifier
                        .size(36.dp) // Avatar más grande
                        .clip(CircleShape)
                        .background(colorPrimary.copy(alpha = 0.2f))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = receta.usuario.first().toString().uppercase(),
                        color = colorPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = receta.usuario,
                    fontSize = 16.sp, // Texto más grande
                    fontWeight = FontWeight.SemiBold, // Más énfasis
                    color = colorSecondary
                )

                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp)) // Más espacio

            // Descripción con mejor diseño
            Text(
                text = receta.descripcion,
                fontSize = 15.sp, // Texto más grande
                lineHeight = 24.sp, // Mayor espaciado entre líneas
                color = colorSecondary.copy(alpha = 0.8f), // Color más oscuro para mejor legibilidad
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(20.dp)) // Más espacio

            // Botón mejorado
            Button(
                onClick = { onNavigateToDetail() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorPrimary
                ),
                shape = RoundedCornerShape(30.dp), // Botón más redondeado
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp), // Más padding
                modifier = Modifier.fillMaxWidth() // Botón de ancho completo
            ) {
                Text(
                    text = "Ver receta completa",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp, // Texto más grande
                    letterSpacing = 0.5.sp // Mayor espaciado entre letras
                )
            }
        }
    }
}

@Composable
fun RatingDisplay(rating: Float, colorAccent: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp) // Espaciado uniforme entre estrellas
    ) {
        // Mostrar 5 estrellas, rellenadas según la puntuación
        for (i in 1..5) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = if (i <= rating) colorAccent else Color.White.copy(alpha = 0.5f), // Mayor contraste
                modifier = Modifier.size(18.dp) // Estrellas más grandes
            )
        }

        // Mostrar número de puntuación
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = String.format("%.1f", rating),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

data class RecetaSocial(
    val usuario: String,
    val imagenReceta: String = "",
    val titulo: String,
    val descripcion: String,
    val puntuacion: Float
)
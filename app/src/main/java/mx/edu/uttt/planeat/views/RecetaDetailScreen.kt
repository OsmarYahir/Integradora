package mx.edu.uttt.planeat.views

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.Comment
import androidx.compose.material.icons.rounded.LocalDining
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.util.copy
import coil.compose.AsyncImage
import coil.request.ImageRequest
import mx.edu.uttt.planeat.models.Platillo
import mx.edu.uttt.planeat.models.Puntuaciones
import mx.edu.uttt.planeat.response.UserPreferences
import mx.edu.uttt.planeat.viewmodels.*

// Definición de colores mejorados para una apariencia más elegante
val cafeClaro = Color(0xFFD9C4B5)

val blanco = Color.White
val grisFondo = Color(0xFFF9F9F9)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecetaDetailScreen(
    platillo: Platillo,
    onBack: () -> Unit,
    ingredienteViewModel: IngredienteViewModel = viewModel(),
    pasoViewModel: PlatilloPasoViewModel = viewModel(),
    comentarioViewModel: ComentarioViewModel = viewModel(),
    favoritoViewModel: FavoritoViewModel = viewModel(),
    usuarioViewModel: UsuariosViewModel = viewModel(),
    puntuacionViewModel: PuntuacionesViewModel = viewModel(), // Añadir el ViewModel de puntuaciones
    onNavigateToAgenda: (Int) -> Unit
) {
    var comment by remember { mutableStateOf("") }
    var userRating by remember { mutableStateOf(0) } // Estado para la puntuación del usuario
    var showRatingFeedback by remember { mutableStateOf(false) }
    var ratingFeedbackMessage by remember { mutableStateOf("") }

    // Obtener el idUsuario guardado en SharedPreferences
    val context = LocalContext.current
    val userPreferences = UserPreferences(context)
    val idUsuarioActual = userPreferences.getUserId()

    // Obtén los ingredientes, pasos, comentarios, favoritos y puntuaciones
    val ingredientes by ingredienteViewModel.ingredientes.collectAsState(initial = emptyList())
    val pasos by pasoViewModel.pasos.collectAsState(initial = emptyList())
    val comentarios by comentarioViewModel.comentarios.collectAsState(initial = emptyList())
    val favoritos by favoritoViewModel.favoritos.collectAsState(initial = emptyList())
    val puntuaciones by puntuacionViewModel.puntuaciones.collectAsState(initial = emptyList())

    // Comprobar si la receta está en favoritos
    val esFavorito by remember(favoritos, platillo.IdReceta) {
        derivedStateOf {
            favoritos.any { it.IdReceta == platillo.IdReceta && it.IdUsuario == idUsuarioActual }
        }
    }

    // Obtener la puntuación actual del usuario para esta receta, si existe
    val puntuacionUsuario by remember(puntuaciones, platillo.IdReceta) {
        derivedStateOf {
            puntuaciones.find { it.IdReceta == platillo.IdReceta && it.IdUsuario == idUsuarioActual }
        }
    }

    // Calcular la puntuación promedio de la receta
    val puntuacionPromedio by remember(puntuaciones, platillo.IdReceta) {
        derivedStateOf {
            val puntuacionesReceta = puntuaciones.filter { it.IdReceta == platillo.IdReceta }
            if (puntuacionesReceta.isNotEmpty()) {
                puntuacionesReceta.map { it.Numeracion }.average()
            } else {
                0.0
            }
        }
    }

    // Si el usuario ya tiene una puntuación, mostrarla
    LaunchedEffect(puntuacionUsuario) {
        puntuacionUsuario?.let {
            userRating = it.Numeracion
        }
    }

    LaunchedEffect(platillo.IdReceta) {
        ingredienteViewModel.loadIngredientes()
        pasoViewModel.loadPasos()
        comentarioViewModel.loadComentariosByReceta(platillo.IdReceta)
        favoritoViewModel.loadFavoritosSinFiltro()
        usuarioViewModel.loadUsuarios()
        puntuacionViewModel.loadPuntuaciones() // Cargar puntuaciones
    }

    // Función para enviar o actualizar la puntuación
    val enviarPuntuacion = {
        val puntuacionExistente = puntuacionUsuario

        if (puntuacionExistente != null) {
            // Actualizar puntuación existente
            val puntuacionActualizada = puntuacionExistente.copy(Numeracion = userRating)
            puntuacionViewModel.actualizarPuntuacion(puntuacionExistente.IdPuntuacion, puntuacionActualizada)
            // Mostrar mensaje de confirmación
            ratingFeedbackMessage = "¡Puntuación actualizada!"
        } else {
            // Crear nueva puntuación
            val nuevaPuntuacion = Puntuaciones(
                IdPuntuacion = 0, // La API asignará el ID
                IdUsuario = idUsuarioActual,
                IdReceta = platillo.IdReceta,
                Numeracion = userRating
            )
            puntuacionViewModel.agregarPuntuacion(nuevaPuntuacion)
            // Mostrar mensaje de confirmación
            ratingFeedbackMessage = "¡Gracias por tu valoración!"
        }
        showRatingFeedback = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .padding(8.dp)
                            .clip(CircleShape)
                            .background(cafeClaro.copy(alpha = 0.3f))
                    ) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Regresar", tint = cafeOscuro)
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            onNavigateToAgenda(platillo.IdReceta)
                        },
                        modifier = Modifier
                            .padding(8.dp)
                            .clip(CircleShape)
                            .background(cafeClaro.copy(alpha = 0.3f))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Bookmark,
                            contentDescription = "Guardar",
                            tint = cafeOscuro
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = blanco.copy(alpha = 0.95f)
                )
            )
        },
        containerColor = blanco
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            item {
                // Imagen de la receta con carga desde Firebase
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .background(grisFondo),
                    contentAlignment = Alignment.Center
                ) {
                    // Si hay una URL de imagen en el platillo, cárgala con AsyncImage
                    if (platillo.Imagen != null && platillo.Imagen.isNotEmpty()) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(platillo.Imagen)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Imagen de ${platillo.Titulo}",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        // Fallback si no hay imagen
                        Text("Sin imagen disponible", color = cafeOscuro)
                    }

                    // Overlay de gradiente en la parte inferior para mejor legibilidad
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                androidx.compose.ui.graphics.Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.3f)
                                    ),
                                    startY = 250f,
                                    endY = 500f
                                )
                            )
                    )
                }

                // Contenido principal con bordes redondeados superpuestos a la imagen
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-25).dp),
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    colors = CardDefaults.cardColors(containerColor = blanco),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        // Título y botón de favorito
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = platillo.Titulo,
                                color = cafeOscuro,
                                fontWeight = FontWeight.Bold,
                                fontSize = 26.sp,
                                modifier = Modifier.weight(1f)
                            )

                            IconButton(
                                onClick = {
                                    if (esFavorito) {
                                        val idFavorito =
                                            favoritoViewModel.getIdFavoritoByReceta(platillo.IdReceta)
                                        if (idFavorito != null) {
                                            // Eliminar de favoritos
                                            favoritoViewModel.removeFavorito(
                                                idFavorito,
                                                idUsuarioActual
                                            )
                                            // Recargar la lista de favoritos
                                            favoritoViewModel.loadFavoritosPorUsuario(
                                                idUsuarioActual
                                            )
                                        }
                                    } else {
                                        // Verificar si no está ya en favoritos
                                        val favoritoExistente =
                                            favoritos.any { it.IdReceta == platillo.IdReceta && it.IdUsuario == idUsuarioActual }
                                        if (!favoritoExistente) {
                                            // Agregar a favoritos
                                            favoritoViewModel.addFavorito(
                                                idUsuarioActual,
                                                platillo.IdReceta
                                            )
                                            // Recargar la lista de favoritos
                                            favoritoViewModel.loadFavoritosPorUsuario(
                                                idUsuarioActual
                                            )
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .shadow(4.dp, CircleShape)
                                    .clip(CircleShape)
                                    .background(if (esFavorito) Color.Red.copy(alpha = 0.1f) else blanco)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Favorite,
                                    contentDescription = "Favorito",
                                    tint = if (esFavorito) Color.Red else cafeClaro,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // SISTEMA DE PUNTUACIÓN MEJORADO
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = cafeClaro.copy(alpha = 0.1f)
                            ),
                            elevation = CardDefaults.cardElevation(0.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Mostrar la puntuación promedio con diseño mejorado
                                if (puntuacionPromedio > 0) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        // Estrella grande para mostrar puntuación promedio
                                        Box(
                                            modifier = Modifier
                                                .size(60.dp)
                                                .clip(CircleShape)
                                                .background(amarilloFuerte),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(
                                                    text = String.format("%.1f", puntuacionPromedio),
                                                    fontSize = 18.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = cafeOscuro
                                                )
                                                Icon(
                                                    imageVector = Icons.Filled.Star,
                                                    contentDescription = null,
                                                    tint = cafeOscuro,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(16.dp))

                                        // Información sobre la puntuación
                                        Column {
                                            Text(
                                                text = "Valoración de usuarios",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = cafeOscuro
                                            )
                                            Text(
                                                text = "${puntuaciones.count { it.IdReceta == platillo.IdReceta }} opiniones",
                                                fontSize = 14.sp,
                                                color = cafeMedio
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))
                                    Divider(color = cafeClaro.copy(alpha = 0.3f), thickness = 1.dp)
                                    Spacer(modifier = Modifier.height(16.dp))
                                }

                                // Título de la sección de calificación
                                Text(
                                    text = if (puntuacionUsuario == null) "¿Qué te pareció esta receta?" else "Tu valoración",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = cafeOscuro
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Componente de calificación por estrellas mejorado
                                AnimatedStarRating(
                                    rating = userRating,
                                    onRatingChanged = { newRating ->
                                        userRating = newRating
                                    }
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Botón para enviar la puntuación con animación
                                Button(
                                    onClick = enviarPuntuacion,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = amarilloFuerte,
                                        contentColor = cafeOscuro
                                    ),
                                    shape = RoundedCornerShape(24.dp),
                                    modifier = Modifier.height(48.dp),
                                    elevation = ButtonDefaults.buttonElevation(4.dp)
                                ) {
                                    Text(
                                        text = if (puntuacionUsuario == null) "Enviar valoración" else "Actualizar valoración",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }

                                // Mensaje de confirmación animado
                                AnimatedVisibility(
                                    visible = showRatingFeedback,
                                    enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.expandVertically(),
                                    exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.shrinkVertically()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = ratingFeedbackMessage,
                                            color = cafeOscuro,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 16.sp
                                        )
                                    }

                                    // Ocultar el mensaje después de unos segundos
                                    LaunchedEffect(showRatingFeedback) {
                                        kotlinx.coroutines.delay(2000)
                                        showRatingFeedback = false
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Descripción
                        Text(
                            text = "Descripción",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = cafeOscuro
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = platillo.Descripcion,
                            fontSize = 16.sp,
                            color = cafeMedio,
                            lineHeight = 24.sp
                        )
                        Spacer(modifier = Modifier.height(32.dp))

                        // INGREDIENTES con diseño mejorado
                        SectionHeader(title = "Ingredientes", count = ingredientes.count { it.IdReceta == platillo.IdReceta })

                        Spacer(modifier = Modifier.height(12.dp))

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = amarilloClaro)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                ingredientes
                                    .filter { it.IdReceta == platillo.IdReceta }
                                    .forEach { ingrediente ->
                                        IngredienteItem(nombre = ingrediente.Nombre, cantidad = ingrediente.Cantidad)
                                    }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // PASOS con diseño mejorado
                        SectionHeader(title = "Preparación", count = pasos.count { it.IdReceta == platillo.IdReceta })

                        Spacer(modifier = Modifier.height(12.dp))

                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            pasos
                                .filter { it.IdReceta == platillo.IdReceta }
                                .sortedBy { it.Paso }
                                .forEach { paso ->
                                    PasoItem(numeroPaso = paso.Paso, descripcion = paso.Descripcion)
                                }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // COMENTARIOS con diseño mejorado
                        SectionHeader(
                            title = "Comentarios",
                            count = comentarios.count { it.IdReceta == platillo.IdReceta }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        val usuarios by usuarioViewModel.usuarios.collectAsState(initial = emptyList())

                        if (comentarios.any { it.IdReceta == platillo.IdReceta }) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                comentarios
                                    .filter { it.IdReceta == platillo.IdReceta }
                                    .forEach { comentario ->
                                        val nombreUsuario = usuarios.find { it.IdUsuario == comentario.IdUsuario }?.Nombre ?: "Usuario desconocido"
                                        ComentarioItem(
                                            nombre = nombreUsuario,
                                            fecha = comentario.Fecha.take(10),
                                            texto = comentario.Texto
                                        )
                                    }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Sé el primero en comentar",
                                    fontSize = 16.sp,
                                    color = cafeClaro,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // CAMPO PARA COMENTAR mejorado
                        OutlinedTextField(
                            value = comment,
                            onValueChange = { comment = it },
                            placeholder = { Text("¿Qué te pareció esta receta?", color = cafeClaro) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp)),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                containerColor = amarilloClaro.copy(alpha = 0.5f),
                                focusedBorderColor = amarilloFuerte,
                                unfocusedBorderColor = cafeClaro.copy(alpha = 0.2f),
                                cursorColor = cafeOscuro
                            ),
                            minLines = 3
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (comment.isNotBlank()) {
                                    comentarioViewModel.addComentario(
                                        texto = comment,
                                        idUsuario = idUsuarioActual,
                                        idReceta = platillo.IdReceta
                                    ) {
                                        comentarioViewModel.loadComentariosByReceta(platillo.IdReceta)
                                    }
                                    comment = ""
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = cafeOscuro,
                                contentColor = blanco
                            ),
                            elevation = ButtonDefaults.buttonElevation(4.dp)
                        ) {
                            Text(
                                text = "Publicar comentario",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedStarRating(
    rating: Int,
    onRatingChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..5) {
            val selected = i <= rating
            val scale by animateFloatAsState(
                targetValue = if (selected) 1.2f else 1f,
                label = "starScale"
            )

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .clickable { onRatingChanged(i) }
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = if (selected) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = "Estrella $i",
                    tint = if (selected) amarilloFuerte else cafeClaro.copy(alpha = 0.5f),
                    modifier = Modifier
                        .size(40.dp)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                )
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = cafeOscuro
        )

        if (count > 0) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(amarilloFuerte.copy(alpha = 0.2f))
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = count.toString(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = cafeOscuro
                )
            }
        }
    }
}

@Composable
fun InfoItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, description: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = cafeOscuro,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = cafeOscuro
        )

        Text(
            text = description,
            fontSize = 12.sp,
            color = cafeMedio
        )
    }
}

@Composable
fun IngredienteItem(nombre: String, cantidad: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(amarilloFuerte),
            contentAlignment = Alignment.Center
        ) {
            Text("✓", color = cafeOscuro, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = nombre,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = cafeOscuro
            )

            Text(
                text = cantidad,
                fontSize = 14.sp,
                color = cafeMedio
            )
        }
    }
}

@Composable
fun PasoItem(numeroPaso: Int, descripcion: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = grisFondo),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(amarilloFuerte),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = numeroPaso.toString(),
                    color = cafeOscuro,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = descripcion,
                fontSize = 16.sp,
                color = cafeMedio,
                lineHeight = 24.sp
            )
        }
    }
}

@Composable
fun ComentarioItem(nombre: String, fecha: String, texto: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = grisFondo),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(amarilloFuerte),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = nombre.first().uppercaseChar().toString(),
                        color = cafeOscuro,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = nombre,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = cafeOscuro
                    )

                    Text(
                        text = fecha,
                        fontSize = 14.sp,
                        color = cafeMedio
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = texto,
                fontSize = 16.sp,
                color = cafeMedio,
                lineHeight = 24.sp
            )
        }
    }
}

// Star Rating component to be added to RecetaDetailScreen function
@Composable
fun StarRating(
    rating: Int,
    onRatingChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..5) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Estrella $i",
                tint = if (i <= rating) amarilloFuerte else cafeClaro.copy(alpha = 0.5f),
                modifier = Modifier
                    .size(36.dp)
                    .clickable { onRatingChanged(i) }
            )
        }
    }
}
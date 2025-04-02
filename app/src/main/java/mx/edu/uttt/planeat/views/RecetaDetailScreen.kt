package mx.edu.uttt.planeat.views

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.edu.uttt.planeat.models.Platillo
import mx.edu.uttt.planeat.response.UserPreferences
import mx.edu.uttt.planeat.viewmodels.*
import androidx.compose.ui.platform.LocalContext

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
    onNavigateToAgenda: (Int) -> Unit
) {
    var comment by remember { mutableStateOf("") }

    // Obtener el idUsuario guardado en SharedPreferences
    val context = LocalContext.current
    val userPreferences = UserPreferences(context)
    val idUsuarioActual = userPreferences.getUserId()

    // Obtén los ingredientes, pasos, comentarios y favoritos
    val ingredientes by ingredienteViewModel.ingredientes.collectAsState(initial = emptyList())
    val pasos by pasoViewModel.pasos.collectAsState(initial = emptyList())
    val comentarios by comentarioViewModel.comentarios.collectAsState(initial = emptyList())
    val favoritos by favoritoViewModel.favoritos.collectAsState(initial = emptyList())

    // Comprobar si la receta está en favoritos
    val esFavorito by remember(favoritos, platillo.IdReceta) {
        derivedStateOf {
            favoritos.any { it.IdReceta == platillo.IdReceta && it.IdUsuario == idUsuarioActual }
        }
    }

    LaunchedEffect(platillo.IdReceta) {
        ingredienteViewModel.loadIngredientes()
        pasoViewModel.loadPasos()
        comentarioViewModel.loadComentariosByReceta(platillo.IdReceta)
        favoritoViewModel.loadFavoritosSinFiltro()
        usuarioViewModel.loadUsuarios()
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
                // Imagen de la receta con diseño mejorado
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .background(grisFondo),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Imagen Platillo", color = cafeOscuro)
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
                                        val idFavorito = favoritoViewModel.getIdFavoritoByReceta(platillo.IdReceta)
                                        if (idFavorito != null) {
                                            favoritoViewModel.removeFavorito(idFavorito, idUsuarioActual)
                                            favoritoViewModel.loadFavoritosByUsuario(idUsuarioActual)
                                        }
                                    } else {
                                        val favoritoExistente = favoritos.any { it.IdReceta == platillo.IdReceta && it.IdUsuario == idUsuarioActual }
                                        if (!favoritoExistente) {
                                            favoritoViewModel.addFavorito(idUsuarioActual, platillo.IdReceta)
                                            favoritoViewModel.loadFavoritosByUsuario(idUsuarioActual)
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

                        // Estadísticas de la receta
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {





                        }

                        Spacer(modifier = Modifier.height(24.dp))

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

// Componentes auxiliares para mejorar la modularidad y reutilización

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
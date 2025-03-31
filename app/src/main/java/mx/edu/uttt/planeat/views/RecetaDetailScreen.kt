package mx.edu.uttt.planeat.views

import android.annotation.SuppressLint
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.edu.uttt.planeat.models.Platillo
import mx.edu.uttt.planeat.response.UserPreferences
import mx.edu.uttt.planeat.viewmodels.*
import androidx.compose.ui.platform.LocalContext

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
    val amarilloFuerte = Color(0xFFFFD94C)
    val cafeOscuro = Color(0xFF4E3629)
    val blanco = Color.White
    val grisFondo = Color(0xFFF9F9F9)

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
            favoritos.any { it.IdReceta == platillo.IdReceta }
        }
    }

    

    LaunchedEffect(platillo.IdReceta) {
        ingredienteViewModel.loadIngredientes()
        pasoViewModel.loadPasos()
        comentarioViewModel.loadComentariosByReceta(platillo.IdReceta)
        favoritoViewModel.loadFavoritosSinFiltro()
        usuarioViewModel.loadUsuarios() // 👈 Esta línea es clave
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = platillo.Titulo,
                        color = cafeOscuro,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Regresar", tint = cafeOscuro)
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = blanco)
            )
        },
        containerColor = blanco
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            item {
                // Imagen de la receta
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(grisFondo),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Imagen Platillo", color = cafeOscuro)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botón de Favorito
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .clickable {
                                if (esFavorito) {
                                    // Eliminar de favoritos
                                    val idFavorito = favoritoViewModel.getIdFavoritoByReceta(platillo.IdReceta)
                                    if (idFavorito != null) {
                                        favoritoViewModel.removeFavorito(idFavorito, idUsuarioActual)
                                        // Después de eliminar, actualizar el estado de favoritos
                                        favoritoViewModel.loadFavoritosByUsuario(idUsuarioActual)
                                    }
                                } else {
                                    // Agregar a favoritos
                                    favoritoViewModel.addFavorito(idUsuarioActual, platillo.IdReceta)
                                    // Después de agregar, actualizar el estado de favoritos
                                    favoritoViewModel.loadFavoritosByUsuario(idUsuarioActual)
                                }
                            }
                            .background(if (esFavorito) Color.Red.copy(alpha = 0.2f) else cafeOscuro.copy(alpha = 0.1f))
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Favorito",
                            tint = if (esFavorito) Color.Red else cafeOscuro
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (esFavorito) "Favorito" else "Agregar a favoritos",
                            color = cafeOscuro,
                            fontSize = 14.sp
                        )
                    }

                    // Icono de Guardar
                    IconButton(
                        onClick = {
                            onNavigateToAgenda(platillo.IdReceta)
                        },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(cafeOscuro.copy(alpha = 0.1f))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Bookmark,
                            contentDescription = "Guardar",
                            tint = cafeOscuro
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = platillo.Descripcion,
                    fontSize = 16.sp,
                    color = cafeOscuro
                )

                Spacer(modifier = Modifier.height(16.dp))

                // INGREDIENTES
                Text(
                    text = "Ingredientes",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = cafeOscuro
                )
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = amarilloClaro),
                    elevation = CardDefaults.elevatedCardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        ingredientes
                            .filter { it.IdReceta == platillo.IdReceta }
                            .forEach { ingrediente ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(amarilloFuerte),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("✓", color = cafeOscuro, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Text(
                                        text = "${ingrediente.Nombre}: ${ingrediente.Cantidad}",
                                        fontSize = 14.sp,
                                        color = cafeMedio
                                    )
                                }
                            }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

// PASOS
                Text(
                    text = "Pasos",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = cafeOscuro
                )
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = amarilloClaro),
                    elevation = CardDefaults.elevatedCardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        pasos
                            .filter { it.IdReceta == platillo.IdReceta }
                            .sortedBy { it.Paso }
                            .forEach { paso ->
                                Row(
                                    verticalAlignment = Alignment.Top,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(amarilloFuerte),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = paso.Paso.toString(),
                                            color = cafeOscuro,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Text(
                                        text = paso.Descripcion,
                                        fontSize = 14.sp,
                                        color = cafeMedio
                                    )
                                }
                            }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

// COMENTARIOS
                Text(
                    text = "Comentarios",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = cafeOscuro
                )

                Spacer(modifier = Modifier.height(8.dp))

                val usuarios by usuarioViewModel.usuarios.collectAsState(initial = emptyList())

                comentarios.filter { it.IdReceta == platillo.IdReceta }.forEach { comentario ->
                    val nombreUsuario = usuarios.find { it.IdUsuario == comentario.IdUsuario }?.Nombre ?: "Usuario desconocido"

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = amarilloClaro),
                        elevation = CardDefaults.elevatedCardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(amarilloFuerte),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = nombreUsuario.first().uppercaseChar().toString(),
                                        color = cafeOscuro,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = nombreUsuario,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = cafeOscuro
                                    )
                                    Text(
                                        text = comentario.Fecha.take(10),
                                        fontSize = 12.sp,
                                        color = cafeMedio
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = comentario.Texto,
                                fontSize = 14.sp,
                                color = cafeMedio
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

// CAMPO PARA COMENTAR
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Deja un comentario...", color = cafeMedio) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = amarilloClaro,
                        unfocusedContainerColor = amarilloClaro,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = cafeOscuro
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        comentarioViewModel.addComentario(
                            texto = comment,
                            idUsuario = idUsuarioActual,
                            idReceta = platillo.IdReceta
                        ) {
                            comentarioViewModel.loadComentariosByReceta(platillo.IdReceta)
                        }
                        comment = ""
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = cafeOscuro)
                ) {
                    Text(
                        text = "Comentar",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

            }
        }
    }
}


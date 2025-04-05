package mx.edu.uttt.planeat.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.edu.uttt.planeat.models.toRecetaSocial
import mx.edu.uttt.planeat.response.UserPreferences
import mx.edu.uttt.planeat.viewmodels.PlatilloViewModel
import mx.edu.uttt.planeat.viewmodels.UsuariosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navigateToLogin: () -> Unit,
    platilloViewModel: PlatilloViewModel = viewModel(),
    usuarioViewModel: UsuariosViewModel = viewModel()
) {
    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }
    val idUsuario = userPreferences.getUserId()

    // Estados para mostrar el diálogo de confirmación
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Colores de la aplicación
    val colorPrimary = Color(0xFFFFEB3B)
    val colorSecondary = Color(0xFF000000)
    val backgroundColor = Color(0xFFFFFFFF)
    val cardColor = Color.White
    val accentColor = Color(0xFF674C3F)

    // Gradiente para el header
    val gradientColors = listOf(colorPrimary, colorPrimary.copy(alpha = 0.7f))

    val usuarios by usuarioViewModel.usuarios.collectAsState()
    val platillos by platilloViewModel.platillos.collectAsState()

    // Intenta acceder a las propiedades con ambos nombres posibles
    val usuario = usuarios.find { it.IdUsuario == idUsuario }
    val nombre = usuario?.let {
        it.Nombre ?: it.Nombre ?: "Usuario"
    } ?: "Usuario"

    val correo = usuario?.let {
        it.Email ?: it.Email ?: "correo@desconocido.com"
    } ?: "correo@desconocido.com"

    val recetasDelUsuario = platillos.filter { it.IdUsuario == idUsuario }

    LaunchedEffect(Unit) {
        usuarioViewModel.loadUsuarios()
        platilloViewModel.loadPlatillos()
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Mi Perfil", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = colorPrimary,
                    titleContentColor = Color.Black
                )
            )
        },
        containerColor = backgroundColor
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header con información de usuario
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(
                            brush = Brush.verticalGradient(gradientColors),
                            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                        )
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Perfil",
                                tint = colorSecondary,
                                modifier = Modifier.size(60.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = nombre,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Text(
                            text = correo,
                            fontSize = 16.sp,
                            color = Color.Black.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Sección de recetas
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Restaurant,
                                contentDescription = "Recetas",
                                tint = colorPrimary
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "Mis Recetas",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorSecondary
                            )
                        }

                        Divider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            color = colorSecondary.copy(alpha = 0.2f)
                        )

                        if (recetasDelUsuario.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No has subido recetas todavía.",
                                    color = colorSecondary.copy(alpha = 0.6f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                recetasDelUsuario.forEach { receta ->
                                    RecetaSocialCard(
                                        receta = receta.toRecetaSocial(nombre),
                                        colorPrimary = colorPrimary,
                                        colorSecondary = colorSecondary,
                                        colorSurface = cardColor,
                                        colorAccent = accentColor,
                                        onNavigateToDetail = { /* Navegación opcional */ }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Botón de cerrar sesión
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Button(
                            onClick = { showLogoutDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Cerrar Sesión",
                                tint = Color.White
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "Cerrar Sesión",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Diálogo de confirmación de cierre de sesión
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("Cerrar Sesión") },
                text = { Text("¿Estás seguro que deseas cerrar tu sesión?") },
                confirmButton = {
                    Button(
                        onClick = {
                            userPreferences.clearUserId()
                            navigateToLogin()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Confirmar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("Cancelar")
                    }
                },
                containerColor = cardColor,
                titleContentColor = colorSecondary,
                textContentColor = colorSecondary.copy(alpha = 0.8f)
            )
        }
    }
}
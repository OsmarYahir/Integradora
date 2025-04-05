package mx.edu.uttt.planeat.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import mx.edu.uttt.planeat.R
import mx.edu.uttt.planeat.models.Platillo
import mx.edu.uttt.planeat.response.UserPreferences
import mx.edu.uttt.planeat.viewmodels.BandejaRecetaViewModel
import mx.edu.uttt.planeat.viewmodels.FechaCalendarioViewModel
import mx.edu.uttt.planeat.viewmodels.PlatilloViewModel
import mx.edu.uttt.planeat.viewmodels.RecomendacionViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import coil.compose.AsyncImage
import coil.request.ImageRequest


@Composable
fun HomeScreen(
    navController: NavController,
    navigateToDetalles: () -> Unit,
    navigateToRecetas: () -> Unit,
    navigateToCalendario: () -> Unit,
    navigateToSubirReceta: () -> Unit,
    navigateToFavoritos: () -> Unit,
    navigateToSimple: () -> Unit,
    navigateToDetalleReceta: (Int) -> Unit,
    navigateToLogout: () -> Unit // Añadida nueva función de navegación para Logout
) {
    // Color palette - elegant warm tones
    val primary = Color(0xFFD4A056)
    val secondary = Color(0xFFF8ECD1)
    val textDark = Color(0xFF3A2E25)
    val textLight = Color(0xFF8D7B6A)
    val background = Color(0xFFFAF7F2)
    val cardBackground = Color.White
    val accentGreen = Color(0xFF7D9D64)

    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }
    val idUsuario = userPreferences.getUserId()

    val bandejaViewModel: BandejaRecetaViewModel = viewModel()
    val platilloViewModel: PlatilloViewModel = viewModel()
    val recomendacionViewModel: RecomendacionViewModel = viewModel()
    val fechaCalendarioViewModel: FechaCalendarioViewModel = viewModel()
    val fechasCalendario by fechaCalendarioViewModel.fechas.collectAsState()


    val recomendaciones by recomendacionViewModel.recomendaciones.collectAsState()
    val bandejas by bandejaViewModel.bandejaRecetas.collectAsState()
    val platillos by platilloViewModel.platillos.collectAsState()

    val hoy = LocalDate.now()
    val formattedDate = hoy.format(DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM"))



    val recetasHoy = remember(bandejas, platillos, fechasCalendario) {
        val bandejasUsuario = bandejas.filter { it.IdUsuario == idUsuario }

        val bandejasDeHoy = bandejasUsuario.filter { bandeja ->
            val fecha = fechasCalendario.find { it.IdCalendario == bandeja.IdCalendario }
            fecha?.Anio == hoy.year &&
                    fecha.Mes == hoy.monthValue &&
                    fecha.Dia == hoy.dayOfMonth
        }

        platillos.filter { platillo ->
            bandejasDeHoy.any { it.IdReceta == platillo.IdReceta }
        }
    }


    LaunchedEffect(true) {
        platilloViewModel.loadPlatillos()
        bandejaViewModel.obtenerTodasLasBandejas()
        fechaCalendarioViewModel.loadFechas() // Agrega esto
        recomendacionViewModel.cargarRecomendaciones()
    }


    Scaffold(
        bottomBar = {
            ElegantBottomNavigation(
                textDark = textDark,
                primary = primary,
                navigateToRecetas = navigateToRecetas,
                navigateToCalendario = navigateToCalendario,
                navigateToSubirReceta = navigateToSubirReceta,
                navigateToFavoritos = navigateToFavoritos,
                navigateToSimple = navigateToSimple
            )
        },
        containerColor = background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Top Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "PlanEat",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = textDark
                    )
                    Text(
                        text = formattedDate.capitalize(),
                        fontSize = 14.sp,
                        color = textLight
                    )
                }

                // Icono de usuario - Ahora con navegación a Logout
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .shadow(8.dp, CircleShape)
                        .clip(CircleShape)
                        .background(cardBackground)
                        .clickable { navigateToLogout() }, // Añadido clickable para navegar a Logout
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Perfil",
                        tint = primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Spacer(modifier = Modifier.height(32.dp))

            // Welcome Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(primary, primary.copy(alpha = 0.8f))
                            )
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "¡Hola Chef!",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Descubre nuevas recetas para preparar hoy",
                                fontSize = 16.sp,
                                color = Color.White.copy(alpha = 0.9f),
                                lineHeight = 24.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = navigateToRecetas,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White
                                ),
                                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "Explorar",
                                    color = primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "Logo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .shadow(8.dp, CircleShape)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Agenda de Hoy
            SectionTitle(title = "Recetas para Hoy", textDark = textDark)

            Spacer(modifier = Modifier.height(16.dp))

            if (recetasHoy.isEmpty()) {
                EmptyRecetasCard(cardBackground, textDark, textLight, primary, navigateToCalendario)
            } else {
                RecetasHoyList(recetasHoy, cardBackground, textDark, textLight, navigateToDetalleReceta)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Recomendaciones
            SectionTitle(title = "Recomendaciones para Ti", textDark = textDark)

            Spacer(modifier = Modifier.height(16.dp))

            if (recomendaciones.isEmpty()) {
                EmptyRecomendacionesCard(cardBackground, textDark, textLight, primary)
            } else {
                RecomendacionesList(recomendaciones, cardBackground, textDark, textLight, primary, navigateToDetalleReceta)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Add New Recipe Button
            ElevatedButton(
                onClick = navigateToSubirReceta,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = primary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.elevatedButtonElevation(
                    defaultElevation = 6.dp,
                    pressedElevation = 8.dp
                )
            ) {
                Text(
                    "Crear Nueva Receta",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun SectionTitle(title: String, textDark: Color) {
    Text(
        text = title,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = textDark
    )
}

@Composable
fun RecetasHoyList(
    recetas: List<Platillo>,
    cardBackground: Color,
    textDark: Color,
    textLight: Color,
    navigateToDetalleReceta: (Int) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
    ) {
        items(recetas) { receta ->
            RecetaCard(
                receta = receta,
                cardBackground = cardBackground,
                textDark = textDark,
                textLight = textLight,
                onClick = { navigateToDetalleReceta(receta.IdReceta) }
            )
        }
    }
}

@Composable
fun RecetaCard(
    receta: Platillo,
    cardBackground: Color,
    textDark: Color,
    textLight: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(220.dp)
            .clickable(onClick = onClick)
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackground)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (!receta.Imagen.isNullOrBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(receta.Imagen)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Imagen de la receta",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Sin imagen",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(60.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = receta.Titulo,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = receta.Descripcion ?: "",
                    fontSize = 13.sp,
                    color = textLight,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}


@Composable
fun EmptyRecetasCard(
    cardBackground: Color,
    textDark: Color,
    textLight: Color,
    primary: Color,
    navigateToCalendario: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No tienes recetas programadas para hoy",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = textDark,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Organiza tu menú semanal para tener todo listo",
                fontSize = 14.sp,
                color = textLight,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Button(
                onClick = navigateToCalendario,
                colors = ButtonDefaults.buttonColors(containerColor = primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Planificar Menú")
            }
        }
    }
}

@Composable
fun RecomendacionesList(
    recomendaciones: List<Any>, // Replace with your actual Recomendacion model
    cardBackground: Color,
    textDark: Color,
    textLight: Color,
    primary: Color,
    navigateToDetalleReceta: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            recomendaciones.forEach { recomendacion ->
                // Access the fields using reflection or modify based on your actual model
                val motivo = "Basado en tus preferencias"
                val idReceta = 2 // Replace with actual ID

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                        .clickable { navigateToDetalleReceta(idReceta) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(primary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = motivo,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = textDark
                        )

                        Text(
                            text = "Toca para ver detalles",
                            fontSize = 13.sp,
                            color = textLight
                        )
                    }
                }

                // Add a divider between items
                if (recomendaciones.indexOf(recomendacion) < recomendaciones.size - 1) {
                    Divider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = textLight.copy(alpha = 0.2f)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyRecomendacionesCard(
    cardBackground: Color,
    textDark: Color,
    textLight: Color,
    primary: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Aún no hay recomendaciones",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = textDark,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Completa tu perfil para recibir sugerencias personalizadas",
                fontSize = 14.sp,
                color = textLight,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
fun ElegantBottomNavigation(
    textDark: Color,
    primary: Color,
    navigateToRecetas: () -> Unit,
    navigateToCalendario: () -> Unit,
    navigateToSubirReceta: () -> Unit,
    navigateToFavoritos: () -> Unit,
    navigateToSimple: () -> Unit
) {
    var selectedItem by remember { mutableStateOf(0) }

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 16.dp,
        modifier = Modifier
            .shadow(16.dp)
            .height(70.dp)
    ) {
        NavigationBarItem(
            selected = selectedItem == 0,
            onClick = { selectedItem = 0 },
            icon = {
                Icon(
                    Icons.Filled.Home,
                    contentDescription = "Inicio",
                    modifier = Modifier.size(26.dp)
                )
            },
            label = { Text("Inicio", fontSize = 12.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = primary,
                selectedTextColor = primary,
                unselectedIconColor = textDark.copy(alpha = 0.5f),
                unselectedTextColor = textDark.copy(alpha = 0.5f),
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = selectedItem == 1,
            onClick = {
                selectedItem = 1
                navigateToRecetas()
            },
            icon = {
                Icon(
                    Icons.Filled.Menu,
                    contentDescription = "Recetas",
                    modifier = Modifier.size(26.dp)
                )
            },
            label = { Text("Recetas", fontSize = 12.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = primary,
                selectedTextColor = primary,
                unselectedIconColor = textDark.copy(alpha = 0.5f),
                unselectedTextColor = textDark.copy(alpha = 0.5f),
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = selectedItem == 2,
            onClick = {
                selectedItem = 2
                navigateToFavoritos()
            },
            icon = {
                Icon(
                    Icons.Filled.Favorite,
                    contentDescription = "Favoritos",
                    modifier = Modifier.size(26.dp)
                )
            },
            label = { Text("Favs", fontSize = 12.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = primary,
                selectedTextColor = primary,
                unselectedIconColor = textDark.copy(alpha = 0.5f),
                unselectedTextColor = textDark.copy(alpha = 0.5f),
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = selectedItem == 3,
            onClick = {
                selectedItem = 3
                navigateToSimple()
            },
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.calendario),
                    contentDescription = "Calendario",
                    modifier = Modifier.size(26.dp)
                )
            },
            label = { Text("Agenda", fontSize = 12.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = primary,
                selectedTextColor = primary,
                unselectedIconColor = textDark.copy(alpha = 0.5f),
                unselectedTextColor = textDark.copy(alpha = 0.5f),
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = selectedItem == 4,
            onClick = {
                selectedItem = 4
                navigateToSubirReceta()
            },
            icon = {
                Icon(
                    Icons.Filled.Person,
                    contentDescription = "Subir Receta",
                    modifier = Modifier.size(26.dp)
                )
            },
            label = { Text("Subir", fontSize = 12.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = primary,
                selectedTextColor = primary,
                unselectedIconColor = textDark.copy(alpha = 0.5f),
                unselectedTextColor = textDark.copy(alpha = 0.5f),
                indicatorColor = Color.Transparent
            )
        )
    }
}


package mx.edu.uttt.planeat.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.edu.uttt.planeat.models.Platillo
import mx.edu.uttt.planeat.response.UserPreferences
import mx.edu.uttt.planeat.viewmodels.BandejaRecetaViewModel
import mx.edu.uttt.planeat.viewmodels.FechaCalendarioViewModel
import mx.edu.uttt.planeat.viewmodels.PlatilloViewModel
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaSimpleScreen(
    onBack: () -> Unit = {}
) {
    // Elegant color palette (matching the previous screen)
    val cafeOscuro = Color(0xFF3E2723)
    val cafeClaro = Color(0xFF8D6E63)
    val blanco = Color(0xFFFFFBFA)
    val grisFondo = Color(0xFFF5F5F5)
    val amarilloFuerte = Color(0xFFFFC107)
    val amarilloSuave = Color(0xFFFFECB3)

    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            cafeClaro.copy(alpha = 0.05f),
            grisFondo
        )
    )

    val bandejaViewModel: BandejaRecetaViewModel = viewModel()
    val platilloViewModel: PlatilloViewModel = viewModel()
    val fechaCalendarioViewModel: FechaCalendarioViewModel = viewModel()
    val userPreferences = UserPreferences(LocalContext.current)
    val idUsuario = userPreferences.getUserId()

    val bandejas by bandejaViewModel.bandejaRecetas.collectAsState()
    val platillos by platilloViewModel.platillos.collectAsState()

    // Obtener la fecha de hoy
    val hoy = LocalDate.now()
    val fechaFormateada = remember {
        val diaSemana = hoy.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("es", "MX"))
            .replaceFirstChar { it.uppercase() }
        val dia = hoy.dayOfMonth
        val mes = hoy.month.getDisplayName(TextStyle.FULL, Locale("es", "MX"))
            .replaceFirstChar { it.uppercase() }
        val year = hoy.year
        "$diaSemana, $dia de $mes de $year"
    }

    // Cargar las fechas desde el viewModel
    val fechasCalendario by fechaCalendarioViewModel.fechas.collectAsState()

    var isLoading by remember { mutableStateOf(true) }

    // We'll use this to keep track of the actual data
    var recetasHoy by remember { mutableStateOf<List<Platillo>>(emptyList()) }

    // Load the data first before processing it
    LaunchedEffect(bandejas, platillos, fechasCalendario) {
        if (bandejas.isNotEmpty() && platillos.isNotEmpty() && fechasCalendario.isNotEmpty()) {
            // This is the original logic from the provided code
            val hoyBandejas = bandejas.filter { it.IdUsuario == idUsuario }

            val hoyBandejasConFecha = hoyBandejas.filter { bandeja ->
                val fechaCalendario = fechasCalendario.find { it.IdCalendario == bandeja.IdCalendario }
                fechaCalendario?.Anio == hoy.year &&
                        fechaCalendario.Mes == hoy.monthValue &&
                        fechaCalendario.Dia == hoy.dayOfMonth
            }

            recetasHoy = platillos.filter { platillo ->
                hoyBandejasConFecha.any { it.IdReceta == platillo.IdReceta }
            }

            isLoading = false
        }
    }

    LaunchedEffect(true) {
        bandejaViewModel.obtenerTodasLasBandejas()
        platilloViewModel.loadPlatillos()
        fechaCalendarioViewModel.loadFechas()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Agenda de Hoy",
                        color = cafeOscuro,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        letterSpacing = 0.5.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = cafeOscuro
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = blanco,
                    scrolledContainerColor = blanco
                ),
                modifier = Modifier.shadow(elevation = 2.dp)
            )
        },
        containerColor = grisFondo
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = gradientBackground)
                .padding(padding)
        ) {
            if (isLoading) {
                // Loading state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = amarilloFuerte)
                }
            } else {
                // Content
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Date header
                    item {
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(animationSpec = tween(durationMillis = 400))
                        ) {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = amarilloSuave
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(amarilloFuerte)
                                            .padding(8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.CalendarToday,
                                            contentDescription = null,
                                            tint = cafeOscuro
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            "Hoy",
                                            color = cafeOscuro,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                        Text(
                                            fechaFormateada,
                                            color = cafeOscuro.copy(alpha = 0.8f),
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Recipe list
                    item {
                        AnimatedVisibility(
                            visible = true,
                            enter = slideInVertically(
                                initialOffsetY = { 40 },
                                animationSpec = tween(durationMillis = 400, delayMillis = 100)
                            ) + fadeIn(animationSpec = tween(durationMillis = 400, delayMillis = 100))
                        ) {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = blanco),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = "Recetas guardadas para hoy",
                                        color = cafeOscuro,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )

                                    Divider(color = cafeClaro.copy(alpha = 0.1f))

                                    Spacer(modifier = Modifier.height(8.dp))

                                    if (recetasHoy.isEmpty()) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 24.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                "No tienes recetas guardadas para hoy",
                                                color = cafeClaro.copy(alpha = 0.7f),
                                                fontSize = 14.sp
                                            )
                                        }
                                    } else {
                                        recetasHoy.forEachIndexed { index, receta ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    Icons.Default.RestaurantMenu,
                                                    contentDescription = null,
                                                    tint = cafeClaro,
                                                    modifier = Modifier.size(18.dp)
                                                )

                                                Spacer(modifier = Modifier.width(12.dp))

                                                Column {
                                                    Text(
                                                        text = receta.Titulo,
                                                        color = cafeOscuro,
                                                        fontWeight = FontWeight.Medium,
                                                        fontSize = 15.sp
                                                    )

                                                    if (!receta.Descripcion.isNullOrBlank()) {
                                                        Text(
                                                            text = receta.Descripcion,
                                                            color = cafeClaro,
                                                            fontSize = 13.sp,
                                                            maxLines = 1
                                                        )
                                                    }
                                                }
                                            }

                                            // Add dividers between items, but not after the last one
                                            if (index < recetasHoy.size - 1) {
                                                Divider(
                                                    color = cafeClaro.copy(alpha = 0.05f),
                                                    modifier = Modifier.padding(start = 30.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
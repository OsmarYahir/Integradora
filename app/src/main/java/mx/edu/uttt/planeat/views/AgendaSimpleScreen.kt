package mx.edu.uttt.planeat.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.edu.uttt.planeat.models.Platillo
import mx.edu.uttt.planeat.response.UserPreferences
import mx.edu.uttt.planeat.viewmodels.BandejaRecetaViewModel
import mx.edu.uttt.planeat.viewmodels.FechaCalendarioViewModel
import mx.edu.uttt.planeat.viewmodels.PlatilloViewModel
import java.time.LocalDate
import java.time.YearMonth
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
    val verdeClaro = Color(0xFF8BC34A)

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
    val fechasCalendario by fechaCalendarioViewModel.fechas.collectAsState()

    // Variables para el calendario
    val fechaActual = LocalDate.now()
    var fechaSeleccionada by remember { mutableStateOf(fechaActual) }
    val anio = fechaSeleccionada.year
    val mes = fechaSeleccionada.monthValue
    val dia = fechaSeleccionada.dayOfMonth

    // Calcular el número real de días en el mes actual
    val diasEnMesActual = YearMonth.of(anio, mes).lengthOfMonth()

    // Calcular el día de la semana en que comienza el mes (0 = Lunes, 6 = Domingo)
    val primerDiaDelMes = LocalDate.of(anio, mes, 1).dayOfWeek.value % 7

    // Obtener el nombre del mes en español
    val nombreMes = fechaSeleccionada.month.getDisplayName(TextStyle.FULL, Locale("es", "MX"))
        .replaceFirstChar { it.uppercase() }

    // Formatear la fecha seleccionada
    val fechaFormateada = remember(fechaSeleccionada) {
        val diaSemana = fechaSeleccionada.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("es", "MX"))
            .replaceFirstChar { it.uppercase() }
        val diaNum = fechaSeleccionada.dayOfMonth
        val mesText = fechaSeleccionada.month.getDisplayName(TextStyle.FULL, Locale("es", "MX"))
            .replaceFirstChar { it.uppercase() }
        val year = fechaSeleccionada.year
        "$diaSemana, $diaNum de $mesText de $year"
    }

    var isLoading by remember { mutableStateOf(true) }
    var mostrarCalendario by remember { mutableStateOf(false) }

    // We'll use this to keep track of the actual data
    var recetasDelDia by remember { mutableStateOf<List<Platillo>>(emptyList()) }

    // List of dates with scheduled recipes
    var diasConRecetas by remember { mutableStateOf<Set<Int>>(emptySet()) }

    // Load the data first before processing it
    LaunchedEffect(bandejas, platillos, fechasCalendario, fechaSeleccionada) {
        if (bandejas.isNotEmpty() && platillos.isNotEmpty() && fechasCalendario.isNotEmpty()) {
            // Get bandejas for current user
            val usuarioBandejas = bandejas.filter { it.IdUsuario == idUsuario }

            // Map days that have recipes in current month
            val diasConRecetasDelMes = fechasCalendario
                .filter { fecha ->
                    fecha.Anio == anio &&
                            fecha.Mes == mes &&
                            usuarioBandejas.any { it.IdCalendario == fecha.IdCalendario }
                }
                .map { it.Dia }
                .toSet()

            diasConRecetas = diasConRecetasDelMes

            // Get recipes for selected date
            val diaSeleccionadoBandejas = usuarioBandejas.filter { bandeja ->
                val fechaCalendario = fechasCalendario.find { it.IdCalendario == bandeja.IdCalendario }
                fechaCalendario?.Anio == fechaSeleccionada.year &&
                        fechaCalendario.Mes == fechaSeleccionada.monthValue &&
                        fechaCalendario.Dia == fechaSeleccionada.dayOfMonth
            }

            recetasDelDia = platillos.filter { platillo ->
                diaSeleccionadoBandejas.any { it.IdReceta == platillo.IdReceta }
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
                        "Agenda",
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
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                modifier = Modifier.clickable { mostrarCalendario = !mostrarCalendario }
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

                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            if (fechaSeleccionada.equals(LocalDate.now())) "Hoy" else "Fecha seleccionada",
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

                                    Icon(
                                        Icons.Default.CalendarMonth,
                                        contentDescription = "Mostrar calendario",
                                        tint = cafeOscuro
                                    )
                                }
                            }
                        }
                    }

                    // Calendar view (visible only when clicked)
                    item {
                        AnimatedVisibility(
                            visible = mostrarCalendario,
                            enter = slideInVertically(
                                initialOffsetY = { -40 },
                                animationSpec = tween(durationMillis = 300)
                            ) + fadeIn(animationSpec = tween(durationMillis = 300))
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
                                    // Month and year header
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.CalendarMonth,
                                            contentDescription = "Calendario",
                                            tint = amarilloFuerte,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            "$nombreMes $anio",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = cafeOscuro
                                        )
                                    }

                                    Divider(color = cafeClaro.copy(alpha = 0.1f))
                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Calendar implementation
                                    CalendarView(
                                        diasDelMes = (1..diasEnMesActual).toList(),
                                        primerDiaDelMes = primerDiaDelMes,
                                        diasConRecetas = diasConRecetas,
                                        diaSeleccionado = fechaSeleccionada.dayOfMonth,
                                        amarilloFuerte = amarilloFuerte,
                                        cafeOscuro = cafeOscuro,
                                        grisClaro = grisFondo,
                                        verdeClaro = verdeClaro,
                                        onClick = { diaSeleccionado ->
                                            fechaSeleccionada = LocalDate.of(anio, mes, diaSeleccionado)
                                            mostrarCalendario = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Recipe list for selected date
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
                                        text = "Recetas para ${fechaSeleccionada.dayOfMonth} de $nombreMes",
                                        color = cafeOscuro,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )

                                    Divider(color = cafeClaro.copy(alpha = 0.1f))

                                    Spacer(modifier = Modifier.height(8.dp))

                                    if (recetasDelDia.isEmpty()) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 24.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                "No tienes recetas guardadas para este día",
                                                color = cafeClaro.copy(alpha = 0.7f),
                                                fontSize = 14.sp
                                            )
                                        }
                                    } else {
                                        recetasDelDia.forEachIndexed { index, receta ->
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

                                                    // 👇 NUEVO: Mostrar el tipo de comida
                                                    val tipo = bandejas.firstOrNull { it.IdReceta == receta.IdReceta &&
                                                            fechasCalendario.any { fc ->
                                                                fc.IdCalendario == it.IdCalendario &&
                                                                        fc.Anio == fechaSeleccionada.year &&
                                                                        fc.Mes == fechaSeleccionada.monthValue &&
                                                                        fc.Dia == fechaSeleccionada.dayOfMonth
                                                            }
                                                    }?.TipoComida ?: "Tipo no especificado"

                                                    Text(
                                                        text = "Tipo: $tipo",
                                                        color = cafeOscuro.copy(alpha = 0.8f),
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.Light
                                                    )
                                                }
                                            }

                                            if (index < recetasDelDia.size - 1) {
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

@Composable
fun CalendarView(
    diasDelMes: List<Int>,
    primerDiaDelMes: Int,
    diasConRecetas: Set<Int>,
    diaSeleccionado: Int,
    amarilloFuerte: Color,
    cafeOscuro: Color,
    grisClaro: Color,
    verdeClaro: Color,
    onClick: (Int) -> Unit
) {
    val diasSemana = listOf("Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb")

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        // Cabecera de días de la semana
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            diasSemana.forEach { dia ->
                Text(
                    text = dia,
                    fontWeight = FontWeight.Bold,
                    color = cafeOscuro,
                    fontSize = 14.sp,
                    modifier = Modifier.width(40.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Generamos los días vacíos antes del primer día del mes
        val todasLasCeldas = List(primerDiaDelMes) { -1 } + diasDelMes

        todasLasCeldas.chunked(7).forEach { semana ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                semana.forEach { dia ->
                    val tieneRecetas = dia in diasConRecetas
                    val esDiaSeleccionado = dia == diaSeleccionado
                    val esHoy = dia == LocalDate.now().dayOfMonth

                    CalendarDayCell(
                        dia = dia,
                        tieneRecetas = tieneRecetas,
                        amarilloFuerte = amarilloFuerte,
                        cafeOscuro = cafeOscuro,
                        grisClaro = grisClaro,
                        verdeClaro = verdeClaro,
                        esDiaSeleccionado = esDiaSeleccionado,
                        esHoy = esHoy,
                        onClick = onClick
                    )
                }
            }
        }
    }
}

@Composable
fun CalendarDayCell(
    dia: Int,
    tieneRecetas: Boolean,
    amarilloFuerte: Color,
    cafeOscuro: Color,
    grisClaro: Color,
    verdeClaro: Color,
    esDiaSeleccionado: Boolean,
    esHoy: Boolean,
    onClick: (Int) -> Unit
) {
    if (dia < 0) {
        // Celda vacía para completar la cuadrícula
        Box(
            modifier = Modifier
                .size(40.dp)
                .padding(4.dp)
        )
    } else {
        Box(
            modifier = Modifier
                .padding(2.dp)
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    when {
                        esDiaSeleccionado -> verdeClaro
                        tieneRecetas -> amarilloFuerte
                        else -> grisClaro
                    }
                )
                .shadow(
                    elevation = if (esDiaSeleccionado || tieneRecetas) 4.dp else 0.dp,
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable { onClick(dia) },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = dia.toString(),
                    fontWeight = if (esDiaSeleccionado || tieneRecetas || esHoy) FontWeight.Bold else FontWeight.Normal,
                    color = if (esDiaSeleccionado) Color.White else cafeOscuro,
                    fontSize = 14.sp
                )
                if (tieneRecetas) {
                    Box(
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (esDiaSeleccionado) Color.White else cafeOscuro)
                    )
                }
            }
        }
    }
}
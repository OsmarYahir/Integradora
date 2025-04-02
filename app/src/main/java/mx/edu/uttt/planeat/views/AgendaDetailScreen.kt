package mx.edu.uttt.planeat.views

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.edu.uttt.planeat.models.FechaCalendario
import mx.edu.uttt.planeat.viewmodels.FechaCalendarioViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaDetailScreen(
    idReceta: Int,
    onBack: () -> Unit,
    onnavigateToGuardar: (Int, Int, Int, Int, Int) -> Unit
) {
    val amarilloFuerte = Color(0xFFFFD94C)
    val cafeOscuro = Color(0xFF4E3629)
    val blanco = Color.White
    val grisClaro = Color(0xFFF0F0F0)
    val verdeClaro = Color(0xFF8BC34A)

    val fechaViewModel: FechaCalendarioViewModel = viewModel()
    val fechaActual = LocalDate.now()
    val anio = fechaActual.year
    val mes = fechaActual.monthValue

    // Obtener el nombre del mes actual en español
    val nombreMes = fechaActual.month.getDisplayName(TextStyle.FULL, Locale("es", "ES"))
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

    // Calcular el número real de días en el mes actual
    val diasEnMesActual = YearMonth.of(anio, mes).lengthOfMonth()

    // Calcular el día de la semana en que comienza el mes (0 = Lunes, 6 = Domingo)
    val primerDiaDelMes = LocalDate.of(anio, mes, 1).dayOfWeek.value % 7

    val fechas by fechaViewModel.fechas.collectAsState()

    LaunchedEffect(true) {
        fechaViewModel.loadFechas()
    }

    val diasDelMes = (1..diasEnMesActual).toList()
    val actividadesPorDia = fechas.groupBy { it.Dia }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Agenda",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = cafeOscuro
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = amarilloFuerte)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Encabezado con mes y año
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .shadow(4.dp, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = blanco)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Calendario",
                        tint = amarilloFuerte,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = nombreMes,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = cafeOscuro
                        )
                        Text(
                            text = anio.toString(),
                            fontSize = 16.sp,
                            color = cafeOscuro.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            CalendarView(
                diasDelMes = diasDelMes,
                primerDiaDelMes = primerDiaDelMes,
                actividadesPorDia = actividadesPorDia,
                amarilloFuerte = amarilloFuerte,
                cafeOscuro = cafeOscuro,
                grisClaro = grisClaro,
                verdeClaro = verdeClaro,
                onClick = { dia ->
                    // Verifica si la fecha existe, y si no, la crea
                    fechaViewModel.guardarFechaSiNoExiste(anio, mes, dia) { idCalendario ->
                        // Ahora que tienes el IdCalendario, envíalo a la bandeja
                        onnavigateToGuardar(anio, mes, dia, idReceta, idCalendario)
                    }
                }
            )
        }
    }
}

@Composable
fun CalendarView(
    diasDelMes: List<Int>,
    primerDiaDelMes: Int,
    actividadesPorDia: Map<Int, List<FechaCalendario>>,
    amarilloFuerte: Color,
    cafeOscuro: Color,
    grisClaro: Color,
    verdeClaro: Color,
    onClick: (Int) -> Unit
) {
    val diasSemana = listOf("Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
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

            Divider(
                color = grisClaro,
                thickness = 1.dp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Generamos los días vacíos antes del primer día del mes
            val todasLasCeldas = List(primerDiaDelMes) { -1 } + diasDelMes

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                todasLasCeldas.chunked(7).forEach { semana ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        semana.forEach { dia ->
                            val actividades = if (dia > 0) actividadesPorDia[dia] ?: emptyList() else emptyList()
                            CalendarDayCell(
                                dia = dia,
                                actividades = actividades,
                                amarilloFuerte = amarilloFuerte,
                                cafeOscuro = cafeOscuro,
                                grisClaro = grisClaro,
                                verdeClaro = verdeClaro,
                                esHoy = LocalDate.now().dayOfMonth == dia,
                                onClick = onClick
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarDayCell(
    dia: Int,
    actividades: List<FechaCalendario>,
    amarilloFuerte: Color,
    cafeOscuro: Color,
    grisClaro: Color,
    verdeClaro: Color,
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
                        esHoy -> verdeClaro
                        actividades.isNotEmpty() -> amarilloFuerte
                        else -> grisClaro
                    }
                )
                .shadow(
                    elevation = if (esHoy || actividades.isNotEmpty()) 4.dp else 0.dp,
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
                    fontWeight = if (esHoy || actividades.isNotEmpty()) FontWeight.Bold else FontWeight.Normal,
                    color = if (esHoy) Color.White else cafeOscuro,
                    fontSize = 14.sp
                )
                if (actividades.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (esHoy) Color.White else cafeOscuro)
                    )
                }
            }
        }
    }
}
package mx.edu.uttt.planeat.views

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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.edu.uttt.planeat.models.FechaCalendario
import mx.edu.uttt.planeat.viewmodels.FechaCalendarioViewModel
import java.time.LocalDate
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun AgendaDetailScreen(
    idReceta: Int,
    onBack: () -> Unit,
    onnavigateToGuardar: (Int, Int, Int) -> Unit
) {
    val amarilloFuerte = Color(0xFFFFD94C)
    val cafeOscuro = Color(0xFF4E3629)
    val blanco = Color.White
    val grisClaro = Color(0xFFF0F0F0)

    val fechaViewModel: FechaCalendarioViewModel = viewModel()
    val fechas by fechaViewModel.fechas.collectAsState()

    LaunchedEffect(true) {
        fechaViewModel.loadFechas()
    }

    val diasDelMes = (1..31).toList()
    val actividadesPorDia = fechas.groupBy { it.Dia }

    val fechaActual = LocalDate.now()
    val anio = fechaActual.year
    val mes = fechaActual.monthValue

    Scaffold(
        // tu TopAppBar...
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp)
        ) {
            Text(
                text = "Agenda del mes",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = cafeOscuro
            )

            Spacer(modifier = Modifier.height(16.dp))

            CalendarView(
                diasDelMes = diasDelMes,
                actividadesPorDia = actividadesPorDia,
                amarilloFuerte = amarilloFuerte,
                cafeOscuro = cafeOscuro,
                grisClaro = grisClaro,
                onClick = { dia ->
                    onnavigateToGuardar(anio, mes, dia)
                }
            )
        }
    }
}

@Composable
fun CalendarView(
    diasDelMes: List<Int>,
    actividadesPorDia: Map<Int, List<FechaCalendario>>,
    amarilloFuerte: Color,
    cafeOscuro: Color,
    grisClaro: Color,
    onClick: (Int) -> Unit
) {
    val diasSemana = listOf("Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb")

    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            diasSemana.forEach { dia ->
                Text(text = dia, fontWeight = FontWeight.Bold, color = cafeOscuro, fontSize = 14.sp)
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(diasDelMes.chunked(7)) { semana ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    semana.forEach { dia ->
                        val actividades = actividadesPorDia[dia] ?: emptyList()
                        CalendarDayCell(dia, actividades, amarilloFuerte, cafeOscuro, grisClaro, onClick)
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
    onClick: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .size(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (actividades.isNotEmpty()) amarilloFuerte else grisClaro)
            .clickable { onClick(dia) }, // ✅ Solo llamamos al callback
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = dia.toString(),
                fontWeight = FontWeight.Bold,
                color = cafeOscuro,
                fontSize = 14.sp
            )
            if (actividades.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Actividad",
                    tint = cafeOscuro.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AgendaDetailScreenPreview() {
    AgendaDetailScreen(
        idReceta = 1,
        onBack = {},
        onnavigateToGuardar = { _, _, _ -> }
    )
}






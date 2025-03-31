package mx.edu.uttt.planeat.views

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.edu.uttt.planeat.models.Platillo
import mx.edu.uttt.planeat.response.UserPreferences
import mx.edu.uttt.planeat.viewmodels.BandejaRecetaViewModel
import mx.edu.uttt.planeat.viewmodels.PlatilloViewModel
import java.time.LocalDate


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaSimpleScreen(
    onBack: () -> Unit = {}
) {
    val amarilloFuerte = Color(0xFFFFD94C)
    val cafeOscuro = Color(0xFF4E3629)

    val bandejaViewModel: BandejaRecetaViewModel = viewModel()
    val platilloViewModel: PlatilloViewModel = viewModel()
    val userPreferences = UserPreferences(LocalContext.current)
    val idUsuario = userPreferences.getUserId()

    val bandejas by bandejaViewModel.bandejaRecetas.collectAsState()
    val platillos by platilloViewModel.platillos.collectAsState()

    val hoy = LocalDate.now()

    val recetasHoy = remember(bandejas, platillos) {
        val hoyBandejas = bandejas.filter { it.IdUsuario == idUsuario }
        platillos.filter { platillo ->
            hoyBandejas.any { it.IdReceta == platillo.IdReceta }
        }
    }

    LaunchedEffect(true) {
        bandejaViewModel.obtenerTodasLasBandejas()
        platilloViewModel.loadPlatillos()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agenda de Hoy", color = cafeOscuro, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = cafeOscuro)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = amarilloFuerte)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Recetas guardadas para hoy:", color = cafeOscuro, fontWeight = FontWeight.SemiBold)

            Spacer(modifier = Modifier.height(12.dp))

            if (recetasHoy.isEmpty()) {
                Text("No tienes recetas guardadas para hoy.", color = cafeOscuro.copy(alpha = 0.7f))
            } else {
                recetasHoy.forEach { receta ->
                    Text("• ${receta.Titulo}", color = cafeOscuro)
                }
            }
        }
    }
}

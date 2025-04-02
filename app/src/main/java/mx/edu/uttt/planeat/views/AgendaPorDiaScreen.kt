package mx.edu.uttt.planeat.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import mx.edu.uttt.planeat.response.UserPreferences
import mx.edu.uttt.planeat.viewmodels.BandejaRecetaViewModel
import mx.edu.uttt.planeat.viewmodels.PlatilloViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaPorDiaScreen(
    anio: Int,
    mes: Int,
    dia: Int,
    onBack: () -> Unit,
    navigateToRecetaDetail: (Int) -> Unit // Agregado para navegar a detalles de receta
) {
    val cafeOscuro = Color(0xFF4E3629)
    val amarilloFuerte = Color(0xFFFFD94C)

    val bandejaViewModel: BandejaRecetaViewModel = viewModel()
    val platilloViewModel: PlatilloViewModel = viewModel()
    val userPreferences = UserPreferences(LocalContext.current)
    val idUsuario = userPreferences.getUserId()

    val bandejas by bandejaViewModel.bandejaRecetas.collectAsState()
    val platillos by platilloViewModel.platillos.collectAsState()

    val recetasDelDia = remember(bandejas, platillos) {
        val fechaId = bandejas.find {
            it.IdUsuario == idUsuario &&
                    it.IdCalendario.let { idCal ->
                        // Reemplaza esto con una mejor lógica si puedes cruzar con la tabla `FechaCalendario`
                        true // temporalmente acepta todos
                    }
        }?.IdCalendario

        val bandejasDelDia = bandejas.filter {
            it.IdUsuario == idUsuario && it.IdCalendario == fechaId
        }

        platillos.filter { platillo ->
            bandejasDelDia.any { it.IdReceta == platillo.IdReceta }
        }
    }

    LaunchedEffect(true) {
        bandejaViewModel.obtenerTodasLasBandejas()
        platilloViewModel.loadPlatillos()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Recetas del $dia/$mes/$anio", color = cafeOscuro)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = cafeOscuro)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = amarilloFuerte)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            if (recetasDelDia.isEmpty()) {
                Text("No hay recetas para esta fecha.", color = cafeOscuro.copy(alpha = 0.7f))
            } else {
                recetasDelDia.forEach { receta ->
                    Text(
                        text = "• ${receta.Titulo}",
                        color = cafeOscuro,
                        modifier = Modifier.clickable {
                            // Llamada a la función de navegación cuando se hace clic en una receta
                            navigateToRecetaDetail(receta.IdReceta)
                        }
                    )
                }
            }
        }
    }
}


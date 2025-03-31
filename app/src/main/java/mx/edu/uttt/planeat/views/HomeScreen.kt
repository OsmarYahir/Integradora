package mx.edu.uttt.planeat.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import mx.edu.uttt.planeat.R
import mx.edu.uttt.planeat.models.BandejaReceta
import mx.edu.uttt.planeat.models.FechaCalendario
import mx.edu.uttt.planeat.models.Platillo
import mx.edu.uttt.planeat.response.UserPreferences
import mx.edu.uttt.planeat.viewmodels.BandejaRecetaViewModel
import mx.edu.uttt.planeat.viewmodels.PlatilloViewModel
import mx.edu.uttt.planeat.viewmodels.RecomendacionViewModel
import java.time.LocalDate

@Composable
fun HomeScreen(
    navController: NavController,
    navigateToDetalles: () -> Unit,
    navigateToRecetas: () -> Unit,
    navigateToCalendario: () -> Unit,
    navigateToSubirReceta: () -> Unit,
    navigateToFavoritos: () -> Unit,
    navigateToSimple: () -> Unit,
    navigateToDetalleReceta: (Int) -> Unit

) {
    val amarilloFuerte = Color(0xFFFFD94C)
    val amarilloClaro = Color(0xFFFFF8E1)
    val cafeOscuro = Color(0xFF4E3629)
    val cafeMedio = Color(0xFF8D6E63)
    val grisFondo = Color(0xFFF9F9F9)

    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }
    val idUsuario = userPreferences.getUserId()

    val bandejaViewModel: BandejaRecetaViewModel = viewModel()
    val platilloViewModel: PlatilloViewModel = viewModel()


    val recomendacionViewModel: RecomendacionViewModel = viewModel()
    val recomendaciones by recomendacionViewModel.recomendaciones.collectAsState()


    val bandejas by bandejaViewModel.bandejaRecetas.collectAsState()
    val platillos by platilloViewModel.platillos.collectAsState()

    val hoy = LocalDate.now()
    val recetasHoy = remember(bandejas, platillos) {
        val hoyBandejas = bandejas.filter {
            it.IdUsuario == idUsuario &&
                    it.IdCalendario.let { idCal ->
                        // Aquí suponemos que el ID calendario está mapeado a una fecha de hoy.
                        // Si tuvieras que cruzar con FechaCalendario, deberías traer esa relación también.
                        true // puedes mejorar esto si manejas FechaCalendario con más detalle
                    }
        }


        // Mapeamos los IdReceta a los Platillos correspondientes
        platillos.filter { platillo ->
            hoyBandejas.any { it.IdReceta == platillo.IdReceta }
        }
    }

    LaunchedEffect(true) {
        platilloViewModel.loadPlatillos()
        bandejaViewModel.obtenerTodasLasBandejas()
        recomendacionViewModel.cargarRecomendaciones()
    }




    Scaffold(
        bottomBar = {
            BottomNavigationBarMinimal(
                cafeOscuro = cafeOscuro,
                amarilloFuerte = amarilloFuerte,
                navigateToRecetas = navigateToRecetas,
                navigateToCalendario = navigateToCalendario,
                navigateToSubirReceta = navigateToSubirReceta,
                navigateToFavoritos = navigateToFavoritos,
                navigateToSimple = navigateToSimple
            )
        },
        containerColor = grisFondo
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PlanEat",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = cafeOscuro
                )

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(amarilloFuerte),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Perfil",
                        tint = cafeOscuro
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = amarilloFuerte)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "¡Hola!",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = cafeOscuro
                        )
                        Text(
                            text = "¿Qué vas a cocinar hoy?",
                            fontSize = 16.sp,
                            color = cafeOscuro
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            AgendaHoyCard(
                recetas = recetasHoy,
                amarilloFuerte = amarilloFuerte,
                cafeOscuro = cafeOscuro
            )


            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.elevatedCardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Recomendaciones",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = cafeOscuro
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (recomendaciones.isEmpty()) {
                        Text("No hay recomendaciones aún", color = cafeOscuro.copy(alpha = 0.7f))
                    } else {
                        recomendaciones.forEach { recomendacion ->
                            Text(
                                text = "• ${recomendacion.Motivo}",
                                fontSize = 14.sp,
                                color = cafeMedio,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        navigateToDetalleReceta(recomendacion.IdReceta)
                                    }
                                    .padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }





            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = navigateToSubirReceta,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = amarilloFuerte),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Subir nueva receta", color = cafeOscuro, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun BottomNavigationBarMinimal(
    cafeOscuro: Color,
    amarilloFuerte: Color,
    navigateToRecetas: () -> Unit,
    navigateToCalendario: () -> Unit,
    navigateToSubirReceta: () -> Unit,
    navigateToFavoritos: () -> Unit,
    navigateToSimple: () -> Unit
) {
    var selectedItem by remember { mutableStateOf(0) }

    NavigationBar(containerColor = Color.White, tonalElevation = 10.dp) {
        NavigationBarItem(
            selected = selectedItem == 0,
            onClick = { selectedItem = 0 },
            icon = { Icon(Icons.Filled.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = amarilloFuerte,
                selectedTextColor = amarilloFuerte,
                unselectedIconColor = cafeOscuro,
                unselectedTextColor = cafeOscuro
            )
        )
        NavigationBarItem(
            selected = selectedItem == 1,
            onClick = {
                selectedItem = 1
                navigateToRecetas()
            },
            icon = { Icon(Icons.Filled.Menu, contentDescription = "Recetas") },
            label = { Text("Recetas") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = amarilloFuerte,
                selectedTextColor = amarilloFuerte,
                unselectedIconColor = cafeOscuro,
                unselectedTextColor = cafeOscuro
            )
        )
        NavigationBarItem(
            selected = selectedItem == 2,
            onClick = {
                selectedItem = 2
                navigateToFavoritos()
            },
            icon = { Icon(Icons.Filled.Favorite, contentDescription = "Favoritos") },
            label = { Text("Favs") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = amarilloFuerte,
                selectedTextColor = amarilloFuerte,
                unselectedIconColor = cafeOscuro,
                unselectedTextColor = cafeOscuro
            )
        )
        NavigationBarItem(
            selected = selectedItem == 3,
            onClick = {
                selectedItem = 3
                navigateToSimple() // ✅ Solo llamamos a la función, no la reasignamos
            },
            icon = {
                Image(
                    painter = painterResource(id = R.drawable.calendario),
                    contentDescription = "Calendario",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Agenda") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = amarilloFuerte,
                selectedTextColor = amarilloFuerte,
                unselectedIconColor = cafeOscuro,
                unselectedTextColor = cafeOscuro
            )
        )
        NavigationBarItem(
            selected = selectedItem == 4,
            onClick = {
                selectedItem = 4
                navigateToSubirReceta()
            },
            icon = { Icon(Icons.Filled.Person, contentDescription = "Subir Receta") },
            label = { Text("Subir") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = amarilloFuerte,
                selectedTextColor = amarilloFuerte,
                unselectedIconColor = cafeOscuro,
                unselectedTextColor = cafeOscuro
            )
        )
    }
}



@Composable
fun AgendaHoyCard(
    recetas: List<Platillo>,
    amarilloFuerte: Color,
    cafeOscuro: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Agenda de Hoy",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = cafeOscuro
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (recetas.isEmpty()) {
                Text(
                    text = "No hay recetas guardadas para hoy.",
                    color = cafeOscuro.copy(alpha = 0.6f)
                )
            } else {
                recetas.forEach { receta ->
                    Text(
                        text = "• ${receta.Titulo}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = cafeOscuro
                    )
                }
            }
        }
    }
}



package mx.edu.uttt.planeat

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import mx.edu.uttt.planeat.viewmodels.*
import mx.edu.uttt.planeat.models.Platillo
import mx.edu.uttt.planeat.states.LoginViewModelFactory
import mx.edu.uttt.planeat.viewmodel.LoginViewModel
import mx.edu.uttt.planeat.views.*

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun NavigationWrapper(navHostController: NavHostController) {

    val context = LocalContext.current
    val platilloViewModel: PlatilloViewModel = viewModel()
    val usuarioViewModel: UsuariosViewModel = viewModel()
    val favoritoViewModel: FavoritoViewModel = viewModel()

    NavHost(navController = navHostController, startDestination = "inicio") {

        composable("inicio") {
            InicioScreen(navigateToLogin = { navHostController.navigate("login") })
        }

        composable("home") {
            HomeScreen(
                navController = navHostController,
                navigateToRecetas = { navHostController.navigate("recetas") },
                navigateToDetalles = { navHostController.navigate("recetas") },
                navigateToCalendario = { navHostController.navigate("Agenda") },
                navigateToSubirReceta = { navHostController.navigate("subirReceta") },
                navigateToSimple = { navHostController.navigate("agendaSimple") },
                navigateToFavoritos = { navHostController.navigate("favoritos") },
                navigateToDetalleReceta = { idReceta ->
                    navHostController.navigate("recetaDetail/$idReceta")

                },
                navigateToLogout = { navHostController.navigate("logout") }
            )
        }

        composable("login") {
            LoginScreen(
                navigateToSignUp = { navHostController.navigate("registrar") },
                navigateToHome = { navHostController.navigate("home") }
            )
        }

        composable("registrar") {
            RegisterScreen(
                navigateToHome = { navHostController.navigate("home") },
                navigateToLogin = { navHostController.navigate("login") }
            )
        }

        composable("recetas") {
            RecetasSocialScreen(
                platilloViewModel = platilloViewModel,
                usuarioViewModel = usuarioViewModel,
                onNavigateToDetail = { platillo ->
                    navHostController.navigate("recetaDetail/${platillo.IdReceta}")
                }
            )
        }

        composable("recetaDetail/{idReceta}") { backStackEntry ->
            val idReceta = backStackEntry.arguments?.getString("idReceta")?.toIntOrNull()
            val platillo = platilloViewModel.platillos.collectAsState().value.find {
                it.IdReceta == idReceta
            }

            if (platillo != null) {
                RecetaDetailScreen(
                    platillo = platillo,
                    onBack = { navHostController.popBackStack() },
                    onNavigateToAgenda = { id ->
                        navHostController.navigate("Agenda/$id")
                    }
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF4E3629))
                }
            }
        }

        composable("Agenda/{idReceta}") { backStackEntry ->
            val idReceta = backStackEntry.arguments?.getString("idReceta")?.toIntOrNull() ?: return@composable
            AgendaDetailScreen(
                idReceta = idReceta,
                onBack = { navHostController.popBackStack() },
                onnavigateToGuardar = { anio, mes, dia, idReceta, tipoComida ->
                    navHostController.navigate("guardarReceta/$idReceta/$anio/$mes/$dia")
                }
            )
        }



        composable("Agenda") {
            AgendaDetailScreen(
                idReceta = -1, // o cualquier valor dummy
                onBack = { navHostController.popBackStack() },
                onnavigateToGuardar = { anio, mes, dia, idReceta, tipoComida ->
                    // Puedes redirigir a una pantalla diferente o simplemente ignorar
                }
            )
        }



        composable("guardarReceta/{idReceta}/{anio}/{mes}/{dia}") { backStackEntry ->
            val idReceta = backStackEntry.arguments?.getString("idReceta")?.toIntOrNull() ?: return@composable
            val anio = backStackEntry.arguments?.getString("anio")?.toIntOrNull() ?: return@composable
            val mes = backStackEntry.arguments?.getString("mes")?.toIntOrNull() ?: return@composable
            val dia = backStackEntry.arguments?.getString("dia")?.toIntOrNull() ?: return@composable

            GuardarRecetaScreen(
                anio = anio,
                mes = mes,
                dia = dia,
                idReceta = idReceta,
                onBack = { navHostController.popBackStack() },
                navigateToHome = { navHostController.navigate("home") } // 👈 aquí defines la navegación a Home
            )
        }



        composable("subirReceta") {
            SubirRecetaScreen(
                onBack = { navHostController.popBackStack() },
                navController = navHostController
            )
        }

        composable("agregarIngredientes/{platilloId}") { backStackEntry ->
            val platilloId = backStackEntry.arguments?.getString("platilloId")?.toIntOrNull() ?: return@composable
            AgregarIngredientesScreen(
                platilloId = platilloId,
                onBack = { navHostController.popBackStack() }
            )
        }

        composable("favoritos") {
            RecetasFavoritasScreen(
                platilloViewModel = platilloViewModel,
                usuarioViewModel = usuarioViewModel,
                onNavigateToDetail = { platillo ->
                    navHostController.navigate("recetaDetail/${platillo.IdReceta}")
                },
                favoritoViewModel = favoritoViewModel
            )
        }


        composable("agendaSimple") {
            AgendaSimpleScreen(onBack = { navHostController.popBackStack() })
        }

        composable("logout") {
            // Usar el ViewModel con la factory que provee el Context
            val loginViewModel: LoginViewModel = viewModel(factory = LoginViewModelFactory(context))
            LogoutScreen(viewModel = loginViewModel)
        }


    }
}


/*
fun getRecetaByTitulo(titulo: String?): RecetaDetalle {
    // Aquí deberías obtener la receta desde tu base de datos o API.
    // Este es solo un ejemplo con datos estáticos.
    return RecetaDetalle(
        titulo = titulo ?: "Tacos de Pescado",
        descripcion = "Deliciosos tacos de pescado con salsa de mango y vegetales frescos.",
        imagenReceta = R.drawable.logo, // Usa una imagen adecuada
        ingredientes = listOf("Pescado", "Tortillas", "Mango", "Cebolla", "Aguacate"),
        pasos = listOf("Cocinar el pescado", "Preparar la salsa de mango", "Servir en tortillas"),
        puntuacion = 4.5f
    )
}
*/

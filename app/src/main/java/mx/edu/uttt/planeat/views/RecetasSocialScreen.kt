package mx.edu.uttt.planeat.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.uttt.planeat.R
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import mx.edu.uttt.planeat.models.Platillo
import mx.edu.uttt.planeat.models.RecetaDetalle
import mx.edu.uttt.planeat.models.toRecetaSocial
import mx.edu.uttt.planeat.viewmodels.PlatilloViewModel
import mx.edu.uttt.planeat.viewmodels.UsuariosViewModel

@Composable
fun RecetasSocialScreen(
    onNavigateToDetail: (Platillo) -> Unit,
    platilloViewModel: PlatilloViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    usuarioViewModel: UsuariosViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val amarilloFuerte = Color(0xFFFFD94C)
    val cafeOscuro = Color(0xFF4E3629)
    val grisFondo = Color(0xFFF9F9F9)
    val blanco = Color.White

    val platillos by platilloViewModel.platillos.collectAsState()
    val isLoading by platilloViewModel.isLoading.collectAsState()
    val errorMessage by platilloViewModel.errorMessage.collectAsState()
    val usuariosMap by usuarioViewModel.usuariosMap.collectAsState()

    LaunchedEffect(Unit) {
        usuarioViewModel.loadUsuarios()
        platilloViewModel.loadPlatillos()
    }

    Scaffold(
        containerColor = grisFondo
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Recetas de la Comunidad",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = cafeOscuro
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator(color = cafeOscuro)
                Spacer(modifier = Modifier.height(16.dp))
            }

            errorMessage?.let {
                Text(text = it, color = Color.Red)
                Spacer(modifier = Modifier.height(16.dp))
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(platillos) { platillo ->
                    val nombreUsuario = usuariosMap[platillo.IdUsuario] ?: "Usuario desconocido"

                    RecetaSocialCard(
                        receta = platillo.toRecetaSocial(nombreUsuario),
                        amarilloFuerte = amarilloFuerte,
                        cafeOscuro = cafeOscuro,
                        blanco = blanco,
                        onNavigateToDetail = {
                            onNavigateToDetail(platillo)
                        }
                    )
                }
            }
        }
    }
}





@Composable
fun RecetaSocialCard(
    receta: RecetaSocial,
    amarilloFuerte: Color,
    cafeOscuro: Color,
    blanco: Color,
    onNavigateToDetail: () -> Unit
) {
    var liked by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, shape = RoundedCornerShape(16.dp))
            .clickable { onNavigateToDetail() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = blanco)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = receta.imagenUsuario),
                    contentDescription = "Usuario",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(amarilloFuerte)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = receta.usuario,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = cafeOscuro
                )
            }

            Image(
                painter = painterResource(id = receta.imagenReceta),
                contentDescription = receta.titulo,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(amarilloFuerte),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = receta.titulo,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = cafeOscuro
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = receta.descripcion,
                    fontSize = 14.sp,
                    color = cafeOscuro.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { liked = !liked }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Like",
                            tint = if (liked) Color.Red else cafeOscuro
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (liked) "Te gusta" else "Like",
                            color = cafeOscuro,
                            fontSize = 14.sp
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Puntuación",
                            tint = amarilloFuerte
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = receta.puntuacion.toString(),
                            color = cafeOscuro,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}



data class RecetaSocial(
    val usuario: String,
    val imagenUsuario: Int,
    val imagenReceta: Int,
    val titulo: String,
    val descripcion: String,
    val puntuacion: Float
)


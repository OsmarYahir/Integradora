package mx.edu.uttt.planeat.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.edu.uttt.planeat.models.BandejaReceta
import mx.edu.uttt.planeat.response.UserPreferences
import mx.edu.uttt.planeat.viewmodels.BandejaRecetaViewModel
import mx.edu.uttt.planeat.viewmodels.FechaCalendarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuardarRecetaScreen(
    fecha: String,
    idReceta: Int,
    onBack: () -> Unit,
    fechaCalendarioViewModel: FechaCalendarioViewModel = viewModel(),
    bandejaRecetaViewModel: BandejaRecetaViewModel = viewModel(),
    navigateToHome: () -> Unit, // 👈 Nuevo parámetro
) {
    val cafeOscuro = Color(0xFF4E3629)
    val blanco = Color.White
    val grisFondo = Color(0xFFF9F9F9)
    val amarilloFuerte = Color(0xFFFFD94C)

    var tipoComida by remember { mutableStateOf("Desayuno") }

    val userPreferences = UserPreferences(LocalContext.current)
    val idUsuarioActual = userPreferences.getUserId()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Guardar receta",
                        color = cafeOscuro,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = cafeOscuro)
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = blanco)
            )
        },
        containerColor = grisFondo
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = blanco),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "¿Cuándo deseas guardar esta receta?",
                        color = cafeOscuro,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Fecha seleccionada: $fecha",
                        color = cafeOscuro.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Tipo de comida",
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = cafeOscuro
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TipoComidaButton("Desayuno", tipoComida) { tipoComida = it }
                        TipoComidaButton("Comida", tipoComida) { tipoComida = it }
                        TipoComidaButton("Cena", tipoComida) { tipoComida = it }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    val (anio, mes, dia) = fecha.split("-").map { it.toInt() }

                    Button(
                        onClick = {
                            fechaCalendarioViewModel.guardarFecha(anio, mes, dia) { fechaGuardada ->
                                val nuevaBandeja = BandejaReceta(
                                    TipoComida = tipoComida,
                                    IdUsuario = idUsuarioActual,
                                    IdReceta = idReceta,
                                    IdCalendario = fechaGuardada.IdCalendario
                                )
                                bandejaRecetaViewModel.guardarBandejaReceta(nuevaBandeja)
                                navigateToHome() // 👈 Navegamos a inicio
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = amarilloFuerte)
                    ) {
                        Text("Guardar receta", fontWeight = FontWeight.Bold, color = cafeOscuro)
                    }


                    TextButton(onClick = onBack) {
                        Text("Cancelar", color = cafeOscuro.copy(alpha = 0.7f))
                    }
                }
            }
        }
    }
}

@Composable
fun TipoComidaButton(text: String, selected: String, onClick: (String) -> Unit) {
    val isSelected = text == selected
    val background = if (isSelected) Color(0xFFFFD94C) else Color(0xFFF2F2F2)
    val textColor = if (isSelected) Color.Black else Color.DarkGray

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(background)
            .clickable { onClick(text) }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(text = text, color = textColor, fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
}


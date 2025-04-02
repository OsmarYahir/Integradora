package mx.edu.uttt.planeat.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.outlined.LunchDining
import androidx.compose.material.icons.outlined.Nightlife
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
import mx.edu.uttt.planeat.models.BandejaReceta
import mx.edu.uttt.planeat.response.UserPreferences
import mx.edu.uttt.planeat.viewmodels.BandejaRecetaViewModel
import mx.edu.uttt.planeat.viewmodels.FechaCalendarioViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuardarRecetaScreen(
    anio: Int,
    mes: Int,
    dia: Int,
    idReceta: Int,
    onBack: () -> Unit,
    fechaCalendarioViewModel: FechaCalendarioViewModel = viewModel(),
    bandejaRecetaViewModel: BandejaRecetaViewModel = viewModel(),
    navigateToHome: () -> Unit,
) {
    // Elegant color palette
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

    val context = LocalContext.current
    val userPreferences = UserPreferences(context)
    val idUsuario = userPreferences.getUserId()

    var tipoComida by remember { mutableStateOf("Desayuno") }
    var isSaving by remember { mutableStateOf(false) }
    var shouldNavigate by remember { mutableStateOf(false) }  // Para controlar la navegación

    val fecha = remember {
        // Format the date (months in Calendar are 0-based, so we subtract 1)
        val calendar = Calendar.getInstance()
        calendar.set(anio, mes - 1, dia)
        val dateFormat = SimpleDateFormat("EEEE, d 'de' MMMM, yyyy", Locale("es", "MX"))
        dateFormat.format(calendar.time).replaceFirstChar { it.uppercase() }
    }

    // Realizamos la verificación y creación de la fecha si es necesario
    var idCalendario by remember { mutableStateOf(0) }
    LaunchedEffect(anio, mes, dia) {
        // Llamamos a la función, asegurándonos de que se pase el ID de la fecha
        fechaCalendarioViewModel.guardarFechaSiNoExiste(anio, mes, dia) { id ->
            idCalendario = id  // Aquí obtenemos el idCalendario que se pasa desde la función
        }
    }

    // Navegación después de que se guarda la receta
    if (shouldNavigate) {
        navigateToHome() // Ejecuta la navegación cuando el estado es verdadero
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Guardar Receta",
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
                            contentDescription = "Atrás",
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
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = gradientBackground)
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = blanco),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Calendar icon and title
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(amarilloSuave)
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = cafeOscuro
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = "Añadir a tu plan alimenticio",
                                    color = cafeOscuro,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )

                                Text(
                                    text = fecha,
                                    color = cafeClaro,
                                    fontSize = 14.sp,
                                    letterSpacing = 0.4.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Divider
                        Divider(
                            color = grisFondo,
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Meal type selection
                        Text(
                            text = "Selecciona el tipo de comida",
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = cafeOscuro
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            MealTypeOption(
                                title = "Desayuno",
                                icon = Icons.Outlined.LunchDining,
                                isSelected = tipoComida == "Desayuno",
                                accentColor = amarilloFuerte,
                                darkColor = cafeOscuro
                            ) {
                                tipoComida = "Desayuno"
                            }

                            MealTypeOption(
                                title = "Comida",
                                icon = Icons.Outlined.LunchDining,
                                isSelected = tipoComida == "Comida",
                                accentColor = amarilloFuerte,
                                darkColor = cafeOscuro
                            ) {
                                tipoComida = "Comida"
                            }

                            MealTypeOption(
                                title = "Cena",
                                icon = Icons.Outlined.Nightlife,
                                isSelected = tipoComida == "Cena",
                                accentColor = amarilloFuerte,
                                darkColor = cafeOscuro
                            ) {
                                tipoComida = "Cena"
                            }
                        }

                        Spacer(modifier = Modifier.height(40.dp))

                        // Save button with loading state
                        Button(
                            onClick = {
                                isSaving = true
                                val nuevaBandeja = BandejaReceta(
                                    TipoComida = tipoComida,
                                    IdUsuario = idUsuario,
                                    IdReceta = idReceta,
                                    IdCalendario = idCalendario
                                )
                                bandejaRecetaViewModel.guardarBandejaReceta(nuevaBandeja)
                                shouldNavigate = true  // Navegar al Home después de guardar
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = amarilloFuerte,
                                contentColor = cafeOscuro
                            ),
                            enabled = !isSaving
                        ) {
                            if (isSaving) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = cafeOscuro,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    "Guardar receta",
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Cancel button
                        TextButton(
                            onClick = onBack,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = cafeClaro
                            )
                        ) {
                            Text(
                                "Cancelar",
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MealTypeOption(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    accentColor: Color,
    darkColor: Color,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) accentColor.copy(alpha = 0.15f) else Color.Transparent
    val borderColor = if (isSelected) accentColor else Color(0xFFE0E0E0)
    val iconTint = if (isSelected) accentColor else darkColor.copy(alpha = 0.6f)
    val textColor = if (isSelected) darkColor else darkColor.copy(alpha = 0.7f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(backgroundColor)
                .border(
                    width = 1.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = title,
            color = textColor,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

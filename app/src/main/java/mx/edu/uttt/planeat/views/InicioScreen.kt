package mx.edu.uttt.planeat.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import mx.edu.uttt.planeat.R

@Composable
fun InicioScreen(navigateToLogin: () -> Unit, navigateToSignUp: () -> Unit = {}) {
    val amarilloFondo = Color(0xFFFFD94C)
    val amarilloClaro = Color(0xFFFFE78A)
    val marronOscuro = Color(0xFF4E3629)
    val marronClaro = Color(0xFF8A6552)
    val blanco = Color.White
    val naranja = Color(0xFFF8A13F)

    // Usamos LaunchedEffect para retrasar la navegación después de 2.5 segundos
    LaunchedEffect(Unit) {
        delay(2500)  // Espera 2.5 segundos
        navigateToLogin()  // Redirige al login después de la animación
    }

    // Animaciones
    val infiniteTransition = rememberInfiniteTransition(label = "splashAnimation")
    val pulsate = infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val rotationAnimation = infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )

    // Animación de entrada del texto
    var showText by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(700)
        showText = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(amarilloClaro, amarilloFondo)
                )
            )
    ) {
        // Formas geométricas en el fondo
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            // Círculo superior izquierdo
            drawCircle(
                color = naranja.copy(alpha = 0.2f),
                radius = 180f,
                center = Offset(x = -50f, y = -50f)
            )

            // Círculo inferior derecho
            drawCircle(
                color = marronClaro.copy(alpha = 0.15f),
                radius = 250f,
                center = Offset(size.width + 50f, size.height + 100f)
            )

            // Curva wave arriba
            val wavePathTop = Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width, size.height * 0.15f)
                cubicTo(
                    size.width * 0.75f, size.height * 0.10f,
                    size.width * 0.25f, size.height * 0.20f,
                    0f, size.height * 0.15f
                )
                close()
            }

            drawPath(
                path = wavePathTop,
                color = marronOscuro.copy(alpha = 0.08f),
                style = Fill
            )

            // Curva wave abajo
            val wavePath = Path().apply {
                moveTo(0f, size.height * 0.85f)
                cubicTo(
                    size.width * 0.25f, size.height * 0.80f,
                    size.width * 0.75f, size.height * 0.90f,
                    size.width, size.height * 0.85f
                )
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }

            drawPath(
                path = wavePath,
                color = marronOscuro.copy(alpha = 0.2f),
                style = Fill
            )
        }

        // Elementos decorativos de comida (pequeños iconos flotantes)
        Box(
            modifier = Modifier
                .offset(x = 40.dp, y = 80.dp)
                .rotate(rotationAnimation.value * 2)
                .size(30.dp)
                .shadow(5.dp, RoundedCornerShape(50))
                .background(blanco, CircleShape)
                .padding(6.dp)
        ) {
            // Aquí puedes agregar un pequeño icono de comida si lo tienes
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(color = naranja, radius = 15f)
            }
        }

        Box(
            modifier = Modifier
                .offset(x = 300.dp, y = 200.dp)
                .rotate(rotationAnimation.value * -1)
                .size(25.dp)
                .shadow(4.dp, RoundedCornerShape(50))
                .background(blanco, CircleShape)
                .padding(5.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(color = marronClaro, radius = 15f)
            }
        }

        // Logo centrado con sombra y bordes con animación de pulsación
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(280.dp)
                .scale(pulsate.value)
                .shadow(20.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(blanco, Color(0xFFF8F8F8)),
                        center = Offset.Infinite,
                        radius = 300f
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // Círculo interior brillante
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .shadow(8.dp, CircleShape)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(blanco, amarilloClaro.copy(alpha = 0.3f)),
                            radius = 200f
                        )
                    )
            )

            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo PlanEat",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(220.dp)
                    .clip(CircleShape)
            )
        }

        // Texto con animación de entrada
        AnimatedVisibility(
            visible = showText,
            enter = fadeIn(animationSpec = tween(800)) +
                    slideInVertically(animationSpec = tween(800)) { it / 2 },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Bienvenido a",
                    color = marronOscuro.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Text(
                    text = "PlanEat",
                    color = marronOscuro,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier
                        .width(120.dp)
                        .height(5.dp),
                    colors = CardDefaults.cardColors(containerColor = marronOscuro.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(50)
                ) { }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InicioScreenPreview() {
    InicioScreen(navigateToLogin = {})
}
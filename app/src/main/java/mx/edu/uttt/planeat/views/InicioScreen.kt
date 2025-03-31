package mx.edu.uttt.planeat.views

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import mx.edu.uttt.planeat.R

@Composable
fun InicioScreen(navigateToLogin: () -> Unit, navigateToSignUp: () -> Unit = {}) {
    val amarilloFondo = Color(0xFFFFD94C)
    val marronOscuro = Color(0xFF4E3629)
    val blanco = Color.White

    // Usamos LaunchedEffect para retrasar la navegación después de 2 segundos
    LaunchedEffect(Unit) {
        delay(2000)  // Espera 2 segundos
        navigateToLogin()  // Redirige al login después de 2 segundos
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(amarilloFondo)
    ) {
        // Formas geométricas en el fondo
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            // Círculo superior izquierdo
            drawCircle(
                color = marronOscuro.copy(alpha = 0.2f),
                radius = 150f,
                center = Offset(x = 0f, y = 0f)
            )

            // Círculo inferior derecho
            drawCircle(
                color = marronOscuro.copy(alpha = 0.2f),
                radius = 200f,
                center = Offset(size.width, size.height)
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
                color = marronOscuro.copy(alpha = 0.3f),
                style = Fill
            )
        }

        // Logo centrado con sombra y bordes
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(250.dp)
                .shadow(15.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(blanco),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo), // Ajusta el logo si el nombre es otro
                contentDescription = "Logo PlanEat",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(220.dp)
                    .clip(CircleShape)
            )
        }

        // Texto o frase motivacional opcional
        Text(
            text = "Bienvenido a PlanEat",
            color = marronOscuro,
            style = MaterialTheme.typography.titleLarge, // Usamos titleLarge, si no tienes esto puedes usar `h6` o `bodyLarge`
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }
}


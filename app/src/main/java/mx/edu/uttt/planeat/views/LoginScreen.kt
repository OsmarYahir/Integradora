package mx.edu.uttt.planeat.views

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

import mx.edu.uttt.planeat.R
import mx.edu.uttt.planeat.states.LoginViewModelFactory
import mx.edu.uttt.planeat.viewmodel.LoginViewModel

// Paleta de colores
val amarilloFuerte = Color(0xFFFFD94C)
val amarilloClaro = Color(0xFFFFF8E1)
val cafeOscuro = Color(0xFF4E3629)
val cafeMedio = Color(0xFF8D6E63)

@Composable
fun LoginScreen(navigateToSignUp: () -> Unit, navigateToHome: () -> Unit = {}) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Usar ViewModelProvider con la fábrica personalizada
    val viewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(context)
    )

    // Estado de la carga y mensajes de error
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val usuarioLogueado by viewModel.usuarioLogueado.collectAsState()

    // Verificación del idUsuario guardado
    val userId = viewModel.getUserId()

    // Cuando el usuario está logueado o el idUsuario ya está guardado, navegamos automáticamente al Home
    LaunchedEffect(usuarioLogueado) {
        if (usuarioLogueado != null || userId != -1) {
            navigateToHome()  // Si el usuario está logueado o el idUsuario existe, navegar al home
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo con diseño wave
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Dibujamos el fondo amarillo claro
            drawRect(amarilloClaro, Offset.Zero, size)

            // Dibujamos una onda amarilla fuerte en la parte superior
            val wavePath = Path().apply {
                moveTo(0f, 0f)
                lineTo(width, 0f)
                lineTo(width, height * 0.35f)
                cubicTo(
                    width * 0.75f, height * 0.45f,
                    width * 0.5f, height * 0.25f,
                    0f, height * 0.38f
                )
                close()
            }
            drawPath(wavePath, amarilloFuerte, style = Fill)
        }

        // Contenido principal
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo y título superior
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 50.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Círculo con ícono
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .shadow(8.dp, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Usuario",
                        tint = cafeOscuro,
                        modifier = Modifier.size(60.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "PlanEat",
                    style = TextStyle(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = cafeOscuro
                    )
                )

                Text(
                    text = "Tu planificador de comidas",
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = cafeOscuro
                    )
                )
            }

            // Card de login
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(top = 40.dp)
                    .shadow(elevation = 10.dp, shape = RoundedCornerShape(24.dp), spotColor = cafeMedio.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 32.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Inicia sesión",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = cafeOscuro
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    // Campo de usuario
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = {
                            Text(
                                "Correo electrónico",
                                color = cafeMedio,
                                fontSize = 14.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = cafeMedio,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = cafeMedio.copy(alpha = 0.1f)),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = amarilloClaro,
                            unfocusedContainerColor = amarilloClaro,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = cafeOscuro,
                            focusedTextColor = cafeOscuro,
                            unfocusedTextColor = cafeOscuro
                        ),
                        textStyle = TextStyle(fontSize = 16.sp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Campo de contraseña
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = {
                            Text(
                                "Contraseña",
                                color = cafeMedio,
                                fontSize = 14.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = cafeMedio,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = cafeMedio,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = cafeMedio.copy(alpha = 0.1f)),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = amarilloClaro,
                            unfocusedContainerColor = amarilloClaro,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = cafeOscuro,
                            focusedTextColor = cafeOscuro,
                            unfocusedTextColor = cafeOscuro
                        ),
                        textStyle = TextStyle(fontSize = 16.sp)
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    // Mostrar mensajes de error
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = cafeOscuro,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    errorMessage?.let { msg ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .background(Color.Red.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = msg,
                                color = Color.Red,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Botón de inicio de sesión
                    Button(
                        onClick = {
                            viewModel.login(username, password)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(8.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = cafeOscuro,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 6.dp,
                            pressedElevation = 8.dp
                        )
                    ) {
                        Text(
                            text = "Iniciar sesión",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Enlace para registrarse
                    TextButton(
                        onClick = { navigateToSignUp() },
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = "¿No tienes cuenta? Regístrate ahora",
                            color = cafeOscuro,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
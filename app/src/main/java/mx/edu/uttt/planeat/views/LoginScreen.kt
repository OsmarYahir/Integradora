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
        // Parte superior amarilla con ícono
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp) // más altura para dar espacio al ícono
                .background(amarilloFuerte),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Usuario",
                tint = Color.Black,
                modifier = Modifier.size(70.dp)
            )
        }

        // Card blanca centrada
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 200.dp), // bajamos la tarjeta un poco
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .wrapContentHeight()
                    .shadow(8.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Inicia sesión",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = cafeOscuro
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Correo electrónico", color = cafeMedio) },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = cafeMedio)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = amarilloClaro,
                            unfocusedContainerColor = amarilloClaro,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = cafeOscuro
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña", color = cafeMedio) },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = cafeMedio)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = cafeMedio
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = amarilloClaro,
                            unfocusedContainerColor = amarilloClaro,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = cafeOscuro
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (isLoading) {
                        CircularProgressIndicator(color = cafeOscuro)
                    }

                    errorMessage?.let { msg ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = msg,
                            color = Color.Red,
                            fontSize = 14.sp
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.login(username, password)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = cafeOscuro)
                    ) {
                        Text(
                            text = "Iniciar sesión",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(onClick = { navigateToSignUp() }) {
                        Text(
                            text = "¿No tienes cuenta? Regístrate ahora",
                            color = cafeOscuro,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}





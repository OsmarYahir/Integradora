package mx.edu.uttt.planeat.views

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import mx.edu.uttt.planeat.viewmodel.LoginViewModel

@Composable
fun LogoutScreen(viewModel: LoginViewModel) {
    // Obtener el estado del usuario logueado
    val usuario = viewModel.usuarioLogueado.collectAsState().value

    // Obtener el contexto
    val context = LocalContext.current

    // Verificar si hay usuario logueado
    if (usuario != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Cambiamos h6 por titleLarge que es el equivalente en Material 3
            Text(text = "Bienvenido, ${usuario.Nombre}", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                viewModel.logout()  // Llamamos a la función logout del ViewModel
                Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()  // Usamos LocalContext para obtener el contexto
            }) {
                Text(text = "Cerrar sesión")
            }
        }
    } else {
        // Si no hay usuario logueado, mostramos un mensaje
        Text(text = "No hay usuario logueado", style = MaterialTheme.typography.titleLarge)
    }
}

package mx.edu.uttt.planeat.views

import android.Manifest
import android.graphics.Bitmap
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import mx.edu.uttt.planeat.models.Ingrediente
import mx.edu.uttt.planeat.viewmodels.IngredienteViewModel
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarIngredientesScreen(
    platilloId: Int,  // Recibe el ID del platillo
    ingredienteViewModel: IngredienteViewModel = viewModel(),
    onBack: () -> Unit
) {
    val cafeOscuro = Color(0xFF4E3629)
    val blanco = Color.White
    val grisFondo = Color(0xFFF9F9F9)
    val amarilloFuerte = Color(0xFFFFD94C)

    var nuevoIngrediente by remember { mutableStateOf("") }
    var cantidadIngrediente by remember { mutableStateOf("") }
    val ingredientes = remember { mutableStateListOf<Pair<String, String>>() }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val estado by ingredienteViewModel.errorMessage.collectAsState()

    LaunchedEffect(estado) {
        if (estado != null) {
            snackbarHostState.showSnackbar(estado!!)
            ingredienteViewModel.loadIngredientes()  // Refrescar lista de ingredientes después de cualquier operación
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Agregar Ingredientes", color = cafeOscuro) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = cafeOscuro
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = blanco)
            )
        },
        containerColor = grisFondo
    ) { innerPadding ->
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Campos para agregar ingredientes
            OutlinedTextField(
                value = nuevoIngrediente,
                onValueChange = { nuevoIngrediente = it },
                label = { Text("Ingrediente") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = cantidadIngrediente,
                onValueChange = { cantidadIngrediente = it },
                label = { Text("Cantidad") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (nuevoIngrediente.isNotBlank() && cantidadIngrediente.isNotBlank()) {
                        ingredientes.add(nuevoIngrediente to cantidadIngrediente)
                        nuevoIngrediente = ""
                        cantidadIngrediente = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = amarilloFuerte)
            ) {
                Text("Agregar ingrediente", color = cafeOscuro)
            }

            // Mostrar lista de ingredientes agregados
            ingredientes.forEach { (ing, cant) ->
                Text("• $ing: $cant", color = cafeOscuro)
            }

            // Botón para guardar los ingredientes
            Button(
                onClick = {
                    if (ingredientes.isNotEmpty()) {
                        ingredientes.forEach { (nombre, cantidad) ->
                            val ingrediente = Ingrediente(0, nombre, cantidad, platilloId)
                            ingredienteViewModel.addIngrediente(ingrediente) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Ingrediente agregado con éxito.")
                                }
                            }
                        }
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar("Agrega al menos un ingrediente antes de guardar.")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = amarilloFuerte)
            ) {
                Text("Guardar Ingredientes", color = cafeOscuro, fontWeight = FontWeight.Bold)
            }
        }
    }
}

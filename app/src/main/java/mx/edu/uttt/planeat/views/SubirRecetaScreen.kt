package mx.edu.uttt.planeat.views

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import mx.edu.uttt.planeat.models.Ingrediente
import mx.edu.uttt.planeat.models.PlatilloPaso
import mx.edu.uttt.planeat.response.UserPreferences
import mx.edu.uttt.planeat.viewmodels.IngredienteViewModel
import mx.edu.uttt.planeat.viewmodels.PlatilloPasoViewModel
import mx.edu.uttt.planeat.viewmodels.SubirRecetaViewModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubirRecetaScreen(
    navController: NavHostController,
    subirRecetaViewModel: SubirRecetaViewModel = viewModel(),
    ingredienteViewModel: IngredienteViewModel = viewModel(),
    platilloPasoViewModel: PlatilloPasoViewModel = viewModel(),
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }
    val idUsuarioActual = userPreferences.getUserId()

    var imagenBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val imageFile = remember { File(context.cacheDir, "temp_image.jpg") }
    val imageUri = remember { FileProvider.getUriForFile(context, "${context.packageName}.provider", imageFile) }

    val takePictureLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
            imagenBitmap = bitmap
        }
    }



    if (idUsuarioActual == -1) {
        // El usuario no ha iniciado sesión, redirigir a la pantalla de login
        LaunchedEffect(key1 = true) {
            navController.navigate("login") {
                popUpTo("subirReceta") { inclusive = true }
            }
        }
        return
    }

    val cafeOscuro = Color(0xFF4E3629)
    val blanco = Color.White
    val grisFondo = Color(0xFFF9F9F9)
    val amarilloFuerte = Color(0xFFFFD94C)

    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    // Estado para ingredientes
    var nombreIngrediente by remember { mutableStateOf("") }
    var cantidadIngrediente by remember { mutableStateOf("") }
    var ingredientesList by remember { mutableStateOf<List<Ingrediente>>(emptyList()) }

    // Estado para pasos
    var descripcionPaso by remember { mutableStateOf("") }
    var numeroPaso by remember { mutableStateOf(1) }
    var pasosList by remember { mutableStateOf<List<PlatilloPaso>>(emptyList()) }

    // Estado para gestionar la navegación entre secciones
    var currentScreen by remember { mutableStateOf("info") } // "info", "ingredientes", "pasos"

    // Estado para almacenar ID de receta creada
    var recetaId by remember { mutableStateOf<Int?>(null) }




    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) {
        it?.let { bitmap ->
            val resizedBitmap = resizeBitmap(bitmap, 1600)
            imagenBitmap = resizedBitmap
        }
    }

    val permisoCamaraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            takePictureLauncher.launch(imageUri)
        } else {
            Toast.makeText(context, "Permiso denegado", Toast.LENGTH_SHORT).show()
        }
    }


    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when(currentScreen) {
                            "info" -> "Información de receta"
                            "ingredientes" -> "Ingredientes"
                            "pasos" -> "Pasos de preparación"
                            else -> "Crear receta"
                        },
                        color = cafeOscuro
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (currentScreen == "info") {
                            onBack()
                        } else {
                            currentScreen = "info"
                        }
                    }) {
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
        when (currentScreen) {
            "info" -> {
                InfoRecetaScreen(
                    innerPadding = innerPadding,
                    titulo = titulo,
                    onTituloChange = { titulo = it },
                    descripcion = descripcion,
                    onDescripcionChange = { descripcion = it },
                    imagenBitmap = imagenBitmap,
                    cafeOscuro = cafeOscuro,
                    amarilloFuerte = amarilloFuerte,
                    onTomarFoto = { permisoCamaraLauncher.launch(Manifest.permission.CAMERA) },
                    // Reemplaza el código existente en la sección "info" donde manejas la carga de la imagen
                    onContinuar = {
                        if (titulo.isNotBlank() && descripcion.isNotBlank() && imagenBitmap != null) {
                            val imagenFile = bitmapToFile(imagenBitmap!!, context)

                            subirRecetaViewModel.uploadPlatillo(
                                titulo = titulo,
                                descripcion = descripcion,
                                idUsuario = idUsuarioActual,
                                imageFile = imagenFile,
                                onSuccess = { platillo ->
                                    recetaId = platillo.IdReceta
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Información básica guardada")
                                    }
                                    currentScreen = "ingredientes"
                                },
                                onError = { errorMessage ->
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Error: $errorMessage")
                                    }
                                }
                            )
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Completa todos los campos")
                            }
                        }


                    }
                )
            }
            "ingredientes" -> {
                IngredientesScreen(
                    innerPadding = innerPadding,
                    nombreIngrediente = nombreIngrediente,
                    onNombreChange = { nombreIngrediente = it },
                    cantidadIngrediente = cantidadIngrediente,
                    onCantidadChange = { cantidadIngrediente = it },
                    ingredientesList = ingredientesList,
                    cafeOscuro = cafeOscuro,
                    amarilloFuerte = amarilloFuerte,
                    onAgregarIngrediente = {
                        if (nombreIngrediente.isNotBlank() && cantidadIngrediente.isNotBlank()) {
                            val ingrediente = Ingrediente(
                                IdIngrediente = 0, // Será asignado por el backend
                                Nombre = nombreIngrediente,
                                Cantidad = cantidadIngrediente,
                                IdReceta = recetaId!!
                            )

                            ingredienteViewModel.addIngrediente(ingrediente) {
                                ingredientesList = ingredientesList + ingrediente
                                nombreIngrediente = ""
                                cantidadIngrediente = ""
                            }
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Completa todos los campos del ingrediente")
                            }
                        }
                    },
                    onDeleteIngrediente = { ingrediente ->
                        ingredienteViewModel.deleteIngrediente(ingrediente.IdIngrediente) {
                            ingredientesList = ingredientesList.filter { it.IdIngrediente != ingrediente.IdIngrediente }
                        }
                    },
                    onContinuar = {
                        if (ingredientesList.isNotEmpty()) {
                            currentScreen = "pasos"
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Agrega al menos un ingrediente")
                            }
                        }
                    }
                )
            }
            "pasos" -> {
                PasosScreen(
                    innerPadding = innerPadding,
                    descripcionPaso = descripcionPaso,
                    onDescripcionChange = { descripcionPaso = it },
                    numeroPaso = numeroPaso,
                    onNumeroPasoChange = { numeroPaso = maxOf(1, it.toIntOrNull() ?: 1) },
                    pasosList = pasosList,
                    cafeOscuro = cafeOscuro,
                    amarilloFuerte = amarilloFuerte,
                    onAgregarPaso = {
                        if (descripcionPaso.isNotBlank()) {
                            val paso = PlatilloPaso(
                                IdPaso = 0, // Será asignado por el backend
                                IdReceta = recetaId!!,
                                Paso = numeroPaso,
                                Descripcion = descripcionPaso
                            )

                            platilloPasoViewModel.addPaso(paso) {
                                pasosList = pasosList + paso
                                descripcionPaso = ""
                                numeroPaso = pasosList.maxOfOrNull { it.Paso }?.plus(1) ?: 1
                            }
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Completa la descripción del paso")
                            }
                        }
                    },
                    onDeletePaso = { paso ->
                        platilloPasoViewModel.deletePaso(paso.IdPaso) {
                            pasosList = pasosList.filter { it.IdPaso != paso.IdPaso }
                        }
                    },
                    onFinish = {
                        if (pasosList.isNotEmpty()) {
                            // Navigate to either a success screen or back to recipe list
                            scope.launch {
                                snackbarHostState.showSnackbar("¡Receta creada con éxito!")
                            }
                            navController.navigate("home") {
                                popUpTo("subirReceta") { inclusive = true }
                            }
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Agrega al menos un paso")
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun InfoRecetaScreen(
    innerPadding: PaddingValues,
    titulo: String,
    onTituloChange: (String) -> Unit,
    descripcion: String,
    onDescripcionChange: (String) -> Unit,
    imagenBitmap: Bitmap?,
    cafeOscuro: Color,
    amarilloFuerte: Color,
    onTomarFoto: () -> Unit,
    onContinuar: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .padding(innerPadding)
            .padding(16.dp)
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Información básica", fontWeight = FontWeight.Bold, color = cafeOscuro)

        OutlinedTextField(
            value = titulo,
            onValueChange = onTituloChange,
            label = { Text("Título de la receta") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        OutlinedTextField(
            value = descripcion,
            onValueChange = onDescripcionChange,
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        Text("Imagen", fontWeight = FontWeight.Bold, color = cafeOscuro)

        Button(
            onClick = onTomarFoto,
            colors = ButtonDefaults.buttonColors(containerColor = amarilloFuerte)
        ) {
            Text("Tomar foto", color = cafeOscuro)
        }

        imagenBitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "Vista previa",
                modifier = Modifier
                    .size(150.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onContinuar,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = amarilloFuerte)
        ) {
            Text("Continuar con ingredientes", color = cafeOscuro, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun IngredientesScreen(
    innerPadding: PaddingValues,
    nombreIngrediente: String,
    onNombreChange: (String) -> Unit,
    cantidadIngrediente: String,
    onCantidadChange: (String) -> Unit,
    ingredientesList: List<Ingrediente>,
    cafeOscuro: Color,
    amarilloFuerte: Color,
    onAgregarIngrediente: () -> Unit,
    onDeleteIngrediente: (Ingrediente) -> Unit,
    onContinuar: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(innerPadding)
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Agregar ingredientes", fontWeight = FontWeight.Bold, color = cafeOscuro)

        OutlinedTextField(
            value = nombreIngrediente,
            onValueChange = onNombreChange,
            label = { Text("Nombre del ingrediente") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        OutlinedTextField(
            value = cantidadIngrediente,
            onValueChange = onCantidadChange,
            label = { Text("Cantidad (ej: 2 cucharadas)") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )

        Button(
            onClick = onAgregarIngrediente,
            colors = ButtonDefaults.buttonColors(containerColor = amarilloFuerte)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Agregar")
            Spacer(modifier = Modifier.width(4.dp))
            Text("Agregar ingrediente", color = cafeOscuro)
        }

        Divider(thickness = 1.dp, color = Color.LightGray)

        Text(
            "Ingredientes agregados (${ingredientesList.size})",
            fontWeight = FontWeight.Bold,
            color = cafeOscuro
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(ingredientesList) { ingrediente ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(ingrediente.Nombre, fontWeight = FontWeight.Bold)
                            Text(ingrediente.Cantidad)
                        }

                        IconButton(onClick = { onDeleteIngrediente(ingrediente) }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Eliminar",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = onContinuar,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = amarilloFuerte)
        ) {
            Text("Continuar con pasos", color = cafeOscuro, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PasosScreen(
    innerPadding: PaddingValues,
    descripcionPaso: String,
    onDescripcionChange: (String) -> Unit,
    numeroPaso: Int,
    onNumeroPasoChange: (String) -> Unit,
    pasosList: List<PlatilloPaso>,
    cafeOscuro: Color,
    amarilloFuerte: Color,
    onAgregarPaso: () -> Unit,
    onDeletePaso: (PlatilloPaso) -> Unit,
    onFinish: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(innerPadding)
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Agregar pasos", fontWeight = FontWeight.Bold, color = cafeOscuro)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Top
        ) {
            OutlinedTextField(
                value = numeroPaso.toString(),
                onValueChange = onNumeroPasoChange,
                label = { Text("Paso #") },
                modifier = Modifier.width(80.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )

            OutlinedTextField(
                value = descripcionPaso,
                onValueChange = onDescripcionChange,
                label = { Text("Descripción del paso") },
                modifier = Modifier.weight(1f),
                minLines = 2,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )
        }

        Button(
            onClick = onAgregarPaso,
            colors = ButtonDefaults.buttonColors(containerColor = amarilloFuerte)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Agregar")
            Spacer(modifier = Modifier.width(4.dp))
            Text("Agregar paso", color = cafeOscuro)
        }

        Divider(thickness = 1.dp, color = Color.LightGray)

        Text(
            "Pasos agregados (${pasosList.size})",
            fontWeight = FontWeight.Bold,
            color = cafeOscuro
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(pasosList.sortedBy { it.Paso }) { paso ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Paso ${paso.Paso}",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(70.dp)
                        )

                        Text(
                            paso.Descripcion,
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(onClick = { onDeletePaso(paso) }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Eliminar",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = onFinish,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = amarilloFuerte)
        ) {
            Text("Finalizar y guardar receta", color = cafeOscuro, fontWeight = FontWeight.Bold)
        }
    }
}

// Convierte Bitmap a archivo temporal JPG
fun bitmapToFile(bitmap: Bitmap, context: android.content.Context): File {
    val file = File(context.cacheDir, "imagen_receta_${System.currentTimeMillis()}.jpg")
    file.createNewFile()

    val bos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos) // Calidad máxima
    // Reduce quality to 80%

    val outputStream = FileOutputStream(file)
    outputStream.write(bos.toByteArray())
    outputStream.flush()
    outputStream.close()

    return file
}

fun resizeBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
    val ratio = Math.min(maxSize.toFloat() / bitmap.width, maxSize.toFloat() / bitmap.height)
    val width = Math.round(bitmap.width * ratio)
    val height = Math.round(bitmap.height * ratio)
    return Bitmap.createScaledBitmap(bitmap, width, height, false)
}
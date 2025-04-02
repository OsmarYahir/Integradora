package mx.edu.uttt.planeat.views

import android.Manifest
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import mx.edu.uttt.planeat.viewmodels.SubirRecetaViewModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubirRecetaScreen(
    idUsuarioActual: Int,
    navController: NavHostController,
    subirRecetaViewModel: SubirRecetaViewModel = viewModel(),
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val cafeOscuro = Color(0xFF4E3629)
    val blanco = Color.White
    val grisFondo = Color(0xFFF9F9F9)
    val amarilloFuerte = Color(0xFFFFD94C)

    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var imagenBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) {
        it?.let { bitmap ->
            // Resize the bitmap to reduce size before upload
            val resizedBitmap = resizeBitmap(bitmap, 1024)
            imagenBitmap = resizedBitmap
        }
    }

    val permisoCamaraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                cameraLauncher.launch(null)
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
                title = { Text("Subir receta", color = cafeOscuro) },
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
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título de la receta") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Imagen", fontWeight = FontWeight.Bold, color = cafeOscuro)

            Button(
                onClick = { permisoCamaraLauncher.launch(Manifest.permission.CAMERA) },
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

            Button(
                onClick = {
                    if (titulo.isNotBlank() && descripcion.isNotBlank()) {
                        val imagenFile: File? = imagenBitmap?.let { bitmapToFile(it, context) }

                        if (imagenFile != null) {
                            // Option 1: Upload directly to your API
                            subirRecetaViewModel.uploadPlatilloDirectly(
                                titulo = titulo,
                                descripcion = descripcion,
                                idUsuario = idUsuarioActual,
                                imageFile = imagenFile,
                                onSuccess = {
                                    navController.navigate("agregarIngredientes/${it.IdReceta}")
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Receta subida con éxito.")
                                    }
                                },
                                onError = { errorMessage ->
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Error: $errorMessage")
                                    }
                                }
                            )

                            /* Option 2: If you want to keep using Firebase first
                            subirRecetaViewModel.uploadImageToFirebase(
                                imagenFile,
                                onSuccess = { imageUrl ->
                                    // This would require modifying your API endpoint
                                    subirRecetaViewModel.uploadPlatilloWithImageUrl(
                                        titulo = titulo,
                                        descripcion = descripcion,
                                        idUsuario = idUsuarioActual,
                                        imageUrl = imageUrl,
                                        onSuccess = {
                                            navController.navigate("agregarIngredientes/${it.IdReceta}")
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Receta subida con éxito.")
                                            }
                                        },
                                        onError = { errorMessage ->
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Error: $errorMessage")
                                            }
                                        }
                                    )
                                },
                                onError = { errorMessage ->
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Error al subir la imagen: $errorMessage")
                                    }
                                }
                            )
                            */
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Toma una foto para subir la receta.")
                            }
                        }
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar("Completa todos los campos antes de publicar.")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = amarilloFuerte)
            ) {
                Text("Crear receta", color = cafeOscuro, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Convierte Bitmap a archivo temporal JPG
fun bitmapToFile(bitmap: Bitmap, context: android.content.Context): File {
    val file = File(context.cacheDir, "imagen_receta_${System.currentTimeMillis()}.jpg")
    file.createNewFile()

    val bos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos) // Reduce quality to 80%

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
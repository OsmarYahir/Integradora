package mx.edu.uttt.planeat.models

import mx.edu.uttt.planeat.R
import mx.edu.uttt.planeat.views.RecetaSocial

// ✅ Extensión que convierte un Platillo en RecetaSocial y recibe el nombre del usuario
fun Platillo.toRecetaSocial(nombreUsuario: String): RecetaSocial {
    // Verificar si la imagen es válida, de lo contrario usar una imagen por defecto
    val imagenReceta = if (this.Imagen.isNullOrEmpty()) {
        "url/de/imagen/default.png" // O usa un recurso local si prefieres
    } else {
        this.Imagen
    }

    return RecetaSocial(
        usuario = nombreUsuario,
        imagenReceta = imagenReceta,  // Usar imagen valida o por defecto
        titulo = this.Titulo,
        descripcion = this.Descripcion,
        puntuacion = 4.5f // Establecer un valor predeterminado de puntuación si es necesario
    )
}

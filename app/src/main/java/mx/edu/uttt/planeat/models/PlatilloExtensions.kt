package mx.edu.uttt.planeat.models

import mx.edu.uttt.planeat.R
import mx.edu.uttt.planeat.views.RecetaSocial
fun Platillo.toRecetaSocial(nombreUsuario: String): RecetaSocial {
    // Pass the Base64 string directly to RecetaSocial
    // We'll handle the conversion in the UI
    return RecetaSocial(
        usuario = nombreUsuario,
        imagenReceta = this.Imagen ?: "", // This is already a Base64 string
        titulo = this.Titulo,
        descripcion = this.Descripcion,
        puntuacion = 4.5f
    )
}
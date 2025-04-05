package mx.edu.uttt.planeat.models

import mx.edu.uttt.planeat.R
import mx.edu.uttt.planeat.views.RecetaSocial
fun Platillo.toRecetaSocial(nombreUsuario: String, puntuacion: Float = 0f): RecetaSocial {
    return RecetaSocial(
        usuario = nombreUsuario,
        imagenReceta = this.Imagen ?: "", // Ahora es una URL, no Base64
        titulo = this.Titulo,
        descripcion = this.Descripcion,
        puntuacion = puntuacion
    )
}
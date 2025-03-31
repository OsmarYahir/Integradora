package mx.edu.uttt.planeat.models

import mx.edu.uttt.planeat.R
import mx.edu.uttt.planeat.views.RecetaSocial

// ✅ Extensión que convierte un Platillo en RecetaSocial y recibe el nombre del usuario
fun Platillo.toRecetaSocial(nombreUsuario: String): RecetaSocial {
    return RecetaSocial(
        usuario = nombreUsuario,
        imagenUsuario = R.drawable.logo, // Si tienes imágenes dinámicas, ajusta aquí
        imagenReceta = R.drawable.logo,
        titulo = this.Titulo,
        descripcion = this.Descripcion,
        puntuacion = 5.0f
    )
}

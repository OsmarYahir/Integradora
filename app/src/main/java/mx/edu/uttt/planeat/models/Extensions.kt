package mx.edu.uttt.planeat.models

import mx.edu.uttt.planeat.R
import mx.edu.uttt.planeat.views.RecetaSocial

fun Platillo.toRecetaSocial(): RecetaSocial {
    return RecetaSocial(

        usuario = "Usuario #$IdUsuario", // Si quieres, ponle nombre real // Aquí puedes poner un recurso dinámico
        imagenReceta = this.Imagen,   // Idem, si tienes URLs usa Coil o Glide
        titulo = Titulo,
        descripcion = Descripcion,
        puntuacion = 5.0f // Porque tu API no lo da, puedes calcularlo o dejarlo fijo
    )
}


package mx.edu.uttt.planeat.models

data class Comentario(
    val IdComentario: Int,
    val Texto: String,
    val Fecha: String,
    val IdUsuario: Int,
    val IdReceta: Int
)

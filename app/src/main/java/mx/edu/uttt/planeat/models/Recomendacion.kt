package mx.edu.uttt.planeat.models

data class Recomendacion(
    val IdRecomendacion: Int = 0,
    val IdUsuario: Int,
    val IdReceta: Int,
    val Motivo: String,
    val Fecha: String
)

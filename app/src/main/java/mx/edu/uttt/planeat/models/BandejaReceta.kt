package mx.edu.uttt.planeat.models

data class BandejaReceta(
    val IdBandejaR: Int = 0 ,
    val TipoComida: String,
    val IdUsuario: Int,
    val IdReceta: Int,
    val IdCalendario: Int
)
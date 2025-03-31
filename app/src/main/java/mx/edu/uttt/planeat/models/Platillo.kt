package mx.edu.uttt.planeat.models

data class Platillo(
    val IdReceta: Int,
    val Titulo: String,
    val Descripcion: String,
    val Imagen: String,
    val Fecha_Creacion: String,
    val IdUsuario: Int
)
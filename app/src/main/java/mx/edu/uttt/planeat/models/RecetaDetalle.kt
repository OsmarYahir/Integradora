package mx.edu.uttt.planeat.models

data class RecetaDetalle(
    val idReceta: Int,
    val titulo: String,
    val descripcion: String,
    val imagenReceta: String, // Usamos String si es URL, o cambia a Int si es un recurso drawable
    val ingredientes: List<String>,
    val pasos: List<String>,
    val puntuacion: Float
)

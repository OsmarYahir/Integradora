package mx.edu.uttt.planeat.response

import mx.edu.uttt.planeat.models.BandejaReceta
import mx.edu.uttt.planeat.network.ApiClient // ⬅️ importa correctamente tu ApiClient

class BandejaRecetaRepository {
    suspend fun guardarBandejaReceta(bandeja: BandejaReceta): Boolean {
        return try {
            val response = ApiClient.apiService.guardarBandejaReceta(bandeja)
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun obtenerBandejaRecetas(): List<BandejaReceta> {
        return try {
            ApiClient.apiService.getBandejaRecetas()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun obtenerTodasLasBandejas(): List<BandejaReceta> {
        return try {
            ApiClient.apiService.getBandejaRecetas()
        } catch (e: Exception) {
            emptyList()
        }
    }


}

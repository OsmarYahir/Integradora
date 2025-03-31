    package mx.edu.uttt.planeat.response

    import android.content.Context
    import android.content.SharedPreferences

    class UserPreferences(context: Context) {
        private val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)

        fun saveUserId(userId: Int) {
            sharedPreferences.edit().putInt("userId", userId).apply()
        }

        fun getUserId(): Int {
            return sharedPreferences.getInt("userId", -1)  // Devuelve -1 si no está guardado
        }

        fun clearUserId() {
            sharedPreferences.edit().remove("userId").apply()
        }
    }

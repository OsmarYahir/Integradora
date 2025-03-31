package mx.edu.uttt.planeat.states

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import mx.edu.uttt.planeat.viewmodel.LoginViewModel

class LoginViewModelFactory(private val context: Context) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(context) as T  // Llamamos al constructor de LoginViewModel con el contexto
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

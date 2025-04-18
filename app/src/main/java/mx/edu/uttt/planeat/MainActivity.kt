package mx.edu.uttt.planeat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import mx.edu.uttt.planeat.states.LoginViewModelFactory
import mx.edu.uttt.planeat.ui.theme.PlanEatTheme
import mx.edu.uttt.planeat.viewmodel.LoginViewModel

class MainActivity : ComponentActivity() {

    private lateinit var navHostController: NavHostController
    //val sessionViewModel: SessionViewModel = viewModel()

    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(applicationContext) // Pasar el contexto a la factory
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            navHostController = rememberNavController()
            PlanEatTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavigationWrapper(navHostController )
                }
            }
        }
    }

}
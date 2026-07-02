package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.screens.AdminComplaintManagementScreen
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AdminSettingsScreen
import com.example.ui.screens.AdminStudentManagementScreen
import com.example.ui.screens.ComplaintDetailsScreen
import com.example.ui.screens.ComplaintHistoryScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.RegisterScreen
import com.example.ui.screens.StudentDashboardScreen
import com.example.ui.screens.StudentProfileScreen
import com.example.ui.screens.SubmitComplaintScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        val viewModel: MainViewModel = viewModel()
        val currentScreen by viewModel.currentScreen.collectAsState()
        val context = LocalContext.current

        // Native System Back Button Navigation handler
        BackHandler(
            enabled = currentScreen != Screen.Login &&
                      currentScreen != Screen.Splash &&
                      currentScreen != Screen.StudentDashboard &&
                      currentScreen != Screen.AdminDashboard
        ) {
            viewModel.navigateBack()
        }

        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          Box(modifier = Modifier.padding(innerPadding)) {
              when (val screen = currentScreen) {
                  is Screen.Splash -> SplashScreen()
                  
                  is Screen.Login -> LoginScreen(viewModel) { msg ->
                      Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                  }
                  
                  is Screen.Register -> RegisterScreen(viewModel) { msg ->
                      Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                  }
                  
                  is Screen.StudentDashboard -> StudentDashboardScreen(viewModel) { msg ->
                      Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                  }
                  
                  is Screen.SubmitComplaint -> SubmitComplaintScreen(
                      viewModel = viewModel,
                      editingComplaintId = null
                  ) { msg ->
                      Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                  }
                  
                  is Screen.ComplaintHistory -> ComplaintHistoryScreen(viewModel)
                  
                  is Screen.ComplaintDetails -> ComplaintDetailsScreen(
                      viewModel = viewModel,
                      complaintId = screen.complaintId
                  ) { msg ->
                      Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                  }
                  
                  is Screen.StudentProfile -> StudentProfileScreen(viewModel) { msg ->
                      Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                  }
                  
                  is Screen.AdminDashboard -> AdminDashboardScreen(viewModel)
                  
                  is Screen.AdminComplaintManagement -> AdminComplaintManagementScreen(viewModel) { msg ->
                      Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                  }
                  
                  is Screen.AdminStudentManagement -> AdminStudentManagementScreen(viewModel) { msg ->
                      Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                  }
                  
                  is Screen.AdminSettings -> AdminSettingsScreen(viewModel)
              }
          }
        }
      }
    }
  }
}

@Composable
fun Box(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    androidx.compose.foundation.layout.Box(modifier = modifier) {
        content()
    }
}

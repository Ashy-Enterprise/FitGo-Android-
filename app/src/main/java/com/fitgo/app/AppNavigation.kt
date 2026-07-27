package com.fitgo.app

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fitgo.app.screens.DashboardScreen
import com.fitgo.app.screens.LandingScreen

sealed class Screen(val route: String) {
    data object Landing : Screen("landing")
    data object Dashboard : Screen("dashboard")
}

@Composable
fun FitGoApp() {
    val navController = rememberNavController()
    val viewModel: DashboardViewModel = viewModel()

    NavHost(navController = navController, startDestination = Screen.Landing.route) {
        composable(Screen.Landing.route) {
            LandingScreen(
                onOpenDashboard = { navController.navigate(Screen.Dashboard.route) }
            )
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(viewModel = viewModel)
        }
    }
}

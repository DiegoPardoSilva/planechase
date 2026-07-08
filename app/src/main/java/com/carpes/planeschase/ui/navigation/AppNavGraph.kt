package com.carpes.planeschase.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.carpes.planeschase.ui.menu.MainMenuScreen
import com.carpes.planeschase.ui.planes.PlaneViewerScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "menu") {
        composable("menu") {
            MainMenuScreen(onNavigateToPlanes = { navController.navigate("planes") })
        }
        composable("planes") {
            PlaneViewerScreen(onBack = { navController.popBackStack() })
        }
    }
}

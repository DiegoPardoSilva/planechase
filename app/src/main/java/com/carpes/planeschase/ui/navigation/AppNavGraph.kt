package com.carpes.planeschase.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.carpes.planeschase.ui.menu.MainMenuScreen
import com.carpes.planeschase.ui.planes.PlaneViewerScreen
import com.carpes.planeschase.ui.gallery.GalleryScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "menu") {
        composable("menu") {
            MainMenuScreen(
                onNavigateToPlanes = { deckId ->
                    if (deckId != null) {
                        navController.navigate("planes?deckId=$deckId")
                    } else {
                        navController.navigate("planes")
                    }
                },
                onNavigateToGallery = { navController.navigate("gallery") }
            )
        }
        composable(
            route = "planes?deckId={deckId}",
            arguments = listOf(navArgument("deckId") { 
                type = NavType.IntType
                defaultValue = -1 
            })
        ) { backStackEntry ->
            val deckId = backStackEntry.arguments?.getInt("deckId")?.takeIf { it != -1 }
            PlaneViewerScreen(
                deckId = deckId,
                onBack = { navController.popBackStack() }
            )
        }
        composable("gallery") {
            GalleryScreen(onBack = { navController.popBackStack() })
        }
    }
}

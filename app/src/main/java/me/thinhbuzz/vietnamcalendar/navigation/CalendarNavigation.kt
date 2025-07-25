package me.thinhbuzz.vietnamcalendar.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import me.thinhbuzz.vietnamcalendar.screens.CalendarScreen
import me.thinhbuzz.vietnamcalendar.screens.SettingsScreen

@Composable
fun CalendarNavigation() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "calendar"
    ) {
        composable("calendar") {
            CalendarScreen(
                onNavigateToSettings = {
                    navController.navigate("settings")
                }
            )
        }
        
        composable("settings") {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
package com.pratik.rems.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
// Import your screens
import com.pratik.rems.ui.screens.HomeScreen
import com.pratik.rems.ui.screens.AddReminderScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        // 1. HOME SCREEN ROUTE
        composable(route = Screen.Home.route) {
            HomeScreen(
                onAddClick = {
                    // Navigates to the Add screen without an ID (New Mode)
                    navController.navigate(Screen.AddReminder.route)
                },
                onEditClick = { reminderId ->
                    // Navigates to the Add screen WITH an ID (Edit Mode)
                    // We use the optional argument syntax: ?reminderId=value
                    navController.navigate("${Screen.AddReminder.route}?reminderId=$reminderId")
                }
            )
        }

        // 2. ADD / EDIT REMINDER SCREEN ROUTE
        // We define the route with an optional argument using "?reminderId={reminderId}"
        composable(
            route = "${Screen.AddReminder.route}?reminderId={reminderId}",
            arguments = listOf(
                navArgument("reminderId") {
                    type = NavType.IntType
                    defaultValue = -1 // -1 means "New Reminder" mode
                }
            )
        ) { backStackEntry ->
            // Extract the ID from the navigation arguments
            val id = backStackEntry.arguments?.getInt("reminderId") ?: -1

            AddReminderScreen(
                reminderId = id,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // 3. SETTINGS SCREEN ROUTE (Optional/Placeholder)
        composable(route = Screen.Settings.route) {
            // If you decide to build a dedicated settings page later
            Text(text = "Settings Screen")
        }

        // 4. DETAIL SCREEN ROUTE (Optional/Placeholder)
        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("reminderId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("reminderId") ?: 0
            Text(text = "Detail View for ID: $id")
        }
    }
}
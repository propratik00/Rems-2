package com.pratik.rems.ui.navigation

/**
 * This sealed class defines the routes for our navigation.
 * Each object represents a different screen in the app.
 */
sealed class Screen(val route: String) {
    // The main dashboard
    object Home : Screen("home_screen")

    // The screen to create a new reminder
    object AddReminder : Screen("add_reminder_screen")

    // The screen to see details of a specific reminder
    // {reminderId} is a placeholder for the actual ID
    object Detail : Screen("detail_screen/{reminderId}") {
        fun createRoute(reminderId: Int) = "detail_screen/$reminderId"
    }

    // The settings screen
    object Settings : Screen("settings_screen")
}
package com.contractorestimator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.contractorestimator.ui.screens.*
import com.contractorestimator.ui.theme.ContractorEstimatorTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ContractorEstimatorTheme {
                MainScreen()
            }
        }
    }
}

sealed class Screen(val route: String, val title: String) {
    object Home : Screen("home", "Dashboard")
    object NewEstimate : Screen("new_estimate", "New Estimate")
    object Summary : Screen("summary/{estimateId}", "Summary")
    object SavedEstimates : Screen("saved_estimates", "History")
    object Settings : Screen("settings", "Settings")
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                val items = listOf(Screen.Home, Screen.SavedEstimates, Screen.Settings)
                
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { /* Icon here */ },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen(navController) }
            composable(Screen.NewEstimate.route) { NewEstimateScreen(navController) }
            composable(Screen.Summary.route) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("estimateId")
                SummaryScreen(navController, id)
            }
            composable(Screen.SavedEstimates.route) { SavedEstimatesScreen(navController) }
            composable(Screen.Settings.route) { SettingsScreen(navController) }
        }
    }
}

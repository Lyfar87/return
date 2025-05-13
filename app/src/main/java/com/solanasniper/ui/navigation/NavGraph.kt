package com.solanasniper.ui.navigation

import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.Composable
import com.solanasniper.ui.screens.ConfigListScreen
import com.solanasniper.ui.screens.MainScreen
import com.solanasniper.ui.screens.SettingsScreen
import com.solanasniper.ui.screens.SniperScreen
import com.solanasniper.ui.viewmodels.ConfigListViewModel
import com.solanasniper.ui.viewmodels.MainViewModel
import com.solanasniper.ui.viewmodels.SniperViewModel

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        // Главный экран
        composable(Screen.Main.route) {
            MainScreen(
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                viewModel = hiltViewModel<MainViewModel>()
            )
        }

        // Настройки
        composable(Screen.Settings.route) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() },
                viewModel = hiltViewModel()
            )
        }

        // Список конфигураций
        composable(Screen.ConfigList.route) {
            ConfigListScreen(
                onAddClick = { navController.navigate(Screen.Sniper.route) },
                onEditClick = { configId ->
                    navController.navigate("${Screen.Sniper.route}/$configId")
                },
                viewModel = hiltViewModel<ConfigListViewModel>()
            )
        }

        // Экран снайпера (с параметром)
        composable(
            route = Screen.Sniper.routeWithArg,
            arguments = listOf(
                navArgument(Screen.Sniper.ARG_CONFIG_ID) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            SniperScreen(
                poolAddress = backStackEntry.arguments?.getString(Screen.Sniper.ARG_CONFIG_ID),
                onBackClick = { navController.popBackStack() },
                viewModel = hiltViewModel<SniperViewModel>()
            )
        }
    }
}

// Определение экранов
sealed class Screen(val route: String) {
    object Main : Screen("main")
    object Settings : Screen("settings")
    object ConfigList : Screen("config_list")
    object Sniper : Screen("sniper") {
        const val routeWithArg = "$route/{configId}"
        const val ARG_CONFIG_ID = "configId"
    }
}
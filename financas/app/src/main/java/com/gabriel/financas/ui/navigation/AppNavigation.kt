package com.gabriel.financas.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gabriel.financas.ui.addtransaction.AddTransactionScreen
import com.gabriel.financas.ui.home.HomeScreen
import kotlinx.serialization.Serializable

@Serializable object HomeRoute
@Serializable object AddTransactionRoute

@Composable
fun AppNavigation(widthClass: androidx.compose.material3.windowsizeclass.WindowWidthSizeClass) {
    val nav = rememberNavController()
    NavHost(nav, startDestination = HomeRoute, modifier = Modifier.fillMaxSize()) {
        composable<HomeRoute> {
            HomeScreen(widthClass = widthClass, onAdd = { nav.navigate(AddTransactionRoute) })
        }
        composable<AddTransactionRoute> {
            AddTransactionScreen(onDone = { nav.popBackStack() })
        }
    }
}

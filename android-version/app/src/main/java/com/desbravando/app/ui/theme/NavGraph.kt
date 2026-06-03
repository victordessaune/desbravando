package com.desbravando.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.desbravando.app.Login

@Composable
fun NavGraph(
    onLoginClick: (String, String) -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            Login(
                onLoginClick = onLoginClick,
                onNavigateToRegister = onNavigateToRegister
            )
        }
        // Se decidir colocar a rota de registro aqui dentro do NavGraph no futuro, ajuda a centralizar a navegação!
    }
}
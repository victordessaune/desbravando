package com.desbravando.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.desbravando.app.Login
import com.desbravando.app.RegisterAccountActivity

@Composable
fun NavGraph(){
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            // PASSANDO A AÇÃO: O segredo está aqui!
            Login(
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }
    }
}
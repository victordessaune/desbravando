package com.desbravando.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.desbravando.app.ui.theme.Purple
import com.desbravando.app.ui.theme.Gray

@Composable
fun BottomBar(
    selectedRoute: String,
    onItemSelected: (String) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        contentColor = Purple
    ) {
        NavigationBarItem(
            selected = selectedRoute == "home",
            onClick = { onItemSelected("home") },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Início") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Purple,
                selectedTextColor = Purple,
                unselectedIconColor = Gray,
                unselectedTextColor = Gray,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = selectedRoute == "search",
            onClick = { onItemSelected("search") },
            icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            label = { Text("Busca") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Purple,
                selectedTextColor = Purple,
                unselectedIconColor = Gray,
                unselectedTextColor = Gray,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = selectedRoute == "profile",
            onClick = { onItemSelected("profile") },
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Perfil") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Purple,
                selectedTextColor = Purple,
                unselectedIconColor = Gray,
                unselectedTextColor = Gray,
                indicatorColor = Color.Transparent
            )
        )
    }
}

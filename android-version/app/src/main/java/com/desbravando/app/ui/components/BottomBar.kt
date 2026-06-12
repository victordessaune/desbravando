package com.desbravando.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.desbravando.app.ui.theme.DesbravandoTheme
import com.desbravando.app.ui.theme.Purple
import com.desbravando.app.ui.theme.Gray

@Composable
fun BottomBar(
    selectedRoute: String,
    onItemSelected: (String) -> Unit
) {
    NavigationBar(
        modifier = Modifier.shadow(
            elevation = 20.dp,
            shape = RectangleShape,
            spotColor = Purple
        ),
        containerColor = Color.White,
        contentColor = Purple,

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
            selected = selectedRoute == "explore",
            onClick = { onItemSelected("explore") },
            icon = { Icon(Icons.Default.Search, contentDescription = "Explorar") },
            label = { Text("Explorar") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Purple,
                selectedTextColor = Purple,
                unselectedIconColor = Gray,
                unselectedTextColor = Gray,
                indicatorColor = Color.Transparent
            )
        )

        // Botão Central com Cor Dinâmica
        val isAddSelected = selectedRoute == "add"
        NavigationBarItem(
            selected = isAddSelected,
            onClick = { onItemSelected("add") },
            icon = {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(if (isAddSelected) Purple else Gray, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Adicionar",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            },
            label = null,
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent
            )
        )

        NavigationBarItem(
            selected = selectedRoute == "favorites",
            onClick = { onItemSelected("favorites") },
            icon = { Icon(Icons.Default.Favorite, contentDescription = "Favoritos") },
            label = { Text("Favoritos") },
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
            icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
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

@Preview(showBackground = true)
@Composable
fun BottomBarPreview() {
    DesbravandoTheme {
        BottomBar(
            selectedRoute = "add",
            onItemSelected = {}
        )
    }
}

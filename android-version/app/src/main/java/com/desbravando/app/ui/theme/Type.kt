package com.desbravando.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.desbravando.app.R

// 1. Uma única família com TODOS os pesos que você tem na pasta
val Poppins = FontFamily(
    Font(resId = R.font.poppins_light, weight = FontWeight.Light),
    Font(resId = R.font.poppins_regular, weight = FontWeight.Normal),
    Font(resId = R.font.poppins_medium, weight = FontWeight.Medium),       // <-- Seu Medium aqui
    Font(resId = R.font.poppins_semibold, weight = FontWeight.SemiBold),   // <-- Seu SemiBold aqui
    Font(resId = R.font.poppins_bold, weight = FontWeight.Bold)
)

// 2. Configuração do Typography do seu App
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = Poppins, // Troquei o Default para Poppins para o seu app usar ela por padrão
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Você pode ativar e customizar os outros aqui depois se quiser */
)
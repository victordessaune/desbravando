package com.desbravando.app.ui.utils

import androidx.compose.ui.graphics.Color
import com.desbravando.app.R

data class WeatherVisual(val icon: Int, val tint: Color)

fun getWeatherVisual(condition: String): WeatherVisual {
    return when (condition) {
        "Clear"        -> WeatherVisual(R.drawable.ic_sun,    Color(0xFFFFCC00)) // amarelo
        "Clouds"       -> WeatherVisual(R.drawable.ic_cloud,  Color(0xFF90A4AE)) // cinza azulado
        "Rain",
        "Drizzle"      -> WeatherVisual(R.drawable.ic_rain,   Color(0xFF42A5F5)) // azul
        "Thunderstorm" -> WeatherVisual(R.drawable.ic_thunder,Color(0xFF7E57C2)) // roxo
        "Snow"         -> WeatherVisual(R.drawable.ic_snow,   Color(0xFFB3E5FC)) // azul claro
        "Mist",
        "Fog",
        "Haze"         -> WeatherVisual(R.drawable.ic_fog,    Color(0xFFB0BEC5)) // cinza
        else           -> WeatherVisual(R.drawable.ic_sun,    Color(0xFFFFCC00))
    }
}
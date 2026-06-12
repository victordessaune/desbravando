package com.desbravando.app.data.model

data class WeatherResponse(
    val main: Main,
    val weather: List<WeatherDescription>
)

data class Main(
    val temp: Double
)

data class WeatherDescription(
    val main: String,
    val description: String
)
package com.example.androidweather

data class WeatherResponse (
    val name: String,
    val main: MainWeather,
    val weather: List<Weather>,
    val wind: Wind
)


data class MainWeather(
    val temp: Double,
    val humidity: Int,
    val feels_like: Double
)

data class Weather(

    val description: String,
    val icon: String
)

data class Wind(
    val speed: Double
)
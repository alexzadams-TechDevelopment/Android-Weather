package com.example.androidweather

data class Country(val countryName: String, val code: String){

    override fun toString(): String = countryName


//    override fun toString(): String {
//        return countryName
//    }
}

package com.example.androidweather


import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.androidweather.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import android.widget.AdapterView
import android.widget.VideoView
//import com.google.android.gms.location.FusedLocationProviderClient
import java.util.Locale
import android.net.Uri
import android.graphics.drawable.GradientDrawable


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val APIkey: String = "b57f2441e36a0d4d23953cadfbfe78c9"
    private var city: String = "London"
    private var countryCode: String = "UK"
    private var location: String = "$city, $countryCode"


    //For user location (remove if not used on completion)
    //private late init var fusedLocationClient: FusedLocationProviderClient



    //Used to call the API and get the weather data using the city name and & API key
    private fun getWeatherData(){
        lifecycleScope.launch { //Creates a coroutin while app makes request for weather data
            try{
            val result = withContext(Dispatchers.IO) { //Runs a thread for an input/output call for API data.
                //searches with the URL provided.                                  (readText(Charsets.UTF_8) is used so different text characters are recognised while making this request.
                URL("https://api.openweathermap.org/data/2.5/weather?q=$location&units=metric&appid=$APIkey").readText(Charsets.UTF_8)

            }
                onPostExecute(result)

        }
        catch (e: Exception){
            e.printStackTrace() //For if the API request fails to connect.
            //todo:: Implement feature to notify users in app when this happens (For example: When theres no internet.)
            inputDisplay(false)
            //println("Could not find Location, Check internet and if the search is correct")
        }

        }
    }

    fun onPostExecute(result: String?){
        inputDisplay(true)

        try{
            val jsonObj = JSONObject(result)
            val main = jsonObj.getJSONObject("main")
            val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
            val temp = main.getString("temp")
            val humidity = main.getString("humidity")
            val wind = jsonObj.getJSONObject("wind")
            val icon = weather.getString("icon")
            val feelLike = main.getDouble("feels_like")




            //video TODO:: Edit the videos within the raw file to have reduced quality to run better/smoothly on the android device.
            var weatherMedia = R.raw.sun_cloudy_day //Used to store the weather status from OpenWeather API JSON (Sun_day being a placeholder)

            val weatherStatus = weather.getString("description")
            binding.cityText.text = location //Updates the location name in a suitable time

            binding.weatherText.text = weatherStatus
            binding.tempText.text = "$temp °C" //Temperature

            binding.feelText.text = "$feelLike °C" //Feels Temperature

            val windString = wind.getString("speed")
            binding.windText.text = "$windString m/s" //Wind Speed
            binding.humidityText.text = "$humidity%" //Humidity

            var dayTime = true // To declare if the area searched is day or not using the switch statement below.

            //Updates the image on display
            val weatherIcon = when(icon){ //TODO:: Replace the current images with my own design (current is from open weather AI as a place holder)
                //Day
                "01d" -> {weatherMedia = R.raw.sun_day; R.drawable.d01}
                "02d" -> {weatherMedia = R.raw.sun_cloudy_day; R.drawable.d02}
                "03d" -> {weatherMedia = R.raw.cloudy_day; R.drawable.d03}
                "04d" -> {weatherMedia = R.raw.cloudy_day; R.drawable.d04}
                "09d" -> {weatherMedia = R.raw.rain_day; R.drawable.d09}
                "10d" -> {weatherMedia = R.raw.cloudy_day; R.drawable.d10} //update to more suitable video
                "11d" -> {weatherMedia = R.raw.thunder_day; R.drawable.d11}
                "13d" -> {weatherMedia = R.raw.snow_day; R.drawable.d13}
                "50d" -> {weatherMedia = R.raw.fog_day; R.drawable.d50}

                //Night
                "01n" -> {dayTime = false; weatherMedia = R.raw.night; R.drawable.n01;}
                "02n" -> {dayTime = false; weatherMedia = R.raw.cloudy_night; R.drawable.n02;}
                "03n" -> {dayTime = false; weatherMedia = R.raw.cloudy_night; R.drawable.n03;}
                "04n" -> {dayTime = false; weatherMedia = R.raw.cloudy_night; R.drawable.n04;}
                "09n" -> {dayTime = false; weatherMedia = R.raw.rain_night; R.drawable.n09;}
                "10n" -> {dayTime = false; weatherMedia = R.raw.cloudy_night; R.drawable.n10;}
                "11n" -> {dayTime = false; weatherMedia = R.raw.thunder_night; R.drawable.n11;}
                "13n" -> {dayTime = false; weatherMedia = R.raw.snow_night; R.drawable.n13;}
                "50n" -> {dayTime = false; weatherMedia = R.raw.cloudy_night; R.drawable.n50;} //update to more suitable video

                else -> {R.drawable.unknown; R.raw.sun_cloudy_day}
            }
            binding.weatherImageView.setImageResource(weatherIcon)



            //Play video
            val video = findViewById<VideoView>(R.id.videoBackground) //Selects the videoView entity in the activity file.
            video.setVideoURI(Uri.parse("android.resource://$packageName/$weatherMedia")) //uses weatherMedia to select the required weather condition to play the video.

            //To play and loop the video in the background
            video.setOnPreparedListener {
                it.isLooping = true
                video.start()
            }


            //To update box colours.
            guiColour(dayTime)
        }
        catch (e: Exception){
            e.printStackTrace() //sort later
        }
    }

    // Used to change the colour of elements in GUI depending on the day & night cycle of the searched area.
    private fun guiColour(dayTime: Boolean){

        val backgroundBox = binding.infoBox2.background.mutate() as GradientDrawable
        val backgroundFade = binding.fade.background.mutate() as GradientDrawable

        if(dayTime){ //Day Time (blue)
            binding.root.setBackgroundColor(Color.parseColor("#41B1E0"))




            backgroundFade.colors = intArrayOf(Color.parseColor("#41b1e0"), Color.TRANSPARENT)
            backgroundBox.colors = intArrayOf(Color.parseColor("#236078"), Color.parseColor("#41b1e0"))

            binding.btnSearch.setBackgroundColor(Color.parseColor("#1D5169"))
            binding.btnSearch.setTextColor(Color.WHITE)


        }else{ //Night Time (purple)
            binding.root.setBackgroundColor(Color.parseColor("#7731B5"))


            backgroundBox.colors = intArrayOf(Color.parseColor("#4B1F73"), Color.parseColor("#8A36D6"))



            backgroundFade.colors = intArrayOf(Color.parseColor("#7731B5"), Color.TRANSPARENT)

            binding.btnSearch.setBackgroundColor(Color.parseColor("#2F154A"))
            binding.btnSearch.setTextColor(Color.WHITE)
        }
    }

    //Changes the app GUI when there's an incorrect input or connection issue.
    private fun inputDisplay(active: Boolean){
        when(active){
            true -> {
                binding.cityText.text = "..."
                binding.humidityText.visibility = View.VISIBLE
                binding.humidityLabel.visibility = View.VISIBLE
                binding.windText.visibility = View.VISIBLE
                binding.windLabel.visibility = View.VISIBLE
                binding.feelText.visibility = View.VISIBLE
                binding.feelLabel.visibility = View.VISIBLE

            }

            false -> {
                binding.cityText.text = "Invalid Location"
                binding.weatherText.text = "Location not found or No Connection."
                binding.tempText.text = "Please try again!"

                binding.humidityText.visibility = View.INVISIBLE
                binding.humidityLabel.visibility = View.INVISIBLE
                binding.windText.visibility = View.INVISIBLE
                binding.windLabel.visibility = View.INVISIBLE
                binding.feelText.visibility = View.INVISIBLE
                binding.feelLabel.visibility = View.INVISIBLE

                //changes image to be unknown
                val unknowIcon = R.drawable.unknown
                binding.weatherImageView.setImageResource(unknowIcon)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //Standard start up onCreate code.
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.cityText.text = location //Loads default location
        getWeatherData()

        val countries = Locale.getISOCountries().map{
            countryCode -> val locale = Locale("", countryCode)

            Country(
                countryName = locale.displayCountry,
                code = countryCode
            )
        }.sortedBy { it.countryName }

        //Spinner Set up//
        val adapter = ArrayAdapter(
            this,
            R.layout.spinner_box, //Now uses spinner_Box.xml design in the layout File.
            countries
        )

//        val position = countries.indexOfFirst { it.countryName == "United Kingdom" }
//        binding.countrySpinner.setSelection(position)

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        binding.countrySpinner.adapter = adapter

        //Spinner Set up//
        binding.countrySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedCountry = //sort later
                        parent?.getItemAtPosition(position) as Country

                    val countryCodeSpinner = selectedCountry.code //sort later


                    countryCode = countryCodeSpinner
                    println(countryCodeSpinner)
                    Log.d("Country", countryCodeSpinner)
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        val position = countries.indexOfFirst { it.countryName == "United Kingdom" } //Selects specific country from the items available
        binding.countrySpinner.setSelection(position) //Sets the country provided to appear first in spinner on create.

        //Searches the city when the user enters the name.
        binding.btnSearch.setOnClickListener {
            if(binding.cityInput.text.toString() == ""){
                //binding.cityInput.hint = "Please Enter a city here!"
                //binding.cityInput.setHintTextColor(Color.RED) //todo: Change to an ideal color or remove

                //This is added to present an animated display to the user that the textInput is empty
                binding.cityInput.animate()
                    .translationX(20f)
                    .setDuration(50)
                    .withEndAction {
                        binding.cityInput.animate()
                            .translationX(-20f)
                            .setDuration(50)
                            .withEndAction {
                                binding.cityInput.animate()
                                    .translationX(0f)
                                    .setDuration(50)
                            }
                    }
            } else{ //For when the user inputs text in cityInput.
                location = binding.cityInput.text.toString() +", $countryCode"
                binding.cityText.text = location
                getWeatherData()
            }
        }
    }

}
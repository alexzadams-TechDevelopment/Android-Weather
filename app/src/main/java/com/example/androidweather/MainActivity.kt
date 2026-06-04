package com.example.androidweather

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.androidweather.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val APIkey: String = "b57f2441e36a0d4d23953cadfbfe78c9"
    private var city: String = "London"
    private var countryCode: String = "UK"
    private var location: String = "$city, $countryCode"




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


            val weatherStatus = weather.getString("description")
            binding.cityText.text = location //Updates the location name in a suitable time

            binding.weatherText.text = weatherStatus
            binding.tempText.text = "$temp °C"

            val windString = wind.getString("speed")
            binding.windText.text = "$windString m/s"
            binding.humidityText.text = humidity

            //Updates the image on display
            val weatherIcon = when(icon){ //TODO:: Replace the current images with my own design (current is from open weather AI as a place holder)
                //Day
                "01d" -> R.drawable.d01
                "02d" -> R.drawable.d02
                "03d" -> R.drawable.d03
                "04d" -> R.drawable.d04
                "09d" -> R.drawable.d09
                "10d" -> R.drawable.d10
                "11d" -> R.drawable.d11
                "13d" -> R.drawable.d13
                "50d" -> R.drawable.d50

                //Night
                "01n" -> R.drawable.n01
                "02n" -> R.drawable.n02
                "03n" -> R.drawable.n03
                "04n" -> R.drawable.n04
                "09n" -> R.drawable.n09
                "10n" -> R.drawable.n10
                "11n" -> R.drawable.n11
                "13n" -> R.drawable.n13
                "50n" -> R.drawable.n50

                else -> R.drawable.unknown
            }



            binding.weatherImageView.setImageResource(weatherIcon)


        }
        catch (e: Exception){
            e.printStackTrace() //sort later
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
            }


            false -> {
                binding.cityText.text = "Invalid Location"
                binding.weatherText.text = "Location not found or No Connection."
                binding.tempText.text = "Please try again!"

                binding.humidityText.visibility = View.INVISIBLE
                binding.humidityLabel.visibility = View.INVISIBLE
                binding.windText.visibility = View.INVISIBLE
                binding.windLabel.visibility = View.INVISIBLE

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


        //Searches the city when the user enters the name.
        binding.btnSearch.setOnClickListener {
            location = binding.cityInput.text.toString() +", $countryCode"
            binding.cityText.text = location
            getWeatherData()
        }
    }
}
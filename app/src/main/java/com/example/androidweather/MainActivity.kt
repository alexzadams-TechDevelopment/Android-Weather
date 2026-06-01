package com.example.androidweather

import android.os.Bundle
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
        }

        }
    }

    fun onPostExecute(result: String?){

        try{
            val jsonObj = JSONObject(result)
            val main = jsonObj.getJSONObject("main")
            val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
            val temp = main.getString("temp")
            val humidity = main.getString("humidity")
            val wind = jsonObj.getJSONObject("wind")

            val weatherStatus = weather.getString("description")
            binding.weatherText.text = weatherStatus
            binding.tempText.text = "$temp °C"

            val windString = wind.getString("speed")
            binding.windText.text = "$windString m/s"
            binding.humidityText.text = humidity
        }
        catch (e: Exception){
            e.printStackTrace() //sort later
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        //Standard start up onCreate code.
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        
        binding.cityText.text = location
        getWeatherData()


        //Searches the city when the user enters the name.
        binding.btnSearch.setOnClickListener {
            //TODO:: implement search feature.
        }



    }
}
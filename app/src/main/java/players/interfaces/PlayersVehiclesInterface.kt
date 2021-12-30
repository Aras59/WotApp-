package players.interfaces

import players.playerVehicleStats.Data
import players.playerVehicleStats.VehicleStats
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface PlayersVehiclesInterface {


    @GET("wot/tanks/stats/?application_id=098b54f4d269cc5f29f074e671fdcc00")
    fun getPlayersVehiclesStats(@Query("account_id") account_id:String) : Call<VehicleStats>

    companion object {

        var BASE_URL_EU = "https://api.worldoftanks.eu"
        var BASE_URL_RU = "https://api.worldoftanks.ru"
        var BASE_URL_ASIA = "https://api.worldoftanks.asia"
        var BASE_URL_NA = "https://api.worldoftanks.com"

        fun createEU() : PlayersVehiclesInterface {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL_EU)
                .build()
            return retrofit.create( PlayersVehiclesInterface::class.java)

        }

        fun createRU() : PlayersVehiclesInterface {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL_RU)
                .build()
            return retrofit.create( PlayersVehiclesInterface::class.java)

        }

        fun createASIA() : PlayersVehiclesInterface {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL_ASIA)
                .build()
            return retrofit.create( PlayersVehiclesInterface::class.java)

        }

        fun createNA() : PlayersVehiclesInterface {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL_NA)
                .build()
            return retrofit.create(PlayersVehiclesInterface::class.java)

        }
    }
}
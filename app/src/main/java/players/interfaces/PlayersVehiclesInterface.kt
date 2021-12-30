package players.interfaces

import okhttp3.OkHttpClient
import players.playerVehicleStats.Data
import players.playerVehicleStats.VehicleStats
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface PlayersVehiclesInterface {

    @GET("wot/tanks/stats/?application_id=098b54f4d269cc5f29f074e671fdcc00&extra=random")
    fun getPlayersVehiclesStats(@Query("account_id") account_id:String) : Call<VehicleStats>

    companion object {

        var BASE_URL_EU = "https://api.worldoftanks.eu"
        var BASE_URL_RU = "https://api.worldoftanks.ru"
        var BASE_URL_ASIA = "https://api.worldoftanks.asia"
        var BASE_URL_NA = "https://api.worldoftanks.com"

        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .callTimeout(30,TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        fun createEU() : PlayersVehiclesInterface {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL_EU)
                .client(client)
                .build()
            return retrofit.create( PlayersVehiclesInterface::class.java)

        }

        fun createRU() : PlayersVehiclesInterface {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL_RU)
                .client(client)
                .build()
            return retrofit.create( PlayersVehiclesInterface::class.java)

        }

        fun createASIA() : PlayersVehiclesInterface {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL_ASIA)
                .client(client)
                .build()
            return retrofit.create( PlayersVehiclesInterface::class.java)

        }

        fun createNA() : PlayersVehiclesInterface {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL_NA)
                .client(client)
                .build()
            return retrofit.create(PlayersVehiclesInterface::class.java)

        }
    }
}
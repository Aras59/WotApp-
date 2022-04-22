package players.interfaces

import okhttp3.*
import players.playerstats.PlayersList
import players.achive.AchiveReponse
import players.playerInfo.PlayerInfo
import players.playerVehicleStats.VehicleStats
import players.playerstats.Datas
import players.vehicles.VehicleRespond
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url
import java.util.*
import java.util.concurrent.TimeUnit

interface PlayersInterface {

    @GET("wot/account/list/?application_id=098b54f4d269cc5f29f074e671fdcc00&limit=8")
    fun getPlayers(@Query("search") nickname:String,) : Call<PlayerInfo>

    @GET("wot/account/info/?application_id=098b54f4d269cc5f29f074e671fdcc00")
    fun getPlayerPersonalData(@Query("account_id") account_id:String) : Call<Datas>

    @GET("wot/tanks/stats/?application_id=098b54f4d269cc5f29f074e671fdcc00&extra=random")
    fun getPlayersVehiclesStats(@Query("account_id") account_id:String) : Call<VehicleStats>

    @GET("wot/encyclopedia/vehicles/?application_id=098b54f4d269cc5f29f074e671fdcc00&fields=images%2Cname%2Cnation%2Ctier%2Ctype%2Ctank_id")
    fun getVehicleList(): Call<VehicleRespond>

    @GET("wot/encyclopedia/achievements/?application_id=098b54f4d269cc5f29f074e671fdcc00")
    fun getAchiveList(): Call<AchiveReponse>

    @GET
    fun getVehicleLogo(@Url Url: String): Call<ResponseBody>



    companion object {
        val aceTanker = "http://api.worldoftanks.eu/static/2.71.0/wot/encyclopedia/achievement/markOfMastery4.png"
        val markOfMastery1 = "http://api.worldoftanks.eu/static/2.71.0/wot/encyclopedia/achievement/markOfMastery3.png"
        val markOfMastery2 = "http://api.worldoftanks.eu/static/2.71.0/wot/encyclopedia/achievement/markOfMastery2.png"
        val markOfMastery3 = "http://api.worldoftanks.eu/static/2.71.0/wot/encyclopedia/achievement/markOfMastery1.png"
        val BASE_URL_EU = "https://api.worldoftanks.eu"
        val BASE_URL_RU = "https://api.worldoftanks.ru"
        val BASE_URL_ASIA = "https://api.worldoftanks.asia"
        val BASE_URL_NA = "https://api.worldoftanks.com"

        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .callTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        fun createEU() : PlayersInterface {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL_EU)
                .client(PlayersVehiclesInterface.client)
                .build()
            return retrofit.create(PlayersInterface::class.java)

        }

        fun createRU() : PlayersInterface {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL_RU)
                .client(PlayersVehiclesInterface.client)
                .build()
            return retrofit.create(PlayersInterface::class.java)

        }

        fun createASIA() : PlayersInterface {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL_ASIA)
                .client(PlayersVehiclesInterface.client)
                .build()
            return retrofit.create(PlayersInterface::class.java)

        }

        fun createNA() : PlayersInterface {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL_NA)
                .client(PlayersVehiclesInterface.client)
                .build()
            return retrofit.create(PlayersInterface::class.java)

        }
    }
}


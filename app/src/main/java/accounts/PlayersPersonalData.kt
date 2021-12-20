package accounts

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface PlayersPersonalData {
    @GET("wot/account/info/?application_id=098b54f4d269cc5f29f074e671fdcc00")
    fun getPlayerPersonalData(@Query("account_id") account_id:String) : Call<Datas>

    companion object {

        var BASE_URL_EU = "https://api.worldoftanks.eu"
        var BASE_URL_RU = "https://api.worldoftanks.ru"
        var BASE_URL_ASIA = "https://api.worldoftanks.asia"
        var BASE_URL_NA = "https://api.worldoftanks.com"

        fun createEU() :  PlayersPersonalData {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL_EU)
                .build()
            return retrofit.create( PlayersPersonalData::class.java)

        }

        fun createRU() :  PlayersPersonalData {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL_RU)
                .build()
            return retrofit.create( PlayersPersonalData::class.java)

        }

        fun createASIA() :  PlayersPersonalData {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL_ASIA)
                .build()
            return retrofit.create( PlayersPersonalData::class.java)

        }

        fun createNA() :  PlayersPersonalData {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL_NA)
                .build()
            return retrofit.create(PlayersPersonalData::class.java)

        }
    }
}
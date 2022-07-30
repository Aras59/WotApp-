package com.example.wotapp.interfaces

import com.example.wotapp.models.clanratings.ClanRatings
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface ClanRatingsInterface {


    @GET("wot/clanratings/clans/?application_id=098b54f4d269cc5f29f074e671fdcc00")
    fun getClanRatings(@Query("clan_id") clanID:String) : Call<ClanRatings>


    companion object {

        var BASE_URL_EU = "https://api.worldoftanks.eu"
        var BASE_URL_RU = "https://api.worldoftanks.ru"
        var BASE_URL_ASIA = "https://api.worldoftanks.asia"
        var BASE_URL_NA = "https://api.worldoftanks.com"

        private val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .callTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        fun createEU() : ClanRatingsInterface {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL_EU)
                .client(client)
                .build()
            return retrofit.create( ClanRatingsInterface::class.java)

        }

        fun createRU() : ClanRatingsInterface {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL_RU)
                .client(client)
                .build()
            return retrofit.create( ClanRatingsInterface::class.java)

        }

        fun createASIA() : ClanRatingsInterface {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL_ASIA)
                .client(client)
                .build()
            return retrofit.create( ClanRatingsInterface::class.java)

        }

        fun createNA() : ClanRatingsInterface {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL_NA)
                .client(client)
                .build()
            return retrofit.create(ClanRatingsInterface::class.java)

        }
    }
}
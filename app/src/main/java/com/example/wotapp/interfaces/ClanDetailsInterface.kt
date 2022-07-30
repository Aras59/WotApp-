package com.example.wotapp.interfaces

import com.example.wotapp.models.clandetails.ClanDetails
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url
import java.util.concurrent.TimeUnit

interface ClanDetailsInterface {

    @GET("wot/clans/info/?application_id=098b54f4d269cc5f29f074e671fdcc00")
    fun getClanDetails(@Query("clan_id") clanID:String) : Call<ClanDetails>

    @GET
    fun getClanLogo(@Url Url: String): Call<ResponseBody>

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

        fun createEU() : ClanDetailsInterface {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL_EU)
                .client(client)
                .build()
            return retrofit.create( ClanDetailsInterface::class.java)

        }

        fun createRU() : ClanDetailsInterface {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL_RU)
                .client(client)
                .build()
            return retrofit.create( ClanDetailsInterface::class.java)

        }

        fun createASIA() : ClanDetailsInterface {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL_ASIA)
                .client(client)
                .build()
            return retrofit.create( ClanDetailsInterface::class.java)

        }

        fun createNA() : ClanDetailsInterface {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL_NA)
                .client(client)
                .build()
            return retrofit.create(ClanDetailsInterface::class.java)

        }
    }
}
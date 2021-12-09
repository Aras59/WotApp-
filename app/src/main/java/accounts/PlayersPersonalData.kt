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

        var BASE_URL = "https://api.worldoftanks.eu"

        fun create() : PlayersPersonalData {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(PlayersPersonalData::class.java)

        }
    }
}
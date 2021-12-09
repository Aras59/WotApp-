package accounts

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface PlayersInterface {

    @GET("wot/account/list/?application_id=098b54f4d269cc5f29f074e671fdcc00&limit=8")
    fun getPlayers(@Query("search") nickname:String) : Call<PlayersList>

    companion object {

        var BASE_URL = "https://api.worldoftanks.eu"

        fun create() : PlayersInterface {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(PlayersInterface::class.java)

        }
    }
}


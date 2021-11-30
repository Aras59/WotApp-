package accounts

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PlayersInterface {
    //https://api.worldoftanks.eu/wot/account/list/?application_id=098b54f4d269cc5f29f074e671fdcc00&limit=30&search=Lody_z_posypkom
    @GET("wot/account/list/?application_id=098b54f4d269cc5f29f074e671fdcc00&limit=30&search=")
    fun getPlayers(@Query("search") nickname:String) : Call<PList>

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


package players.interfaces

import players.playerstats.PlayersList
import okhttp3.CipherSuite
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*

interface PlayersInterface {

    @GET("wot/account/list/?application_id=098b54f4d269cc5f29f074e671fdcc00&limit=8")
    fun getPlayers(@Query("search") nickname:String,) : Call<PlayersList>

    companion object {

        var BASE_URL_EU = "https://api.worldoftanks.eu"
        var BASE_URL_RU = "https://api.worldoftanks.ru"
        var BASE_URL_ASIA = "https://api.worldoftanks.asia"
        var BASE_URL_NA = "https://api.worldoftanks.com"

        fun createEU() : PlayersInterface {
            val spec = ConnectionSpec.Builder(ConnectionSpec.COMPATIBLE_TLS)
                .tlsVersions(TlsVersion.TLS_1_2, TlsVersion.TLS_1_1, TlsVersion.TLS_1_0)
                .cipherSuites(
                    CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                    CipherSuite.TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256,
                    CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA,
                    CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA)
                .build()
            val okttp = OkHttpClient.Builder()
                .connectionSpecs(Collections.singletonList(spec))
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL_EU)
                .build()
            return retrofit.create(PlayersInterface::class.java)

        }

        fun createRU() : PlayersInterface {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL_RU)
                .build()
            return retrofit.create(PlayersInterface::class.java)

        }

        fun createASIA() : PlayersInterface {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL_ASIA)
                .build()
            return retrofit.create(PlayersInterface::class.java)

        }

        fun createNA() : PlayersInterface {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL_NA)
                .build()
            return retrofit.create(PlayersInterface::class.java)

        }
    }
}


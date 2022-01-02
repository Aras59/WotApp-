package clans.clandetails

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import players.playerstats.Player

data class ClanDetails(
    @SerializedName("data")
    @Expose
    val clan: Map<String, Details>,
    val meta: Meta,
    val status: String
)
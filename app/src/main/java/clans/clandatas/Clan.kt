package clans.clandatas

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Clan(
    @SerializedName("data")
    @Expose
    val data: List<ClanData>,
    val meta: Meta,
    val status: String
)
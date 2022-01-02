package players.playerVehicleStats

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import players.playerstats.Player

data class VehicleStats(
    @SerializedName("data")
    @Expose
    val stats:Map<String, List<Stats>>,
    val meta: Meta,
    val status: String
)
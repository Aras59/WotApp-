package players.playerVehicleStats

import players.vehicles.Tank
import java.io.Serializable

data class VehicleStatsForView(
    val statistic: Stats,
    val tank: Tank
):Serializable

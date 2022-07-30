package com.example.wotapp.models.playerVehicleStats

import com.example.wotapp.models.vehicles.Tank
import java.io.Serializable

data class VehicleStatsForView(
    val statistic: Stats,
    val tank: Tank
):Serializable

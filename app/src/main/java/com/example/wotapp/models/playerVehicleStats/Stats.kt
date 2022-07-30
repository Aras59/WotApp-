package com.example.wotapp.models.playerVehicleStats

import java.io.Serializable

data class Stats(
    val account_id: Int,
    val all: All,
    val clan: Clan,
    val company: Company,
    val frags: Any,
    val globalmap: Globalmap,
    val in_garage: Any,
    val mark_of_mastery: Int,
    val max_frags: Int,
    val max_xp: Int,
    val random: Random,
    val regular_team: RegularTeam,
    val stronghold_defense: StrongholdDefense,
    val stronghold_skirmish: StrongholdSkirmish,
    val tank_id: Int,
    val team: Team
):Serializable
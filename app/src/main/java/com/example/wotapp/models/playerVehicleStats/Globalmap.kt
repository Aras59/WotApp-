package com.example.wotapp.models.playerVehicleStats

import java.io.Serializable

data class Globalmap(
    val avg_damage_assisted: Double,
    val avg_damage_assisted_radio: Double,
    val avg_damage_assisted_track: Double,
    val avg_damage_blocked: Double,
    val battle_avg_xp: Int,
    val battles: Int,
    val battles_on_stunning_vehicles: Int,
    val capture_points: Int,
    val damage_dealt: Int,
    val damage_received: Int,
    val direct_hits_received: Int,
    val draws: Int,
    val dropped_capture_points: Int,
    val explosion_hits: Int,
    val explosion_hits_received: Int,
    val frags: Int,
    val hits: Int,
    val hits_percents: Int,
    val losses: Int,
    val no_damage_direct_hits_received: Int,
    val piercings: Int,
    val piercings_received: Int,
    val shots: Int,
    val spotted: Int,
    val stun_assisted_damage: Int,
    val stun_number: Int,
    val survived_battles: Int,
    val tanking_factor: Double,
    val wins: Int,
    val xp: Int
): Serializable
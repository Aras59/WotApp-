package com.example.wotapp.models.playerVehicleStats

import java.io.Serializable

data class Random(
    val battle_avg_xp: Int,
    val battles: Int,
    val battles_on_stunning_vehicles: Int,
    val capture_points: Int,
    val damage_dealt: Int,
    val damage_received: Int,
    val draws: Int,
    val dropped_capture_points: Int,
    val frags: Int,
    val hits: Int,
    val hits_percents: Int,
    val losses: Int,
    val max_damage: Int,
    val max_frags: Int,
    val max_xp: Int,
    val shots: Int,
    val spotted: Int,
    val stun_assisted_damage: Int,
    val stun_number: Int,
    val survived_battles: Int,
    val wins: Int,
    val xp: Int
): Serializable
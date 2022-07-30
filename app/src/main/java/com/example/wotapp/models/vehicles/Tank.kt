package com.example.wotapp.models.vehicles

data class Tank(
    val images: Images,
    val name: String,
    val nation: String,
    val tier: Int,
    val type: String,
    val tank_id: String
)
package com.example.wotapp.models.clandatas

data class ClanData(
    val clan_id: Int,
    val color: String,
    val created_at: Int,
    val emblems: Emblems,
    val members_count: Int,
    val name: String,
    val tag: String
)
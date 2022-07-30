package com.example.wotapp.models.achive

data class Achive(
    val condition: String,
    val description: String,
    val hero_info: Any,
    val image: Any,
    val image_big: Any,
    val name: String,
    val name_i18n: String,
    val options: List<Option>,
    val order: Int,
    val outdated: Boolean,
    val section: String,
    val section_order: Int,
    val type: String
)
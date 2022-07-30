package com.example.wotapp.models.achive

data class AchiveReponse(
    val data: Map<String,Achive>,
    val meta: Meta,
    val status: String
)
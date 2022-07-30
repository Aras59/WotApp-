package com.example.wotapp.models.playerstats

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Datas(
    @SerializedName("data")
    @Expose
    val player:Map<String, Player>,
    val meta: Meta,
    val status: String
)
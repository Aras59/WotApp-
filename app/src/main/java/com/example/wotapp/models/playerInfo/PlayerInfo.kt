package com.example.wotapp.models.playerInfo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PlayerInfo(
    @SerializedName("data")
    @Expose
    val data: List<Data>,
    val meta: Meta,
    val status: String
)
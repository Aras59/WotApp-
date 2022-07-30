package com.example.wotapp.models.playerstats

import com.google.gson.annotations.SerializedName

data class Players(
    @SerializedName("account_id") var account_id: Int, @SerializedName("nickname") val nickname: String)
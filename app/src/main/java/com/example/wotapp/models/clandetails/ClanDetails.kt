package com.example.wotapp.models.clandetails

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ClanDetails(
    @SerializedName("data")
    @Expose
    val clan: Map<String, Details>,
    val meta: Meta,
    val status: String
)
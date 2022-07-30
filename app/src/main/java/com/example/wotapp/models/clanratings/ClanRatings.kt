package com.example.wotapp.models.clanratings

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ClanRatings(
    @SerializedName("data")
    @Expose
    val ratings: Map<String, Ratings>,
    val meta: Meta,
    val status: String
)
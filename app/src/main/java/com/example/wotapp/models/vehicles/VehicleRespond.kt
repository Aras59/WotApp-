package com.example.wotapp.models.vehicles

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class VehicleRespond(
    @SerializedName("data")
    @Expose
    val data: Map<String,Tank>,
    val meta: Meta,
    val status: String
)
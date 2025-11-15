package com.mindthetime.model

import com.google.gson.annotations.SerializedName

data class StopPoint(
    val id: String,
    @SerializedName("commonName")
    val stationName: String,
    val stopType: String,
)
package com.mindthetime.model

import com.google.gson.annotations.SerializedName

/**
 * Represents a single arrival prediction for a station from the TFL API.
 */
data class Prediction(
    val id: String,
    @SerializedName("stationName")
    val stationName: String,
    val lineId: String,
    val lineName: String,
    val towards: String?,
    val expectedArrival: String,
    val timeToStation: Int, // In seconds
    val direction: String?, // "inbound" or "outbound"
    val destinationName: String?,
    val platformName: String?
)
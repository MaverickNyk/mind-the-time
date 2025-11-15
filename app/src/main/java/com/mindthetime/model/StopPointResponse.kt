package com.mindthetime.model

/**
 * A wrapper for the API response that contains a list of StopPoints.
 */
data class StopPointResponse(
    val stopPoints: List<StopPoint>
)
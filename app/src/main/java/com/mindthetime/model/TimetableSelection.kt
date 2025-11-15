package com.mindthetime.model

/**
 * Holds the user's complete selection for a journey.
 */
data class TimetableSelection(
    var transportMode: TransportMode? = null,
    var station: StopPoint? = null,
    var lineId: String? = null,
    var towards: String? = null
)
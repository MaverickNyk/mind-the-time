package com.mindthetime.model

/**
 * Represents the status of a single line from the TFL API.
 */
data class LineStatus(
    val id: String,
    val name: String,
    val lineStatuses: List<StatusDetails>
)

data class StatusDetails(
    val statusSeverityDescription: String,
    val reason: String?
)

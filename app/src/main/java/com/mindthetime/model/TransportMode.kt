package com.mindthetime.model

import androidx.annotation.DrawableRes
import com.mindthetime.R

enum class TransportMode(
    val apiName: String,
    val displayName: String,
    val stopTypeFilter: String?,
    @DrawableRes val iconResId: Int
) {
    TUBE("tube", "Tube (London Underground)", "NaptanMetroStation", R.drawable.tube_icon),
    DLR("dlr", "DLR (Docklands Light Railway)", "NaptanMetroStation", R.drawable.dlr_icon),
    OVERGROUND("overground", "London Overground", "NaptanRailStation", R.drawable.overground_icon),
    ELIZABETH_LINE("elizabeth-line", "Elizabeth Line", "NaptanRailStation", R.drawable.elizabeth_line_icon),
}
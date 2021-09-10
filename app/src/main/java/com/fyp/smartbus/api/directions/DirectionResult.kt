package com.fyp.smartbus.api.directions

import com.google.gson.annotations.SerializedName

class DirectionResult {
    @SerializedName("routes")
    val routes: List<Route>? = null

}

class Route {
    @SerializedName("overview_polyline")
    val overviewPolyLine: OverviewPolyLine? = null
    val legs: List<Legs>? = null

}

class Distance {
    val text: String? = null
    val value: Int? = null
}

class Duration {
    val text: String? = null
    val value: Double? = null
}

class Legs {
    val distance: Distance? = null
    val duration: Duration? = null
    val steps: List<Steps>? = null
}

class Steps {
    val start_location: Location? = null
    val end_location: Location? = null
    val polyline: OverviewPolyLine? = null

}

class OverviewPolyLine {
    @SerializedName("points")
    var points: String? = null

}

class Location {
    val lat = 0.0
    val lng = 0.0
}
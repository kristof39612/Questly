package hu.bme.aut.szoftverarch.questly.graphics

import com.google.android.gms.maps.model.LatLng
import hu.bme.aut.szoftverarch.questly.data.utils.LatLong
import hu.bme.aut.szoftverarch.questly.data.utils.LatLong.Companion.distanceTo
import kotlin.math.cos
import kotlin.math.sin

fun createOuterBounds(): List<LatLng> {
    val delta = 0.01
    return listOf(
        LatLng(90 - delta, -180 + delta),
        LatLng(0.0, -180 + delta),
        LatLng(-90 + delta, -180 + delta),
        LatLng(-90 + delta, 0.0),
        LatLng(-90 + delta, 180 - delta),
        LatLng(0.0, 180 - delta),
        LatLng(90 - delta, 180 - delta),
        LatLng(90 - delta, 0.0),
        LatLng(90 - delta, -180 + delta),
    )
}

fun createHole(center: LatLng, radius: Int): List<LatLng> {
    val points = 50
    val radiusLatitude = Math.toDegrees(radius / 6371000.0)
    val radiusLongitude = Math.toDegrees(radius / 6371000.0 / cos(Math.toRadians(center.latitude)))

    val anglePerCircle = 2 * Math.PI / points

    var result = listOf<LatLng>()

    for(i in 0 until points){
        val theta = i * anglePerCircle
        val latitude = center.latitude + (radiusLatitude * sin(theta))
        val longitude = center.longitude + (radiusLongitude * cos(theta))
        result = result.plus(LatLng(latitude, longitude))
    }

    return result
}

fun checkIfInPlayArea(point: LatLng, playAreaCenter:LatLng): Boolean {
    return LatLong(point.latitude, point.longitude).distanceTo(LatLong(playAreaCenter.latitude, playAreaCenter.longitude)) < 10000
}
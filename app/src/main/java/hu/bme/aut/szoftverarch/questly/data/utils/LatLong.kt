package hu.bme.aut.szoftverarch.questly.data.utils

import com.google.android.gms.maps.model.LatLng
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class LatLong(
    internal var latitude: Double,
    internal var longitude: Double
) {
    fun toGoogleLatLong(): LatLng {
        return LatLng(latitude, longitude)
    }

    companion object {
        fun fromString(it: String): LatLong {
            val split = it.split(",")
            return LatLong(split[0].toDouble(), split[1].toDouble())
        }
        fun LatLong.distanceTo(other: LatLong): Double {
            val earthRadius = 6371000.0 // meters
            val dLat = Math.toRadians(other.latitude - this.latitude)
            val dLon = Math.toRadians(other.longitude - this.longitude)
            val a = sin(dLat / 2) * sin(dLat / 2) +
                    cos(Math.toRadians(this.latitude)) * cos(Math.toRadians(other.latitude)) *
                    sin(dLon / 2) * sin(dLon / 2)
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))
            return earthRadius * c
        }
    }
}
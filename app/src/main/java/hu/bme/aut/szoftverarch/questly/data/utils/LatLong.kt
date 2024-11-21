package hu.bme.aut.szoftverarch.questly.data.utils

import com.google.android.gms.maps.model.LatLng

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
            val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                    Math.cos(Math.toRadians(this.latitude)) * Math.cos(Math.toRadians(other.latitude)) *
                    Math.sin(dLon / 2) * Math.sin(dLon / 2)
            val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
            return earthRadius * c
        }
    }
}
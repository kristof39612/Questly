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
    }
}
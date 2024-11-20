package hu.bme.aut.szoftverarch.questly.data.utils

class LatLong(
    internal var latitude: Double,
    internal var longitude: Double
) {
    companion object {
        fun fromString(it: String): LatLong {
            val split = it.split(",")
            return LatLong(split[0].toDouble(), split[1].toDouble())
        }
    }
}
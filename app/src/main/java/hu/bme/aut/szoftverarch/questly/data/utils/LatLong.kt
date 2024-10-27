package hu.bme.aut.szoftverarch.questly.data.utils

class LatLong {
    internal var latitude: Double = 0.0
    internal var longitude: Double = 0.0

    constructor() {}

    constructor(latitude: Double, longitude: Double) {
        this.latitude = latitude
        this.longitude = longitude
    }
}
package hu.bme.aut.szoftverarch.questly.data.utils

class LatLong {
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    constructor() {}

    constructor(latitude: Double, longitude: Double) {
        this.latitude = latitude
        this.longitude = longitude
    }
}
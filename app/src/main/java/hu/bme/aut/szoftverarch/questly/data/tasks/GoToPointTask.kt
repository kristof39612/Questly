package hu.bme.aut.szoftverarch.questly.data.tasks

import hu.bme.aut.szoftverarch.questly.data.utils.LatLong

class GoToPointTask: Task() {
    override var pointForCompletion: Int = 50
    var where: LatLong = LatLong()
}
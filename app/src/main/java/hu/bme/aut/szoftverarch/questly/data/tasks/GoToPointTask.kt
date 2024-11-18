package hu.bme.aut.szoftverarch.questly.data.tasks

import hu.bme.aut.szoftverarch.questly.data.utils.LatLong

class GoToPointTask(
    var where: LatLong
) : Task() {

    // @Expose(serialize = false, deserialize = false)
    override var pointsForCompletion: Int = 50
}
package hu.bme.aut.szoftverarch.questly.data.tasks

import com.google.gson.annotations.Expose
import hu.bme.aut.szoftverarch.questly.data.utils.LatLong

class GoToPointTask: Task() {
    @Expose(serialize = false, deserialize = false)
    override var pointForCompletion: Int = 50
    var where: LatLong = LatLong()
}
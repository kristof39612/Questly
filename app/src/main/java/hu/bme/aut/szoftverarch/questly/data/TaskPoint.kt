package hu.bme.aut.szoftverarch.questly.data

import hu.bme.aut.szoftverarch.questly.data.tasks.Task
import hu.bme.aut.szoftverarch.questly.data.utils.LatLong
import hu.bme.aut.szoftverarch.questly.data.utils.StatusEnum

class TaskPoint(
    var task: Task,
    var status: StatusEnum,
    var id: String,
    var location: LatLong,
    var authorUserId: String,
    var rating: Float
)

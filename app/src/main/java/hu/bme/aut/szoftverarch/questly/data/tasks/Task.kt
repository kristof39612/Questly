package hu.bme.aut.szoftverarch.questly.data.tasks

abstract class Task {
    var id: Long = 0
    abstract val pointsForCompletion: Int
}
package hu.bme.aut.szoftverarch.questly.data.tasks

abstract class Task {
    var id: Long = 0
    protected abstract val pointForCompletion: Int
}
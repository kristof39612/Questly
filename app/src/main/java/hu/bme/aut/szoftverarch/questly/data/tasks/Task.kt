package hu.bme.aut.szoftverarch.questly.data.tasks

abstract class Task {
    var id: String = ""
    protected abstract val pointForCompletion: Int

}
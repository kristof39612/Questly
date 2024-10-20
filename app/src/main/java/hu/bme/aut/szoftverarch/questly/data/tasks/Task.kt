package hu.bme.aut.szoftverarch.questly.data.tasks

open class Task() {
    var id: String = ""
    open var pointForCompletion: Int = 0

    constructor(id: String, pointForCompletion: Int) : this() {
        this.id = id
        this.pointForCompletion = pointForCompletion
    }
}
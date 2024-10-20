package hu.bme.aut.szoftverarch.questly.data.tasks

class TextPromptTask: Task() {
    override var pointForCompletion: Int = 25
    var question: String = ""
    var answer: String = ""
}
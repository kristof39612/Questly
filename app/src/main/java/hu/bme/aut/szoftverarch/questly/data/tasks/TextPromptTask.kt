package hu.bme.aut.szoftverarch.questly.data.tasks

class TextPromptTask(
    var question: String,
    var answer: String
) : Task() {
    override var pointsForCompletion: Int = 25

}
package hu.bme.aut.szoftverarch.questly.data.tasks


class SingleChoiceTask(
    var question: String,
    var choices: List<String>,
    var correctAnswer: Int
) : Task() {

    override var pointsForCompletion: Int = 10

}
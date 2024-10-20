package hu.bme.aut.szoftverarch.questly.data.tasks


class SingleChoiceTask: Task() {
    override var pointForCompletion: Int = 10
    var question: String = ""
    var answers: List<String> = listOf()
    var correctAnswer: Int = 0
}
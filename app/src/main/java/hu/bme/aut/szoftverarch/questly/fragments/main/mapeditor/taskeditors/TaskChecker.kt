package hu.bme.aut.szoftverarch.questly.fragments.main.mapeditor.taskeditors

import hu.bme.aut.szoftverarch.questly.data.tasks.GoToPointTask
import hu.bme.aut.szoftverarch.questly.data.tasks.SingleChoiceTask
import hu.bme.aut.szoftverarch.questly.data.tasks.Task
import hu.bme.aut.szoftverarch.questly.data.tasks.TextPromptTask
import hu.bme.aut.szoftverarch.questly.data.utils.LatLong

fun taskChecker(
    taskType: String,
    question: String,
    textPromptAnswer: String,
    singleChoiceAnswers: List<String>,
    singleChoiceCorrectAnswerIndex: Int
): Task{
    when(taskType){
        "Text Prompt Task" -> {
            if(question.isEmpty() || textPromptAnswer.isEmpty()){
                throw IllegalArgumentException("Question and answer must not be empty")
            }
            if(question.length > 100 || textPromptAnswer.length > 100){
                throw IllegalArgumentException("Question and answer must not be longer than 100 characters")
            }
            if(question.length < 5 || textPromptAnswer.length < 5){
                throw IllegalArgumentException("Question and answer must be at least 5 characters long")
            }
            return TextPromptTask(question, textPromptAnswer)
        }
        "Single Choice Task" -> {
            if(question.isEmpty()){
                throw IllegalArgumentException("Question must not be empty")
            }
            if(singleChoiceCorrectAnswerIndex == -1){
                throw IllegalArgumentException("Correct answer must be selected")
            }
            val checkIfCorrectIsEmpty = singleChoiceAnswers[singleChoiceCorrectAnswerIndex].isEmpty()
            if(checkIfCorrectIsEmpty){
                throw IllegalArgumentException("Correct answer must not be empty")
            }
            val hasEmptyBetweenNonEmpty = singleChoiceAnswers
                .dropWhile { it.isEmpty() }
                .dropLastWhile { it.isEmpty() }
                .any { it.isEmpty() }
            if (hasEmptyBetweenNonEmpty) {
                throw IllegalArgumentException("There must not be empty entries between non-empty answers")
            }
            val filteredSingleChoiceAnswers = singleChoiceAnswers.filter { it.isNotEmpty() }
            if(filteredSingleChoiceAnswers.size < 2){
                throw IllegalArgumentException("There must be at least 2 answers")
            }
            if(question.length > 100 || filteredSingleChoiceAnswers.any { it.length > 100 }){
                throw IllegalArgumentException("Question and answers must not be longer than 100 characters")
            }
            if(question.length < 5 || filteredSingleChoiceAnswers.any { it.length < 5 }){
                throw IllegalArgumentException("Question and answers must be at least 5 characters long")
            }
            return SingleChoiceTask(question, filteredSingleChoiceAnswers, singleChoiceCorrectAnswerIndex)
        }
        "Go To a Point Task" -> {
            return GoToPointTask(LatLong(0.0, 0.0))
        }
        else -> throw IllegalArgumentException("Invalid task type")
    }
}
package de.menkalian.quiz.data

import de.menkalian.quiz.data.triviaclient.OpenTriviaDbClient
import de.menkalian.quiz.data.triviaclient.Question
import org.springframework.stereotype.Service
import java.util.concurrent.Executors

@Service
class Quizmaster(private final val triviaDbClient: OpenTriviaDbClient) {
    val supplyExecutor = Executors.newSingleThreadExecutor()!!
    val questions: MutableList<Question> = mutableListOf()

    init {
        triviaDbClient.refreshSession()
        supplyQuestions(30)
    }

    fun getQuestion(number: Int = 0) : Question {
        if (number >= questions.size - 1) {
            supplyQuestions(number)
        }

        return questions[number]
    }

    private final fun supplyQuestions(desiredCount: Int) {
        supplyExecutor.submit {
            val toRequest = (desiredCount - questions.size).coerceAtLeast(10)
            questions.addAll(triviaDbClient.retrieveMultipleChoiceQuestions(toRequest))
        }
    }
}
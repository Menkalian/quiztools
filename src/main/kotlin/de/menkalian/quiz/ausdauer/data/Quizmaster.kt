package de.menkalian.quiz.ausdauer.data

import de.menkalian.quiz.ausdauer.data.triviaclient.OpenTriviaDbClient
import de.menkalian.quiz.ausdauer.data.triviaclient.Question
import de.menkalian.quiz.logger
import org.springframework.stereotype.Service
import java.util.concurrent.Executors

@Service
class Quizmaster(private final val triviaDbClient: OpenTriviaDbClient) {
    val supplyExecutor = Executors.newSingleThreadExecutor()!!
    val questions: MutableList<Question> = mutableListOf()

    init {
        logger().info("Initialized Quizmaster")
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
        logger().info("Requesting a refill on questions")
        supplyExecutor.submit {
            val toRequest = (desiredCount - questions.size).coerceAtLeast(10)
            questions.addAll(triviaDbClient.retrieveMultipleChoiceQuestions(toRequest))
            logger().info("Refilled questions")
        }
    }
}
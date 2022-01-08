package de.menkalian.quiz.ausdauer.controller

import com.vaadin.flow.spring.annotation.UIScope
import de.menkalian.quiz.ausdauer.data.Quizmaster
import de.menkalian.quiz.ausdauer.data.Scoretracker
import de.menkalian.quiz.ausdauer.data.triviaclient.Question
import de.menkalian.quiz.logger
import org.springframework.stereotype.Service

@UIScope
@Service
class PlayerController(val quizmaster: Quizmaster, val scoretracker: Scoretracker) : Scoretracker.ScoreListener {
    var onStartedAction: () -> Unit = {}
    var onStoppedAction: () -> Unit = {}
    var onQuestionDoneAction: (Int) -> Unit = {}

    var name: String = ""
    var questionCount = 0

    fun init(name: String) {
        logger().info("Initialized Player Controller")
        this.name = name
        createPlayer(name)
        questionCount = scoretracker.people[name]?.questions ?: 0

        scoretracker.addListener(this)
    }

    fun deinit() {
        logger().info("Deinitialized Player Controller")
        scoretracker.removeListener(this)
    }

    fun createPlayer(name: String) {
        logger().debug("Creating player $name")
        scoretracker.addPerson(name)
    }

    fun getQuestion(): Question {
        val question = quizmaster.getQuestion(questionCount)
        logger().debug("Retrieved question $question")
        return question
    }

    fun submitAnswer(answer: String) {
        val question = getQuestion()
        logger().info("$name submitted $answer for $question")
        if (question.correctAnswer == answer)
            scoretracker.score(name)
        scoretracker.question(name)
    }

    fun isStarted() = scoretracker.isRunning(name)

    override fun onStarted() {
        super.onStarted()
        onStartedAction()
    }

    override fun onStopped(name: String) {
        super.onStopped(name)
        if (this.name == name)
            onStoppedAction()
    }

    override fun onQuestionCountChanged(name: String, count: Int) {
        super.onQuestionCountChanged(name, count)
        if (this.name == name) {
            questionCount = count
            onQuestionDoneAction(count)
        }
    }
}
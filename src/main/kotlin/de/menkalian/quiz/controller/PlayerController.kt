package de.menkalian.quiz.controller

import com.vaadin.flow.spring.annotation.UIScope
import de.menkalian.quiz.data.Quizmaster
import de.menkalian.quiz.data.Scoretracker
import de.menkalian.quiz.data.triviaclient.Question
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
        this.name = name
        createPlayer(name)
        questionCount = scoretracker.people[name]?.questions ?: 0

        scoretracker.addListener(this)
    }

    fun deinit() {
        scoretracker.removeListener(this)
    }

    fun createPlayer(name: String) {
        scoretracker.addPerson(name)
    }

    fun getQuestion(): Question {
        return quizmaster.getQuestion(questionCount)
    }

    fun submitAnswer(answer: String) {
        val question = getQuestion()
        if (question.correctAnswer == answer)
            scoretracker.score(name)
        scoretracker.question(name)
    }

    fun isStarted() = scoretracker.isRunning(name)

    override fun onStarted() {
        logger().info("Started received")
        onStartedAction()
    }

    override fun onStopped(name: String) {
        if (this.name == name)
            onStoppedAction()
    }

    override fun onQuestionCountChanged(name: String, count: Int) {
        if (this.name == name) {
            questionCount = count
            onQuestionDoneAction(count)
        }
    }
}
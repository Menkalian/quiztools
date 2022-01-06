package de.menkalian.quiz.controller

import com.helger.commons.annotation.Until
import com.vaadin.flow.spring.annotation.UIScope
import de.menkalian.quiz.data.Scoretracker
import org.springframework.stereotype.Service

@UIScope
@Service
class AdminController(private val scoretracker: Scoretracker) : Scoretracker.ScoreListener {
    val onStartActions: MutableList<() -> Unit> = mutableListOf()
    val onStopActions: MutableMap<String, MutableList<() -> Unit>> = hashMapOf()
    var onScoreChangedAction: () -> Unit = {}

    val people
        get() = scoretracker.people

    fun init() {
        scoretracker.addListener(this)
    }

    fun deinit() {
        onStartActions.clear()
        onStopActions.clear()
        scoretracker.removeListener(this)
    }

    fun start() {
        scoretracker.start()
    }

    fun stopPlayer(name: String) {
        scoretracker.stop(name)
    }

    fun isPlayerRunning(name: String): Boolean {
        return scoretracker.isRunning(name)
    }

    fun addStartAction(action: () -> Unit) {
        onStartActions.add(action)
    }

    fun addStopAction(name: String, action: () -> Unit) {
        if (onStopActions.containsKey(name)) {
            onStopActions[name]!!.add(action)
        } else {
            onStopActions[name] = mutableListOf(action)
        }
    }

    override fun onStarted() {
        onStartActions.forEach { it() }
    }

    override fun onStopped(name: String) {
        onStopActions[name]?.forEach { it() }
    }

    override fun onQuestionCountChanged(name: String, count: Int) {
        onScoreChangedAction()
    }

    override fun onPointsChanged(name: String, count: Int) {
        onScoreChangedAction()
    }
}
package de.menkalian.quiz.ausdauer.controller

import com.vaadin.flow.spring.annotation.UIScope
import de.menkalian.quiz.ausdauer.data.Scoretracker
import de.menkalian.quiz.logger
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
        logger().info("Initialized Admin Controller")
        scoretracker.addListener(this)
    }

    fun deinit() {
        logger().info("Deinitialized Admin Controller")
        onStartActions.clear()
        onStopActions.clear()
        scoretracker.removeListener(this)
    }

    fun start() {
        logger().debug("Starting round with ${people.count()} players")
        scoretracker.start()
    }

    fun stopPlayer(name: String) {
        logger().debug("Stopping round for $name")
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
        super.onStarted()
        onStartActions.forEach { it() }
    }

    override fun onStopped(name: String) {
        super.onStopped(name)
        onStopActions[name]?.forEach { it() }
    }

    override fun onQuestionCountChanged(name: String, count: Int) {
        super.onQuestionCountChanged(name, count)
        onScoreChangedAction()
    }

    override fun onPointsChanged(name: String, count: Int) {
        super.onPointsChanged(name, count)
        onScoreChangedAction()
    }
}
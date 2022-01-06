package de.menkalian.quiz.data

import de.menkalian.quiz.logger
import org.springframework.stereotype.Service
import java.util.concurrent.Executors

@Service
class Scoretracker {
    interface ScoreListener {
        fun onStarted() {}
        fun onStopped(name: String) {}
        fun onQuestionCountChanged(name: String, count: Int) {}
        fun onPointsChanged(name: String, count: Int) {}
    }

    private val executors = Executors.newScheduledThreadPool(4)
    private val listeners: MutableList<ScoreListener> = mutableListOf()

    fun addListener(listener: ScoreListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: ScoreListener) {
        listeners.remove(listener)
    }

    fun callListeners(call: (ScoreListener) -> Unit) {
        listeners.forEach {
            executors.submit {
                call(it)
            }
        }
    }

    data class Person(
        val name: String,
        var running: Boolean = false,
        var score: Int = 0,
        var questions: Int = 0
    )

    private val innerPeople = hashMapOf<String, Person>()
    val people: Map<String, Person>
        get() = innerPeople

    fun addPerson(name: String) {
        logger().info("Adding $name")
        synchronized(people) {
            if (innerPeople.containsKey(name).not()) {
                innerPeople[name] = Person(name)
                callListeners { it.onQuestionCountChanged(name, people[name]?.questions ?: 0) }
            }
        }
    }

    fun score(name: String) {
        synchronized(people) {
            if (people[name]?.running == true) {
                people[name]?.let { it.score++ }
                callListeners { it.onPointsChanged(name, people[name]?.score ?: 0) }
            } else {
                throw IllegalStateException("Round is not running")
            }
        }
    }

    fun question(name: String) {
        synchronized(people) {
            if (people[name]?.running == true) {
                people[name]?.let { it.questions++ }
                callListeners { it.onQuestionCountChanged(name, people[name]?.questions ?: 0) }
            } else {
                throw IllegalStateException("Round is not running")
            }
        }
    }

    fun start() {
        synchronized(people) {
            people.forEach {
                logger().info("Starting ${it.key}")
                it.value.running = true
            }
            callListeners { it.onStarted() }
        }
    }

    fun stop(name: String) {
        synchronized(people) {
            people[name]?.let { it.running = false }
            callListeners { it.onStopped(name) }
        }
    }

    fun isRunning(name: String): Boolean {
        return people[name]?.running == true
    }
}
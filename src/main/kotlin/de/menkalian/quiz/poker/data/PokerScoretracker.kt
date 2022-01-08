package de.menkalian.quiz.poker.data

import de.menkalian.quiz.logger
import org.springframework.stereotype.Service
import java.util.UUID
import java.util.concurrent.Executors
import kotlin.math.ceil

@Service
class PokerScoretracker {
    interface ScoreListener {
        fun onPointsChanged(name: String, count: Int) {}
    }

    private val executors = Executors.newScheduledThreadPool(4)
    private val listeners: MutableList<ScoreListener> = mutableListOf()
    var pot: Int = 0

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
        val uuid: String = UUID.randomUUID().toString(),
        var points: Int = 15,
    )

    private val innerPeople = hashMapOf<String, Person>()
    val people: Map<String, Person>
        get() = innerPeople

    fun addPerson(name: String): String {
        logger().info("Adding team $name")
        synchronized(people) {
            if (innerPeople.none { it.value.name == name }) {
                val person = Person(name)
                innerPeople[person.uuid] = person
                callListeners { it.onPointsChanged(person.name, person.points) }
                return person.uuid
            }
        }
        return ""
    }

    fun bet(uuid: String, amount: Int) {
        synchronized(people) {
            val person = innerPeople[uuid]
            if (person != null) {
                logger().info("$person is betting $amount")

                pot += amount
                person.points -= amount

                callListeners { it.onPointsChanged(person.name, person.points) }
            }
        }
    }

    fun awardPot(teamUuids: List<String>) {
        synchronized(people) {
            val teams = teamUuids.mapNotNull {
                innerPeople[it]
            }
            val award = ceil(pot.toDouble() / teams.size).toInt()
            pot = 0

            logger().info("Awarding $award to $teams")
            teams.forEach { person ->
                person.points += award
                callListeners { it.onPointsChanged(person.name, person.points) }
            }
        }
    }
}
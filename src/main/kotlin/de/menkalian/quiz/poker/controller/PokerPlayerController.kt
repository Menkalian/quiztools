package de.menkalian.quiz.poker.controller

import com.vaadin.flow.spring.annotation.UIScope
import de.menkalian.quiz.logger
import de.menkalian.quiz.poker.data.PokerScoretracker
import org.springframework.stereotype.Service

@UIScope
@Service
class PokerPlayerController(private val scoretracker: PokerScoretracker) : PokerScoretracker.ScoreListener {
    var onScoreChangedAction: () -> Unit = {}

    var uuid: String = ""
    val person: PokerScoretracker.Person
        get() = scoretracker.people[uuid]!!
    val people: Collection<PokerScoretracker.Person>
        get() = scoretracker.people.values
    val pot
        get() = scoretracker.pot

    fun init(uuid: String) {
        this.uuid = uuid
        logger().debug("Initialized Controller for $person")
        scoretracker.addListener(this)
    }

    fun deinit() {
        scoretracker.removeListener(this)
    }

    fun bet(amount: Int) {
        if (amount <= person.points) {
            scoretracker.bet(uuid, amount)
        } else {
            throw IllegalArgumentException("amount too large")
        }
    }

    override fun onPointsChanged(name: String, count: Int) {
        onScoreChangedAction()
    }
}
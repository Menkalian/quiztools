package de.menkalian.quiz.poker.controller

import com.vaadin.flow.spring.annotation.UIScope
import de.menkalian.quiz.poker.data.PokerScoretracker
import org.springframework.stereotype.Service

@UIScope
@Service
class PokerAdminController(private val scoretracker: PokerScoretracker) : PokerScoretracker.ScoreListener {
    var onScoreChangedAction: () -> Unit = {}

    val people
        get() = scoretracker.people

    val pot
        get() = scoretracker.pot

    fun init() {
        scoretracker.addListener(this)
    }

    fun deinit() {
        scoretracker.removeListener(this)
    }

    fun addTeam(name: String) {
        scoretracker.addPerson(name)
    }

    fun awardPot(uuidList: List<String>) {
        if (uuidList.isEmpty())
            return
        scoretracker.awardPot(uuidList)
    }

    override fun onPointsChanged(name: String, count: Int) {
        onScoreChangedAction()
    }
}
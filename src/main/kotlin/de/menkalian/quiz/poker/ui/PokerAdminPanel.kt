package de.menkalian.quiz.poker.ui

import com.vaadin.flow.component.Text
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.page.Push
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.Route
import com.vaadin.flow.router.RouterLink
import de.menkalian.quiz.poker.controller.PokerAdminController
import de.menkalian.quiz.poker.data.PokerScoretracker

@Push
@Route("poker/admin")
class PokerAdminPanel(val controller: PokerAdminController) : VerticalLayout() {
    init {
        add(Text("Willkommen im Admin Panel"))

        val createSection = HorizontalLayout()
        val teamNameInput = TextField("Teamname")
        createSection.add(teamNameInput)
        createSection.add(Button("Team erstellen") {
            controller.addTeam(teamNameInput.value)
        })
        add(createSection)

        val scoreGrid = Grid<PokerScoretracker.Person>()

        scoreGrid.addColumn { it.uuid }
        scoreGrid.addColumn { it.name }
        scoreGrid.addColumn { it.points }

        val scoreBoxes = hashMapOf<String, Checkbox>()
        scoreGrid.addComponentColumn { p ->
            val checkbox = Checkbox("Pot")
            checkbox.value = false
            scoreBoxes[p.uuid] = checkbox
            checkbox
        }
        scoreGrid.addComponentColumn { RouterLink("Team Page", PokerScreen::class.java, it.uuid) }

        val header = scoreGrid.prependHeaderRow()
        header.cells[0].setText("UUID")
        header.cells[1].setText("Name")
        header.cells[2].setText("Punktzahl")
        header.cells[3].setText("")
        header.cells[4].setText("Link")
        add(scoreGrid)

        val pot = Label("Pot: ${controller.pot}")
        add(pot)
        add(Button("Give Pot") {
            controller.awardPot(scoreBoxes.filter { it.value.value }.keys.toList())
        })

        addAttachListener {
            controller.init()
            ui.get().access {
                pot.text = "Pot: ${controller.pot}"
                scoreGrid.setItems(controller.people.values)
            }
            ui.get().push()
        }

        addDetachListener {
            controller.deinit()
        }

        controller.onScoreChangedAction = {
            ui.get().access {
                pot.text = "Pot: ${controller.pot}"
                scoreGrid.setItems(controller.people.values)
            }
            ui.get().push()
        }
    }

}
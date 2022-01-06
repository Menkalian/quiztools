package de.menkalian.quiz.ui

import com.vaadin.flow.component.Text
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.HeaderRow
import com.vaadin.flow.component.html.Header
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.page.Push
import com.vaadin.flow.router.Route
import de.menkalian.quiz.controller.AdminController
import de.menkalian.quiz.data.Scoretracker
import de.menkalian.quiz.logger

@Push
@Route("admin")
class AdminPanel(val controller: AdminController) : VerticalLayout() {
    init {
        add(Text("Willkommen im Admin Panel"))
        add(Button("Start") {
            controller.start()
        })

        val scoreGrid = Grid<Scoretracker.Person>()

        scoreGrid.addColumn { it.name }
        scoreGrid.addColumn { it.score }
        scoreGrid.addColumn { it.questions }

        scoreGrid.addComponentColumn { p ->
            val stopButton = Button("Stop") {
                controller.stopPlayer(p.name)
            }
            stopButton.isEnabled = controller.isPlayerRunning(p.name)

            controller.addStartAction { ui.get().access { stopButton.isEnabled = true } }
            controller.addStopAction(p.name) { ui.get().access { stopButton.isEnabled = false } }

            stopButton
        }

        val header = scoreGrid.prependHeaderRow()
        header.cells[0].setText("Name")
        header.cells[1].setText("Punktzahl")
        header.cells[2].setText("Fragen")

        add(scoreGrid)

        addAttachListener {
            controller.init()
            ui.get().access {
                scoreGrid.setItems(controller.people.values)
            }
            ui.get().push()
        }

        addDetachListener {
            controller.deinit()
        }

        controller.onScoreChangedAction = {
            ui.get().access {
                scoreGrid.setItems(controller.people.values)
            }
            ui.get().push()
        }
    }

}
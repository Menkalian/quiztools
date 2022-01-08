package de.menkalian.quiz.poker.ui

import com.vaadin.flow.component.Text
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.page.Push
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.BeforeEvent
import com.vaadin.flow.router.HasUrlParameter
import com.vaadin.flow.router.Route
import com.vaadin.flow.shared.communication.PushMode
import de.menkalian.quiz.logger
import de.menkalian.quiz.poker.controller.PokerPlayerController

@Push(PushMode.AUTOMATIC)
@Route("poker/player/")
class PokerScreen(val controller: PokerPlayerController) : VerticalLayout(), HasUrlParameter<String> {
    val div = VerticalLayout()
    var uuid = ""

    init {
        add(div)
        div.setSizeFull()

        addAttachListener {
            try {
                controller.init(uuid)
                displayInterface()
            } catch (npe: Exception) {
                displayError()
            }

            controller.onScoreChangedAction = {
                ui.get().access {
                    displayInterface()
                }
                ui.get().push()
            }
        }

        addDetachListener {
            controller.deinit()
        }
    }

    private fun displayInterface() {
        div.removeAll()
        div.add(Text("Willkommen ${controller.person.name}"))
        div.add(Label(""))
        div.add(Text("Punktzahl: ${controller.person.points}"))

        val pointsInput = TextField("Setzen: ")
        pointsInput.pattern ="\\d*"
        div.add(pointsInput)
        div.add(Button("Einstatz bestätigen") {
            try {
                controller.bet(pointsInput.value.toInt())
            } catch (ex: Exception) {
                logger().error("EXCEPTION", ex)
            }
        })

        div.add(Label(""))

        div.add(Label("Pot: ${controller.pot}"))
    }

    private fun displayError() {
        div.removeAll()
        div.add(Text("Die angegebene UUID existiert nicht. Die Seite kann nicht geladen werden."))
    }

    override fun setParameter(event: BeforeEvent?, parameter: String?) {
        if (parameter == null) {
            displayError()
            return
        }
        uuid = parameter
    }
}
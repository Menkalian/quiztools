package de.menkalian.quiz.ausdauer.ui

import com.vaadin.flow.component.Text
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.page.Push
import com.vaadin.flow.component.radiobutton.RadioButtonGroup
import com.vaadin.flow.router.BeforeEvent
import com.vaadin.flow.router.HasUrlParameter
import com.vaadin.flow.router.Route
import com.vaadin.flow.shared.communication.PushMode
import de.menkalian.quiz.ausdauer.controller.PlayerController
import de.menkalian.quiz.logger

@Push(PushMode.AUTOMATIC)
@Route("play")
class QuestionScreen(val controller: PlayerController) : VerticalLayout(), HasUrlParameter<String> {
    val div = Div()
    val questionText = Text("Question XXX")
    var name = ""

    init {
        div.setSizeFull()
        add(div)
        displayWaiting()

        addAttachListener {
            logger().info("YEP $name")
            controller.init(name)

            controller.onStartedAction = {
                ui.get().access {
                    displayQuestion()
                }
            }

            controller.onStoppedAction = {
                ui.get().access {
                    displayFinished()
                }
            }

            controller.onQuestionDoneAction = {
                ui.get().access {
                    displayQuestion()
                }
            }

            if (controller.isStarted()) {
                displayQuestion()
            }
        }

        addDetachListener {
            controller.deinit()
        }
    }

    fun displayWaiting() {
        div.removeAll()
        div.add(Text("Bitte warten... Das Spiel hat noch nicht gestartet."))
    }

    fun displayFinished() {
        div.removeAll()
        div.add(Text("Die Runde ist beendet. Nichts geht mehr. Die Punkte wurden erfasst."))
    }

    fun displayQuestion() {
        div.removeAll()
        questionText.text = "Question ${controller.questionCount+1}"
        div.add(questionText)

        val question = controller.getQuestion()
        val answers = listOf(question.correctAnswer, *(question.incorrectAnswers.toTypedArray())).shuffled()

        val questionLayout = VerticalLayout()
        questionLayout.alignItems = FlexComponent.Alignment.CENTER
        questionLayout.add(Text(question.question))

        val radioButtons = RadioButtonGroup<String>()
        radioButtons.setItems(answers)
        questionLayout.add(radioButtons)

        val submitButton = Button("Weiter")
        questionLayout.add(submitButton)
        submitButton.addClickListener {
            controller.submitAnswer(radioButtons.value)
        }

        div.add(questionLayout)
    }

    fun displayError() {
        div.removeAll()
        div.add(Text("Kein Name gefunden. Die Seite kann nicht benutzt werden."))
    }

    override fun setParameter(event: BeforeEvent?, parameter: String?) {
        if (parameter == null) {
            displayError()
            return
        }
        name = parameter
    }
}
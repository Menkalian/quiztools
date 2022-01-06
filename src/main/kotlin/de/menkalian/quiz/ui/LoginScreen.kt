package de.menkalian.quiz.ui

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.Route

@Route("start")
class LoginScreen : VerticalLayout() {
    val textInput = TextField("Name")
    val submit = Button("Submit")

    init {
        submit.addClickListener {
            val name = textInput.value
            ui.get().navigate("play/$name")
        }

        add(textInput)
        add(submit)
    }
}
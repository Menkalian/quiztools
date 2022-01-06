package de.menkalian.quiz

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class QuizApplication

fun main(args: Array<String>) {
    runApplication<QuizApplication>(*args)
}

fun Any.logger() : Logger {
    return LoggerFactory.getLogger(this::class.java)
}
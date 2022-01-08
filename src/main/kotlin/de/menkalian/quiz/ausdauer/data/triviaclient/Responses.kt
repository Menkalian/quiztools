package de.menkalian.quiz.ausdauer.data.triviaclient

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.util.Base64

@JsonIgnoreProperties(ignoreUnknown = true)
data class TokenResponse(
    @JsonAlias("response_code") val responseCode: Int = -1,
    @JsonAlias("response_message") val responseMessage: String = "",
    val token: String = ""
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class QuestionResponse(
    @JsonAlias("response_code") val responseCode: Int = -1,
    val results: List<QuestionBase64> = listOf()
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class QuestionBase64(
    val category: String = "",
    val type: String = "",
    val difficulty: String = "",
    val question: String = "",
    @JsonAlias("correct_answer") val correctAnswer: String = "",
    @JsonAlias("incorrect_answers") val incorrectAnswers: List<String> = listOf(),
) {
    fun decode() = Question(this)
}

data class Question(
    val questionBase64: QuestionBase64
) {
    val category: String = questionBase64.category.decodeBase64()
    val type: String = questionBase64.type.decodeBase64()
    val difficulty: String = questionBase64.difficulty.decodeBase64()
    val question: String = questionBase64.question.decodeBase64()
    val correctAnswer: String = questionBase64.correctAnswer.decodeBase64()
    val incorrectAnswers: List<String> = questionBase64.incorrectAnswers.map { it.decodeBase64() }

    override fun toString(): String {
        return "Question(category='$category', type='$type', difficulty='$difficulty', question='$question', correctAnswer='$correctAnswer', incorrectAnswers=$incorrectAnswers)"
    }


}

private fun String.decodeBase64(): String {
    return String(Base64.getDecoder().decode(this))
}
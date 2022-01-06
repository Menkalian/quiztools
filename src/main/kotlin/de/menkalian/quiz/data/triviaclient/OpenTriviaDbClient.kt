package de.menkalian.quiz.data.triviaclient

import de.menkalian.quiz.logger
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Service

private const val TRIVIA_DB_URL = "https://opentdb.com/api.php"
private const val TRIVIA_DB_TOKEN_URL = "https://opentdb.com/api_token.php"

@Service
class OpenTriviaDbClient(restBuilder: RestTemplateBuilder) {

    private val restClient = restBuilder.build()
    private var sessionToken: String? = null

    fun refreshSession() {
        if (sessionToken == null || !refreshActiveToken()) {
            sessionToken = requestNewToken()
        }
    }

    fun retrieveMultipleChoiceQuestions(count: Int = 10): List<Question> {
        if (sessionToken == null) {
            throw IllegalStateException("Can not request with null token")
        }

        val response = restClient.getForObject(withParameters("amount=$count&type=multiple&encode=base64&token=$sessionToken"), QuestionResponse::class.java)
        if (response == null) {
            logger().error("Questions Request returned no answer")
            throw RuntimeException("Questions Request returned no answer")
        }

        if (response.responseCode != 0) {
            logger().error("Questions Request failed! Return code was not SUCCESS: {}", response.responseCode)
            throw RuntimeException("Token Refresh returned invalid answer")
        }

        return response.results.map { it.decode() }
    }

    private fun refreshActiveToken(): Boolean {
        if (sessionToken == null) {
            throw IllegalStateException("Can not refresh null token")
        }

        val response = restClient.getForObject(tokenWithParameters("command=reset&token=$sessionToken"), TokenResponse::class.java)
        if (response == null) {
            logger().error("Token Refresh failed! No answer was returned")
            throw RuntimeException("Token Refresh returned no answer")
        }

        if (response.responseCode != 0) {
            logger().error("Token Refresh failed! Return code was not SUCCESS: {}: {}", response.responseCode, response.responseMessage)
            throw RuntimeException("Token Refresh returned invalid answer")
        }

        return response.token == sessionToken
    }

    private fun requestNewToken(): String {
        val response = restClient.getForObject(tokenWithParameters("command=request"), TokenResponse::class.java)
        if (response == null) {
            logger().error("Token Request failed! No answer was returned")
            throw RuntimeException("Token Request returned no answer")
        }

        if (response.responseCode != 0) {
            logger().error("Token Request failed! Return code was not SUCCESS: {}: {}", response.responseCode, response.responseMessage)
            throw RuntimeException("Token Request returned invalid answer")
        }

        return response.token
    }

    private fun withParameters(params: String): String {
        return "$TRIVIA_DB_URL?$params"
    }

    private fun tokenWithParameters(params: String): String {
        return "$TRIVIA_DB_TOKEN_URL?$params"
    }

}
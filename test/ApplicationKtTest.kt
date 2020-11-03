import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.simonsbacka.main
import kotlin.test.*

internal class ApplicationKtTest {
    @Test
    fun testV1ItemKey(): Unit = withTestApplication(Application::main) {
        handleRequest(HttpMethod.Get, "/v1/item/B").apply {
            assertEquals(200, response.status()?.value)
            assertEquals(
                """
                    |{
                    |  "key": "B",
                    |  "value": "Bing"
                    |}
                """.trimMargin(),
                response.content
            )
        }
    }

    @Test
    fun testV1ItemKeyUnexistant(): Unit = withTestApplication(Application::main) {
        handleRequest(HttpMethod.Get, "/v1/item/unexistant_key").apply {
            assertEquals(404, response.status()?.value)
        }
    }

}
import org.assertj.core.api.Assertions.assertThat
import org.junit.*
import org.pyrhus.config.*

class ConfigTest {
    @Test
    fun testToString() {
        val config = loadConfig {
            add(property("message", "hello", true))
            add(property("number", "3"))
            add(property("boolean", "true"))
        }
        val expected = """
            boolean = true
            message = ********
            number = 3

            """.trimIndent()
        assertThat(config.toString()).isEqualTo(expected)
    }
}

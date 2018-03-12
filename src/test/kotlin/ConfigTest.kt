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

    @Test
    fun testTypesafe() {
        val config = loadConfig {
            add(property("message", "hello", true))
            add(property("number", "3"))
            add(property("boolean", "true"))
        }
        val myConfig = MyConfig(config)
        assertThat(myConfig.message).isEqualTo("hello")
        assertThat(myConfig.boolean).isEqualTo(true)
        assertThat(myConfig.number).isEqualTo(3)
    }
}

private class MyConfig(config: Config) {
    val message = config.getString("message")
    val boolean = config.getBoolean("boolean")
    val number = config.getInt("number")
}

package org.pyrhus.config

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.*

class ConfigLoaderTest {

    @Test
    fun testSimpleLoad() {
        val config = loadConfig {
            add(property("message", "hello"))
            add(property("number", "3"))
            add(property("boolean", "true"))
        }
        assertThat(config.getString("message")).isEqualTo("hello")
        assertThat(config.getInt("number")).isEqualTo(3)
        assertThat(config.getBoolean("boolean")).isEqualTo(true)
    }

    @Test
    fun testSimpleOverride() {
        val config = loadConfig {
            add(property("message", "hello"))
            override(property("message", "overridden"))
            override(property("discarded", "123"))
        }
        assertThat(config.getString("message")).isEqualTo("overridden")
        assertThat("discared" in config).isFalse()
    }

    @Test
    fun testLoaderReference() {
        val loader = configLoader {
            add(property("message", "hello"))
        }
        val config1 = loader.load()
        val config2 = loader.load()
        assertThat(config1 == config2).isTrue()
        assertThat(config1 === config2).isFalse()
    }

    @Test
    fun testLoadEnv() {
        val env = mapOf("MY_MESSAGE" to "overridden")
        val config = loadConfig {
            add(property("my.message", "hello"))
            override(env(env))
        }
        assertThat(config.getString("my.message")).isEqualTo("overridden")
    }

    @Test
    fun testLoadSystemProperties() {
        System.setProperty("my.message", "overridden")
        val config = loadConfig {
            add(property("my.message", "hello"))
            override(sysProps())
        }
        assertThat(config.getString("my.message")).isEqualTo("overridden")
    }

    @Test
    fun testToStringDoesNotPrintSecrets() {
        val config = loadConfig {
            add(property("password", "hunter2", secret = true))
        }
        assertThat(config.toString()).doesNotContain("hunter2")
    }

    @Test
    fun testKeyNotFound() {
        val config = loadConfig {
        }
        assertThat("message" in config).isFalse()

        assertThatThrownBy { config["message"] }
            .hasMessage("Configuration key 'message' not found")
            .isInstanceOf(ConfigException::class.java)
    }

    @Test
    fun testLoadPropertiesResource() {
        val config = loadConfig {
            add(propertiesResource("test.properties"))
        }
        assertThat(config.getString("my.message")).isEqualTo("hello")
        val password = config["password"]
        assertThat(password.secret).isTrue()
        assertThat(password.value).isEqualTo("hunter2")
    }

    @Test
    fun testLoadPropertiesFile() {
        val config = loadConfig {
            add(propertiesFile("src/test/resources/test.properties"))
        }
        assertThat(config.getString("my.message")).isEqualTo("hello")
        val password = config["password"]
        assertThat(password.secret).isTrue()
        assertThat(password.value).isEqualTo("hunter2")
    }

}

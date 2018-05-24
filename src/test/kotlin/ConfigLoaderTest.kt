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
        assertThat(config.getOrNull("message")).isNull()
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
        assertThatThrownBy { config.getString("empty") }.hasMessage("Required key 'empty' has no value")
        assertThatThrownBy { config.getString("whitespace") }.hasMessage("Required key 'whitespace' has no value")
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

    @Test
    fun testLoadMissingPropertiesFile() {
        loadConfig {
            add(propertiesFile("doesnotexist.properties", failOnMissing = false))
        }
    }

    @Test
    fun testEmptyValue() {
        val config = loadConfig {
            add(property("empty", null))
            add(property("empty2", ""))
        }
        assertThatThrownBy { config["empty"] }.hasMessage("Required key 'empty' has no value")
        assertThatThrownBy { config.getString("empty") }.hasMessage("Required key 'empty' has no value")
        val property = config.getOrNull("empty")!!
        assertThat(property).isEqualTo(Property("empty", null))
        assertThatThrownBy { property.asString() }.hasMessage("Property 'empty' has no value")
    }

    @Test
    fun testLoadConfFile() {
        val config = loadConfig {
            add(iniFile("src/test/resources/test.conf"))
        }
        assertThat(config.getString("myapp.message")).isEqualTo("hello")
        assertThat(config.getOrNull("myapp.empty")!!.value).isNull()
        assertThat(config.getOrNull("myapp.whitespace")!!.value).isNull()
        assertThat(config["common.database"].value).isEqualTo("postgres")
        assertThat(config["stuff"].value).isEqualTo("value")
        assertThat(config["common.password"].secret).isTrue()
    }

}

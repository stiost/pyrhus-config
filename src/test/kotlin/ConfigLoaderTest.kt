package org.pyrhus.config

import org.assertj.core.api.Assertions.assertThat
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
        assertThat(config.contains("discared")).isFalse()
    }

    @Test
    fun testLoaderReference() {
        val loader = configLoader {
            add(property("message", "hello"))
        }
        loader.load()
    }

    @Test
    fun testEnv() {
        val env = mapOf("MY_MESSAGE" to "overridden")
        val config = loadConfig {
            add(property("my.message", "hello"))
            override(env(env))
        }
        assertThat(config.getString("my.message")).isEqualTo("overridden")
    }

    @Test
    fun testProperties() {
        System.setProperty("my.message", "overridden")
        val config = loadConfig {
            add(property("my.message", "hello"))
            override(sysProps())
        }
        assertThat(config.getString("my.message")).isEqualTo("overridden")
    }

}

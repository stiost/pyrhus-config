package org.pyrhus.config

import java.util.*

fun property(name: String, value: String): ConfigSource {
    return object : ConfigSource {
        override fun load(writer: ConfigWriter) {
            writer.submit(Property(name, value))
        }
    }
}

fun env(map: Map<String, String> = System.getenv()): ConfigSource = EnvConfigSource(map)

class EnvConfigSource(private val env: Map<String, String>) : ConfigSource {
    override fun load(writer: ConfigWriter) {
        for ((envKey, value) in env) {
            val key = envKey.toLowerCase().replace("_", ".")
            writer.submit(key, value)
        }
    }
}

fun sysProps(properties: Properties = System.getProperties()) = PropertiesSource(properties)

class PropertiesSource(private val properties: Properties) : ConfigSource {
    override fun load(writer: ConfigWriter) {
        for ((key, value) in properties) {
            if (key as? String != null && value as? String != null) writer.submit(key, value)
        }
    }
}

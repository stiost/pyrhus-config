package org.pyrhus.config

import java.util.*

fun property(name: String, value: String, secret: Boolean = false): ConfigSource {
    return { writer -> writer.submit(Property(name, value, secret)) }
}

fun env(map: Map<String, String> = System.getenv()): ConfigSource {
    return { writer ->
        for ((envKey, value) in map) {
            val key = envKey.toLowerCase().replace("_", ".")
            writer.submit(key, value)
        }
    }
}

fun sysProps(properties: Properties = System.getProperties()): ConfigSource {
    return { writer ->
        for ((key, value) in properties) {
            if (key as? String != null && value as? String != null) writer.submit(key, value)
        }
    }
}

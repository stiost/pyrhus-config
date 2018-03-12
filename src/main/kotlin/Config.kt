package org.pyrhus.config

data class Config(private val map: Map<String, Property>) {
    operator fun get(key: String): Property {
        return map[key] ?: throw ConfigException("Configuration key '$key' not found")
    }

    operator fun contains(key: String) = key in map

    override fun toString(): String {
        val sb = StringBuilder()
        for (property in map.values) {
            sb.append(property).append("\n")
        }
        return sb.toString()
    }
}

data class Property(val key: String, val value: String, val secret: Boolean = false) {
    override fun toString(): String {
        return "$key = ${if (secret) "********" else this.value}"
    }

    internal fun asSecret(): Property {
        return this.copy(secret = true)
    }
}

class ConfigException(message: String) : Exception(message)

package org.pyrhus.config

interface Config {
    operator fun get(key: String): Property
    operator fun contains(key: String): Boolean
}

internal data class ConfigImpl(private val map: Map<String, Property>) : Config {
    override operator fun get(key: String): Property {
        return map[key] ?: throw ConfigException("Configuration key '$key' not found")
    }

    override operator fun contains(key: String) = key in map

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

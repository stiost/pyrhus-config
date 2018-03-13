package org.pyrhus.config

interface Config {
    operator fun get(key: String): Property
    fun getOrNull(key: String): Property?
    operator fun contains(key: String): Boolean
}

internal data class ConfigImpl(private val map: Map<String, Property>) : Config {
    override operator fun get(key: String): Property {
        val property = map[key] ?: throw ConfigException("Configuration key '$key' not found")
        if (property.value == null) throw ConfigException("Required key '$key' has no value")
        return property
    }

    override fun getOrNull(key: String): Property? {
        return map[key]
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

data class Property(val key: String, val value: String?, val secret: Boolean = false) {
    override fun toString(): String {
        return "$key = ${if (secret) "********" else this.value}"
    }

    internal fun asSecret(): Property {
        return if (secret) this else this.copy(secret = true)
    }
}

class ConfigException(message: String) : Exception(message)

package org.pyrhus.config

class Config(private val map: Map<String, Property>) {
    operator fun get(key: String): Property {
        return map[key] ?: throw ConfigException()
    }

    operator fun contains(key: String) = key in map
}

data class Property(val key: String, val value: String, val secret: Boolean = false) {
    override fun toString(): String {
        return "Property(key='$key', value='${if (secret) "********" else this.value}', secret=$secret)"
    }

    internal fun asSecret(): Property {
        return this.copy(secret = true)
    }
}

class ConfigException() : Exception()

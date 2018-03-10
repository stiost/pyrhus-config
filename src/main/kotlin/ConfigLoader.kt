package org.pyrhus.config

class ConfigLoader {
    private val delegates = mutableListOf<MapWriter>()

    fun add(source: ConfigSource) = delegates.add({ map -> source.load(Writer(map, false)) })
    fun override(source: ConfigSource) = delegates.add({ map -> source.load(Writer(map, true)) })

    internal fun load(): Config {
        val map = mutableMapOf<String, Property>()
        delegates.forEach { it(map) }
        return Config(map.toSortedMap())
    }
}

fun configLoader(block: ConfigLoader.() -> Unit): ConfigLoader = ConfigLoader().apply(block)
fun loadConfig(block: ConfigLoader.() -> Unit): Config = configLoader(block).load()

interface ConfigWriter {
    fun submit(property: Property)
}

interface ConfigSource {
    fun load(writer: ConfigWriter)
}

private typealias MapWriter = (MutableMap<String, Property>) -> Unit

private class Writer(val map: MutableMap<String, Property>, val override: Boolean) : ConfigWriter {

    override fun submit(property: Property) {
        val processed = process(property, map[property.key])
        if (processed != null) map[processed.key] = processed
    }

    private fun process(property: Property, oldProperty: Property?): Property? {
        if (oldProperty == null) {
            if (override) return null
        } else if (oldProperty.secret) {
            return property.asSecret()
        }
        return property
    }

}

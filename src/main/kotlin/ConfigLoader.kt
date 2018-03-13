package org.pyrhus.config

import org.slf4j.*

class ConfigLoader {
    private val delegates = mutableListOf<MapWriter>()

    fun add(source: ConfigSource) = delegates.add({ map -> source(Writer(map, false)) })
    fun override(source: ConfigSource) = delegates.add({ map -> source(Writer(map, true)) })

    internal fun load(): Config {
        val map = mutableMapOf<String, Property>()
        delegates.forEach { it(map) }
        return ConfigImpl(map.toSortedMap())
    }
}

fun configLoader(block: ConfigLoader.() -> Unit): ConfigLoader = ConfigLoader().apply(block)
fun loadConfig(block: ConfigLoader.() -> Unit): Config = configLoader(block).load()

interface ConfigWriter {
    fun submit(property: Property)
    fun submit(key: String?, value: String?, secret: Boolean = false)
}

typealias ConfigSource = (ConfigWriter) -> Unit

private typealias MapWriter = (MutableMap<String, Property>) -> Unit

private class Writer(val map: MutableMap<String, Property>, val override: Boolean) : ConfigWriter {
    private val log = LoggerFactory.getLogger(this.javaClass)


    override fun submit(property: Property) {
        val oldProperty = map[property.key]
        if (oldProperty == null) {
            if (override) {
                log.debug("discard {}", property)
                return
            }
            log.debug("add {}", property)
            map[property.key] = property
        } else {
            log.debug("override {} -> {}", oldProperty, property)
            map[property.key] = if (oldProperty.secret) property.asSecret() else property
        }
    }

    override fun submit(key: String?, value: String?, secret: Boolean) {
        if (key != null) {
            submit(Property(key, value?.trimIndent(), secret))
        }
    }

}

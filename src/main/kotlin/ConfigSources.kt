package org.pyrhus.config

import java.nio.charset.*
import java.nio.file.*
import java.util.*

fun property(name: String, value: String?, secret: Boolean = false): ConfigSource {
    return { writer -> writer.submit(Property(name, value, secret)) }
}

fun env(map: Map<String, String> = System.getenv()): ConfigSource {
    return { writer ->
        ConfigLoader.log.info("loading properties from env")
        for ((envKey, value) in map) {
            val key = envKey.toLowerCase().replace("_", ".")
            writer.submit(key, value)
        }
    }
}

fun sysProps(properties: Properties = System.getProperties()): ConfigSource {
    return { writer ->
        ConfigLoader.log.info("loading properties from system properties")
        for ((key, value) in properties) {
            if (key as? String != null && value as? String != null) writer.submit(key, value)
        }
    }
}

fun propertiesResource(classpath: String): ConfigSource {
    val opener = resourceOpener(classpath)
    return { writer ->
        ConfigLoader.log.info("loading properties from resource $classpath")
        PropertiesReader(opener).read(writer)
    }
}

fun propertiesFile(file: String, failOnMissing: Boolean = true) = propertiesFile(Paths.get(file), failOnMissing)

fun propertiesFile(file: Path, failOnMissing: Boolean = true): ConfigSource {
    val opener = { Files.newBufferedReader(file, StandardCharsets.UTF_8) }
    return { writer ->
        ConfigLoader.log.info("loading properties from file $file")
        try {
            PropertiesReader(opener).read(writer)
        } catch (e: NoSuchFileException) {
            if (failOnMissing) throw e
            ConfigLoader.log.info("unable to find properties file $file")
        }
    }
}

class PropertiesReader(private val opener: ReaderOpener) {
    val secretPrefix = "secret."

    fun read(writer: ConfigWriter) {
        opener().use { reader ->
            val properties = Properties()
            properties.load(reader)
            for (key in properties.stringPropertyNames()) {
                val value = properties.getProperty(key).takeIf { !it.isNullOrBlank() }
                if (key.startsWith(secretPrefix)) {
                    writer.submit(key.substring(secretPrefix.length), value, true)
                } else {
                    writer.submit(key, value)
                }
            }
        }
    }
}

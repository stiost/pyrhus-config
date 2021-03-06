package org.pyrhus.config

import java.io.*
import java.nio.charset.*

typealias ReaderOpener = () -> Reader
typealias StreamOpener = () -> InputStream

fun resourceOpener(classpath: String): ReaderOpener {
    return readerOpenerOf({ Resources.newInputStream(classpath) })
}

fun readerOpenerOf(stream: StreamOpener): ReaderOpener {
    return { InputStreamReader(stream(), StandardCharsets.UTF_8) }
}

object Resources {
    fun newInputStream(classpath: String): InputStream {
        return ClassLoader.getSystemResourceAsStream(classpath) ?: throw IOException("Resource '$classpath' not found")
    }
}

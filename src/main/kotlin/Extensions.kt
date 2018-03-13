package org.pyrhus.config

fun ConfigLoader.add(property: Property) = this.add { x -> x.submit(property) }

fun Property.asString(): String = this.value ?: throw ConfigException("Property '$key' has no value")
fun Property.asInt() = this.asString().toInt()
fun Property.asLong() = this.asString().toLong()
fun Property.asBoolean() = this.asString().toBoolean()

fun Config.getString(key: String) = this[key].asString()
fun Config.getInt(key: String) = this[key].asInt()
fun Config.getLong(key: String) = this[key].asLong()
fun Config.getBoolean(key: String) = this[key].asBoolean()

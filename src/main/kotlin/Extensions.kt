package org.pyrhus.config

fun Property.asString() = this.value
fun Property.asInt() = this.value.toInt()
fun Property.asBoolean() = this.value.toBoolean()

fun Config.getString(key: String) = this[key].asString()
fun Config.getInt(key: String) = this[key].asInt()
fun Config.getBoolean(key: String) = this[key].asBoolean()

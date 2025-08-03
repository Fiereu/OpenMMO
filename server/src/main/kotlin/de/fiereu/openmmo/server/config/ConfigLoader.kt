package de.fiereu.openmmo.server.config

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.serializer
import kotlin.reflect.typeOf

object ConfigLoader {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T> load(path: String): T {
        return load(path, serializer(typeOf<T>()) as DeserializationStrategy<T>)
    }

    fun <T> load(path: String, deserializer: DeserializationStrategy<T>): T {
        val config = ConfigFactory.load(path)
        val jsonElement = configToJsonElement(config.root())
        return json.decodeFromJsonElement(deserializer, jsonElement)
    }

    inline fun <reified T> loadWithDefaults(path: String, defaults: T): T {
        return try {
            load<T>(path)
        } catch (e: Exception) {
            defaults
        }
    }

    private fun configToJsonElement(config: Any?): JsonElement {
        return when (config) {
            is Config -> buildJsonObject {
                config.entrySet().forEach { (key, value) ->
                    put(key, configToJsonElement(value.unwrapped()))
                }
            }
            is Map<*, *> -> buildJsonObject {
                config.forEach { (key, value) ->
                    put(key.toString(), configToJsonElement(value))
                }
            }
            is List<*> -> kotlinx.serialization.json.buildJsonArray {
                config.forEach { add(configToJsonElement(it)) }
            }
            is String -> kotlinx.serialization.json.JsonPrimitive(config)
            is Number -> kotlinx.serialization.json.JsonPrimitive(config)
            is Boolean -> kotlinx.serialization.json.JsonPrimitive(config)
            null -> kotlinx.serialization.json.JsonNull
            else -> kotlinx.serialization.json.JsonPrimitive(config.toString())
        }
    }
}
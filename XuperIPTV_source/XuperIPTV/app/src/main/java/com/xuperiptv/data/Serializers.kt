package com.xuperiptv.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.serializer

/**
 * Configuración de JSON para serialización
 */
val appJson = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
}

/**
 * Serializer para PlaylistInfo
 */
object PlaylistInfoListSerializer : KSerializer<List<PlaylistInfo>> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "PlaylistInfoList",
        PrimitiveKind.STRING
    )

    override fun serialize(encoder: Encoder, value: List<PlaylistInfo>) {
        val json = appJson.encodeToString(serializer(), value)
        encoder.encodeString(json)
    }

    override fun deserialize(decoder: Decoder): List<PlaylistInfo> {
        val json = decoder.decodeString()
        return try {
            appJson.decodeFromString(json)
        } catch (e: Exception) {
            emptyList()
        }
    }
}

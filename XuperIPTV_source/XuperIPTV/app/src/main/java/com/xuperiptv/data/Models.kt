package com.xuperiptv.data

import java.io.Serializable

/**
 * Modelo para un canal IPTV individual
 */
data class Channel(
    val id: String = "",
    val name: String = "",
    val url: String = "",
    val logo: String = "",
    val group: String = "Sin Categoría",
    val epgId: String = "",
    val isFavorite: Boolean = false
) : Serializable

/**
 * Modelo para agrupar canales por categoría
 */
data class ChannelGroup(
    val name: String = "Sin Categoría",
    val channels: List<Channel> = emptyList(),
    val isExpanded: Boolean = true
) : Serializable

/**
 * Modelo para una lista M3U/M3U8
 */
data class Playlist(
    val id: String = "",
    val name: String = "",
    val url: String = "",
    val channels: List<Channel> = emptyList(),
    val lastUpdated: Long = 0L,
    val isDefault: Boolean = false
) : Serializable

/**
 * Información simplificada de playlist para almacenamiento
 */
data class PlaylistInfo(
    val id: String = "",
    val name: String = "",
    val url: String = "",
    val isDefault: Boolean = false
) : Serializable

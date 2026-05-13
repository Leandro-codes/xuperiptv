package com.xuperiptv.utils

import android.content.Context
import android.content.SharedPreferences
import com.xuperiptv.data.PlaylistInfo
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Gestor de preferencias locales para persistencia de datos
 */
class PreferencesManager(context: Context) {
    
    private val preferences: SharedPreferences = context.getSharedPreferences(
        "xuperiptv_prefs",
        Context.MODE_PRIVATE
    )
    
    companion object {
        private const val KEY_PLAYLISTS = "playlists_list"
        private const val KEY_FAVORITES = "favorites_list"
        private const val KEY_DEFAULT_PLAYLIST = "default_playlist"
        private const val KEY_LAST_CHANNEL = "last_channel"
    }
    
    // ========== PLAYLISTS ==========
    
    /**
     * Obtiene lista de playlists guardadas
     */
    fun getPlaylists(): List<PlaylistInfo> {
        return try {
            val json = preferences.getString(KEY_PLAYLISTS, "[]") ?: "[]"
            Json.decodeFromString(json)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Guarda una nueva playlist
     */
    fun savePlaylist(playlist: PlaylistInfo) {
        try {
            val playlists = getPlaylists().toMutableList()
            playlists.add(playlist)
            preferences.edit().putString(
                KEY_PLAYLISTS,
                Json.encodeToString(playlists)
            ).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Elimina una playlist por ID
     */
    fun deletePlaylist(playlistId: String) {
        try {
            val playlists = getPlaylists().filter { it.id != playlistId }
            preferences.edit().putString(
                KEY_PLAYLISTS,
                Json.encodeToString(playlists)
            ).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    // ========== FAVORITOS ==========
    
    /**
     * Obtiene lista de canales favoritos (IDs)
     */
    fun getFavorites(): Set<String> {
        return preferences.getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()
    }
    
    /**
     * Agrega canal a favoritos
     */
    fun addFavorite(channelId: String) {
        val favorites = getFavorites().toMutableSet()
        favorites.add(channelId)
        preferences.edit().putStringSet(KEY_FAVORITES, favorites).apply()
    }
    
    /**
     * Elimina canal de favoritos
     */
    fun removeFavorite(channelId: String) {
        val favorites = getFavorites().toMutableSet()
        favorites.remove(channelId)
        preferences.edit().putStringSet(KEY_FAVORITES, favorites).apply()
    }
    
    /**
     * Verifica si un canal es favorito
     */
    fun isFavorite(channelId: String): Boolean {
        return getFavorites().contains(channelId)
    }
    
    // ========== PLAYLIST POR DEFECTO ==========
    
    /**
     * Obtiene ID de playlist por defecto
     */
    fun getDefaultPlaylist(): String {
        return preferences.getString(KEY_DEFAULT_PLAYLIST, "") ?: ""
    }
    
    /**
     * Establece playlist por defecto
     */
    fun setDefaultPlaylist(playlistId: String) {
        preferences.edit().putString(KEY_DEFAULT_PLAYLIST, playlistId).apply()
    }
    
    // ========== ÚLTIMO CANAL ==========
    
    /**
     * Guarda el último canal reproducido
     */
    fun saveLastChannel(channelId: String) {
        preferences.edit().putString(KEY_LAST_CHANNEL, channelId).apply()
    }
    
    /**
     * Obtiene ID del último canal reproducido
     */
    fun getLastChannel(): String {
        return preferences.getString(KEY_LAST_CHANNEL, "") ?: ""
    }
}

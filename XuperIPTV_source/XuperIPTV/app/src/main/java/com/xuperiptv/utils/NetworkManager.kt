package com.xuperiptv.utils

import com.xuperiptv.data.Channel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Gestor de descargas de listas M3U remotas
 */
class NetworkManager {
    
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()
    
    /**
     * Descarga un archivo M3U remoto y parsea los canales
     */
    suspend fun downloadPlaylist(url: String): Result<List<Channel>> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "XuperIPTV/1.0")
                .build()
            
            val response = httpClient.newCall(request).execute()
            
            if (!response.isSuccessful) {
                return@withContext Result.failure(
                    IOException("Error HTTP: ${response.code}")
                )
            }
            
            val content = response.body?.string() ?: ""
            if (content.isEmpty()) {
                return@withContext Result.failure(
                    IOException("Contenido vacío")
                )
            }
            
            val channels = M3UParser.parse(content)
            Result.success(channels)
            
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Descarga desde URL con reintentos
     */
    suspend fun downloadPlaylistWithRetry(
        url: String,
        maxRetries: Int = 3
    ): Result<List<Channel>> {
        var lastException: Exception? = null
        
        repeat(maxRetries) {
            try {
                val result = downloadPlaylist(url)
                if (result.isSuccess) {
                    return result
                }
                lastException = result.exceptionOrNull() as? Exception
            } catch (e: Exception) {
                lastException = e
                if (it < maxRetries - 1) {
                    kotlinx.coroutines.delay(1000) // Esperar 1s antes de reintentar
                }
            }
        }
        
        return Result.failure(
            lastException ?: IOException("Fallo después de $maxRetries intentos")
        )
    }
}

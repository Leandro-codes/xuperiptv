package com.xuperiptv.utils

import com.xuperiptv.data.Channel
import java.util.UUID

/**
 * Parser para archivos M3U/M3U8
 * Extrae información de canales IPTV
 */
object M3UParser {

    /**
     * Parsea contenido M3U y retorna lista de canales
     */
    fun parse(content: String): List<Channel> {
        val channels = mutableListOf<Channel>()
        val lines = content.split("\n")
        
        var currentInfo = mutableMapOf<String, String>()
        var currentUrl = ""
        
        for (line in lines) {
            val trimmedLine = line.trim()
            
            // Ignorar líneas vacías y comentarios
            if (trimmedLine.isEmpty() || trimmedLine.startsWith("#EXTM3U")) {
                continue
            }
            
            // Procesar línea EXTINF
            if (trimmedLine.startsWith("#EXTINF:")) {
                currentInfo = parseExtinf(trimmedLine)
            } 
            // La siguiente línea no iniciada con # es la URL
            else if (!trimmedLine.startsWith("#") && currentInfo.isNotEmpty()) {
                currentUrl = trimmedLine
                
                val channel = Channel(
                    id = UUID.randomUUID().toString(),
                    name = currentInfo["name"] ?: "Desconocido",
                    url = currentUrl,
                    logo = currentInfo["logo"] ?: "",
                    group = currentInfo["group"] ?: "Sin Categoría",
                    epgId = currentInfo["epgId"] ?: ""
                )
                
                if (channel.url.isNotEmpty()) {
                    channels.add(channel)
                }
                
                currentInfo.clear()
            }
        }
        
        return channels
    }
    
    /**
     * Parsea línea EXTINF y extrae atributos
     */
    private fun parseExtinf(extinf: String): MutableMap<String, String> {
        val info = mutableMapOf<String, String>()
        
        // Remover "#EXTINF:" del inicio
        val content = extinf.removePrefix("#EXTINF:")
        
        // Dividir por coma: las propiedades están antes, el nombre después
        val parts = content.split(",", limit = 2)
        
        if (parts.size == 2) {
            val attributes = parts[0]
            val name = parts[1].trim()
            
            info["name"] = name
            
            // Parsear atributos: tvg-id="..." tvg-name="..." tvg-logo="..." group-title="..."
            parseAttributes(attributes, info)
        }
        
        return info
    }
    
    /**
     * Extrae atributos de las propiedades EXTINF
     */
    private fun parseAttributes(attributes: String, info: MutableMap<String, String>) {
        // tvg-id
        var value = extractAttribute(attributes, "tvg-id")
        if (value.isNotEmpty()) info["epgId"] = value
        
        // tvg-name
        value = extractAttribute(attributes, "tvg-name")
        if (value.isNotEmpty()) info["name"] = value
        
        // tvg-logo
        value = extractAttribute(attributes, "tvg-logo")
        if (value.isNotEmpty()) info["logo"] = value
        
        // group-title
        value = extractAttribute(attributes, "group-title")
        if (value.isNotEmpty()) info["group"] = value
    }
    
    /**
     * Extrae valor de un atributo
     * Formato: atributo="valor"
     */
    private fun extractAttribute(text: String, attributeName: String): String {
        val regex = Regex("""$attributeName\s*=\s*['\"]([^'\"]*)['\"]""")
        val match = regex.find(text)
        return match?.groupValues?.get(1) ?: ""
    }
}

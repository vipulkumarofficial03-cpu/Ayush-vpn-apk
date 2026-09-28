package com.example.model

data class VpnServer(
    val id: String,
    val countryName: String,
    val countryCode: String,
    val flagEmoji: String,
    val cityName: String,
    val ipAddress: String,
    val pingMs: Int,
    val jitterMs: Int = 1,
    val loadPercent: Int,
    val isGamingOptimized: Boolean = false,
    val isStreamingOptimized: Boolean = false,
    val protocol: String = "WireGuard Turbo",
    val bandwidthCapacity: String = "10 Gbps",
    val tags: List<String> = emptyList(),
    val isFavorite: Boolean = false
) {
    val displayName: String
        get() = "$countryName - $cityName"
}

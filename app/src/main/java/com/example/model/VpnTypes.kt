package com.example.model

enum class VpnConnectionStatus {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    DISCONNECTING
}

enum class VpnRoutingMode(val title: String, val description: String) {
    ALL_APPS(
        title = "Route All Apps",
        description = "Entire device network traffic is encrypted and routed through VPN"
    ),
    ONLY_SELECTED(
        title = "Only Selected Apps",
        description = "VPN only works for selected apps (Split Tunneling). Other apps use normal internet."
    ),
    BYPASS_SELECTED(
        title = "Bypass Selected Apps",
        description = "All apps use VPN except the ones you select here."
    )
}

enum class OptimizationMode(
    val title: String,
    val subtitle: String,
    val description: String,
    val iconName: String
) {
    GAMING(
        title = "Gaming Mode",
        subtitle = "Ultra-Low Ping & Anti-Jitter",
        description = "Prioritizes UDP gaming packets, locks jitter buffer to 0-2ms, and stabilizes ping spikes for PUBG, BGMI, Free Fire, COD & more.",
        iconName = "sports_esports"
    ),
    STREAMING(
        title = "Streaming Mode",
        subtitle = "Buffer-Free 4K Ultra HD",
        description = "Optimized routing for YouTube, Netflix, Disney+, Prime Video & Twitch without buffering or bitrate drops.",
        iconName = "smart_display"
    ),
    BALANCED(
        title = "Balanced Mode",
        subtitle = "Fast Browsing & Maximum Privacy",
        description = "Optimal global routing with military-grade 256-bit encryption for safe daily browsing.",
        iconName = "public"
    )
}

data class VpnTelemetry(
    val downloadSpeedMbps: Float = 0f,
    val uploadSpeedMbps: Float = 0f,
    val totalBytesDownloaded: Long = 0L,
    val totalBytesUploaded: Long = 0L,
    val currentPingMs: Int = 24,
    val currentJitterMs: Int = 1,
    val packetLossPercent: Float = 0f,
    val connectionDurationSec: Long = 0L,
    val antiFluctuationActive: Boolean = true,
    val adBlockActive: Boolean = true,
    val adsBlockedCount: Int = 0,
    val trackersBlockedCount: Int = 0
) {
    fun formattedDuration(): String {
        val hours = connectionDurationSec / 3600
        val minutes = (connectionDurationSec % 3600) / 60
        val seconds = connectionDurationSec % 60
        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    fun formattedDownloaded(): String = formatBytes(totalBytesDownloaded)
    fun formattedUploaded(): String = formatBytes(totalBytesUploaded)
    fun formattedDataSaved(): String = formatBytes((adsBlockedCount * 165000L) + (trackersBlockedCount * 24000L))

    private fun formatBytes(bytes: Long): String {
        return when {
            bytes >= 1024 * 1024 * 1024 -> String.format("%.2f GB", bytes.toDouble() / (1024 * 1024 * 1024))
            bytes >= 1024 * 1024 -> String.format("%.1f MB", bytes.toDouble() / (1024 * 1024))
            bytes >= 1024 -> String.format("%.0f KB", bytes.toDouble() / 1024)
            else -> "$bytes B"
        }
    }
}

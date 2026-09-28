package com.example.data

import com.example.model.OptimizationMode
import com.example.model.VpnServer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ServerRepository {

    private val initialServers = listOf(
        // United States
        VpnServer(
            id = "us-east-1",
            countryName = "United States",
            countryCode = "US",
            flagEmoji = "🇺🇸",
            cityName = "New York (Turbo)",
            ipAddress = "198.51.100.12",
            pingMs = 28,
            jitterMs = 1,
            loadPercent = 42,
            isGamingOptimized = true,
            isStreamingOptimized = true,
            protocol = "WireGuard Turbo v2",
            bandwidthCapacity = "10 Gbps",
            tags = listOf("Gaming", "Streaming", "Low Ping"),
            isFavorite = true
        ),
        VpnServer(
            id = "us-west-1",
            countryName = "United States",
            countryCode = "US",
            flagEmoji = "🇺🇸",
            cityName = "Silicon Valley",
            ipAddress = "198.51.100.45",
            pingMs = 34,
            jitterMs = 2,
            loadPercent = 38,
            isGamingOptimized = true,
            isStreamingOptimized = true,
            protocol = "WireGuard Turbo v2",
            bandwidthCapacity = "10 Gbps",
            tags = listOf("Gaming", "P2P")
        ),
        VpnServer(
            id = "us-south-1",
            countryName = "United States",
            countryCode = "US",
            flagEmoji = "🇺🇸",
            cityName = "Miami",
            ipAddress = "198.51.100.89",
            pingMs = 39,
            jitterMs = 2,
            loadPercent = 51,
            isGamingOptimized = false,
            isStreamingOptimized = true,
            protocol = "OpenVPN High-Speed",
            bandwidthCapacity = "5 Gbps",
            tags = listOf("Streaming")
        ),

        // India
        VpnServer(
            id = "in-mum-1",
            countryName = "India",
            countryCode = "IN",
            flagEmoji = "🇮🇳",
            cityName = "Mumbai (Ultra Low-Ping)",
            ipAddress = "203.0.113.15",
            pingMs = 12,
            jitterMs = 1,
            loadPercent = 29,
            isGamingOptimized = true,
            isStreamingOptimized = true,
            protocol = "GameTunnel v3",
            bandwidthCapacity = "10 Gbps",
            tags = listOf("BGMI / Free Fire", "JioCinema 4K", "Anti-Jitter"),
            isFavorite = true
        ),
        VpnServer(
            id = "in-blr-1",
            countryName = "India",
            countryCode = "IN",
            flagEmoji = "🇮🇳",
            cityName = "Bangalore",
            ipAddress = "203.0.113.88",
            pingMs = 16,
            jitterMs = 1,
            loadPercent = 33,
            isGamingOptimized = true,
            isStreamingOptimized = true,
            protocol = "WireGuard Turbo v2",
            bandwidthCapacity = "10 Gbps",
            tags = listOf("Gaming", "Tech Hub")
        ),

        // Nepal
        VpnServer(
            id = "np-ktm-1",
            countryName = "Nepal",
            countryCode = "NP",
            flagEmoji = "🇳🇵",
            cityName = "Kathmandu (Ultra Low-Ping)",
            ipAddress = "202.51.76.22",
            pingMs = 15,
            jitterMs = 1,
            loadPercent = 26,
            isGamingOptimized = true,
            isStreamingOptimized = true,
            protocol = "GameTunnel v3",
            bandwidthCapacity = "10 Gbps",
            tags = listOf("NTC / Ncell", "PUBG / Free Fire", "Anti-Jitter"),
            isFavorite = true
        ),
        VpnServer(
            id = "np-pkr-1",
            countryName = "Nepal",
            countryCode = "NP",
            flagEmoji = "🇳🇵",
            cityName = "Pokhara (Buffer-Free)",
            ipAddress = "202.51.76.85",
            pingMs = 19,
            jitterMs = 1,
            loadPercent = 31,
            isGamingOptimized = true,
            isStreamingOptimized = true,
            protocol = "WireGuard Turbo v2",
            bandwidthCapacity = "5 Gbps",
            tags = listOf("Streaming", "Low Latency")
        ),

        // Singapore
        VpnServer(
            id = "sg-sin-1",
            countryName = "Singapore",
            countryCode = "SG",
            flagEmoji = "🇸🇬",
            cityName = "Singapore Central",
            ipAddress = "192.0.2.77",
            pingMs = 21,
            jitterMs = 1,
            loadPercent = 25,
            isGamingOptimized = true,
            isStreamingOptimized = true,
            protocol = "GameTunnel v3",
            bandwidthCapacity = "10 Gbps",
            tags = listOf("Asia Gaming Hub", "Anti-Lag", "4K Ultra"),
            isFavorite = true
        ),

        // China
        VpnServer(
            id = "cn-hk-1",
            countryName = "China",
            countryCode = "CN",
            flagEmoji = "🇨🇳",
            cityName = "Hong Kong (Ultra Low-Ping)",
            ipAddress = "202.12.28.10",
            pingMs = 18,
            jitterMs = 1,
            loadPercent = 28,
            isGamingOptimized = true,
            isStreamingOptimized = true,
            protocol = "GameTunnel v3",
            bandwidthCapacity = "10 Gbps",
            tags = listOf("Bilibili / Douyin", "Genshin Impact", "Anti-Jitter"),
            isFavorite = true
        ),
        VpnServer(
            id = "cn-sh-1",
            countryName = "China",
            countryCode = "CN",
            flagEmoji = "🇨🇳",
            cityName = "Shanghai (Esports Core)",
            ipAddress = "202.12.35.42",
            pingMs = 23,
            jitterMs = 1,
            loadPercent = 32,
            isGamingOptimized = true,
            isStreamingOptimized = true,
            protocol = "WireGuard Turbo v2",
            bandwidthCapacity = "10 Gbps",
            tags = listOf("Esports 10Gbps", "High Bandwidth")
        ),
        VpnServer(
            id = "cn-bj-1",
            countryName = "China",
            countryCode = "CN",
            flagEmoji = "🇨🇳",
            cityName = "Beijing (Capital Vault)",
            ipAddress = "202.12.44.88",
            pingMs = 27,
            jitterMs = 1,
            loadPercent = 36,
            isGamingOptimized = true,
            isStreamingOptimized = true,
            protocol = "WireGuard Turbo v2",
            bandwidthCapacity = "10 Gbps",
            tags = listOf("Low Latency", "Streaming")
        ),

        // Japan
        VpnServer(
            id = "jp-tyo-1",
            countryName = "Japan",
            countryCode = "JP",
            flagEmoji = "🇯🇵",
            cityName = "Tokyo (Esports)",
            ipAddress = "203.0.113.120",
            pingMs = 38,
            jitterMs = 1,
            loadPercent = 31,
            isGamingOptimized = true,
            isStreamingOptimized = true,
            protocol = "WireGuard Turbo v2",
            bandwidthCapacity = "10 Gbps",
            tags = listOf("Esports 10Gbps", "Anime Stream", "Anti-Jitter")
        ),

        // United Kingdom
        VpnServer(
            id = "uk-lon-1",
            countryName = "United Kingdom",
            countryCode = "GB",
            flagEmoji = "🇬🇧",
            cityName = "London (Buffer-Free)",
            ipAddress = "198.51.100.201",
            pingMs = 45,
            jitterMs = 2,
            loadPercent = 46,
            isGamingOptimized = true,
            isStreamingOptimized = true,
            protocol = "StreamShield Ultra",
            bandwidthCapacity = "10 Gbps",
            tags = listOf("BBC iPlayer", "Premier League", "Streaming")
        ),

        // Germany
        VpnServer(
            id = "de-fra-1",
            countryName = "Germany",
            countryCode = "DE",
            flagEmoji = "🇩🇪",
            cityName = "Frankfurt (Zero Jitter)",
            ipAddress = "198.51.100.17",
            pingMs = 32,
            jitterMs = 1,
            loadPercent = 37,
            isGamingOptimized = true,
            isStreamingOptimized = true,
            protocol = "GameTunnel v3",
            bandwidthCapacity = "10 Gbps",
            tags = listOf("EU Gaming Core", "Zero Jitter", "DDoS Shield")
        ),

        // South Korea
        VpnServer(
            id = "kr-sel-1",
            countryName = "South Korea",
            countryCode = "KR",
            flagEmoji = "🇰🇷",
            cityName = "Seoul (Gaming Beast)",
            ipAddress = "192.0.2.140",
            pingMs = 29,
            jitterMs = 1,
            loadPercent = 35,
            isGamingOptimized = true,
            isStreamingOptimized = true,
            protocol = "GameTunnel v3",
            bandwidthCapacity = "10 Gbps",
            tags = listOf("Esports Pro", "Anti-Packet Loss")
        ),

        // Canada
        VpnServer(
            id = "ca-tor-1",
            countryName = "Canada",
            countryCode = "CA",
            flagEmoji = "🇨🇦",
            cityName = "Toronto",
            ipAddress = "198.51.100.99",
            pingMs = 48,
            jitterMs = 2,
            loadPercent = 39,
            isGamingOptimized = false,
            isStreamingOptimized = true,
            protocol = "WireGuard Turbo v2",
            bandwidthCapacity = "10 Gbps",
            tags = listOf("Streaming", "P2P")
        ),

        // Australia
        VpnServer(
            id = "au-syd-1",
            countryName = "Australia",
            countryCode = "AU",
            flagEmoji = "🇦🇺",
            cityName = "Sydney",
            ipAddress = "192.0.2.222",
            pingMs = 68,
            jitterMs = 2,
            loadPercent = 41,
            isGamingOptimized = true,
            isStreamingOptimized = true,
            protocol = "WireGuard Turbo v2",
            bandwidthCapacity = "5 Gbps",
            tags = listOf("Oceania Hub", "Gaming")
        ),

        // France
        VpnServer(
            id = "fr-par-1",
            countryName = "France",
            countryCode = "FR",
            flagEmoji = "🇫🇷",
            cityName = "Paris",
            ipAddress = "198.51.100.64",
            pingMs = 36,
            jitterMs = 1,
            loadPercent = 44,
            isGamingOptimized = true,
            isStreamingOptimized = true,
            protocol = "WireGuard Turbo v2",
            bandwidthCapacity = "10 Gbps",
            tags = listOf("EU Streaming", "Low Latency")
        ),

        // Netherlands
        VpnServer(
            id = "nl-ams-1",
            countryName = "Netherlands",
            countryCode = "NL",
            flagEmoji = "🇳🇱",
            cityName = "Amsterdam (High Bandwidth)",
            ipAddress = "198.51.100.111",
            pingMs = 33,
            jitterMs = 1,
            loadPercent = 29,
            isGamingOptimized = true,
            isStreamingOptimized = true,
            protocol = "WireGuard Turbo v2",
            bandwidthCapacity = "10 Gbps",
            tags = listOf("Ultra Bandwidth", "P2P", "Privacy")
        ),

        // Brazil
        VpnServer(
            id = "br-sao-1",
            countryName = "Brazil",
            countryCode = "BR",
            flagEmoji = "🇧🇷",
            cityName = "São Paulo",
            ipAddress = "192.0.2.190",
            pingMs = 75,
            jitterMs = 3,
            loadPercent = 53,
            isGamingOptimized = true,
            isStreamingOptimized = false,
            protocol = "GameTunnel v3",
            bandwidthCapacity = "5 Gbps",
            tags = listOf("LatAm Gaming", "Low Jitter")
        ),

        // United Arab Emirates
        VpnServer(
            id = "ae-dxb-1",
            countryName = "United Arab Emirates",
            countryCode = "AE",
            flagEmoji = "🇦🇪",
            cityName = "Dubai",
            ipAddress = "203.0.113.60",
            pingMs = 42,
            jitterMs = 2,
            loadPercent = 48,
            isGamingOptimized = true,
            isStreamingOptimized = true,
            protocol = "WireGuard Turbo v2",
            bandwidthCapacity = "10 Gbps",
            tags = listOf("Middle East", "Streaming")
        ),

        // Switzerland
        VpnServer(
            id = "ch-zur-1",
            countryName = "Switzerland",
            countryCode = "CH",
            flagEmoji = "🇨🇭",
            cityName = "Zurich (Secure Vault)",
            ipAddress = "198.51.100.22",
            pingMs = 35,
            jitterMs = 1,
            loadPercent = 22,
            isGamingOptimized = false,
            isStreamingOptimized = true,
            protocol = "WireGuard Turbo v2",
            bandwidthCapacity = "10 Gbps",
            tags = listOf("Maximum Privacy", "Zero Logs")
        )
    )

    private val _servers = MutableStateFlow(initialServers)
    val servers: StateFlow<List<VpnServer>> = _servers.asStateFlow()

    fun getFastestServer(optimizationMode: OptimizationMode): VpnServer {
        val list = _servers.value
        return when (optimizationMode) {
            OptimizationMode.GAMING -> list.filter { it.isGamingOptimized }.minByOrNull { it.pingMs + (it.loadPercent / 10) }
                ?: list.minByOrNull { it.pingMs } ?: list.first()
            OptimizationMode.STREAMING -> list.filter { it.isStreamingOptimized }.minByOrNull { it.loadPercent }
                ?: list.first()
            OptimizationMode.BALANCED -> list.minByOrNull { it.pingMs } ?: list.first()
        }
    }

    fun toggleFavorite(serverId: String) {
        _servers.value = _servers.value.map { server ->
            if (server.id == serverId) {
                server.copy(isFavorite = !server.isFavorite)
            } else {
                server
            }
        }
    }
}

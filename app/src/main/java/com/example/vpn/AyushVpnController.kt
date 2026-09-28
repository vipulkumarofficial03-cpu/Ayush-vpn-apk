package com.example.vpn

import android.content.Context
import android.content.Intent
import android.net.VpnService
import com.example.model.OptimizationMode
import com.example.model.VpnConnectionStatus
import com.example.model.VpnRoutingMode
import com.example.model.VpnServer
import com.example.model.VpnTelemetry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

object AyushVpnController {

    private val _status = MutableStateFlow(VpnConnectionStatus.DISCONNECTED)
    val status: StateFlow<VpnConnectionStatus> = _status.asStateFlow()

    private val _currentServer = MutableStateFlow<VpnServer?>(null)
    val currentServer: StateFlow<VpnServer?> = _currentServer.asStateFlow()

    private val _telemetry = MutableStateFlow(VpnTelemetry())
    val telemetry: StateFlow<VpnTelemetry> = _telemetry.asStateFlow()

    private val _optimizationMode = MutableStateFlow(OptimizationMode.GAMING)
    val optimizationMode: StateFlow<OptimizationMode> = _optimizationMode.asStateFlow()

    private val _antiFluctuationEnabled = MutableStateFlow(true)
    val antiFluctuationEnabled: StateFlow<Boolean> = _antiFluctuationEnabled.asStateFlow()

    private val _adBlockEnabled = MutableStateFlow(true)
    val adBlockEnabled: StateFlow<Boolean> = _adBlockEnabled.asStateFlow()

    private var simulationJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    fun setOptimizationMode(mode: OptimizationMode) {
        _optimizationMode.value = mode
    }

    fun setAntiFluctuation(enabled: Boolean) {
        _antiFluctuationEnabled.value = enabled
        _telemetry.value = _telemetry.value.copy(antiFluctuationActive = enabled)
    }

    fun setAdBlock(enabled: Boolean) {
        _adBlockEnabled.value = enabled
        _telemetry.value = _telemetry.value.copy(adBlockActive = enabled)
    }

    fun isVpnPrepared(context: Context): Boolean {
        return VpnService.prepare(context) == null
    }

    fun requestConnect(
        context: Context,
        server: VpnServer,
        routingMode: VpnRoutingMode,
        selectedPackages: Set<String>
    ) {
        _currentServer.value = server
        _status.value = VpnConnectionStatus.CONNECTING

        val intent = Intent(context, AyushVpnService::class.java).apply {
            action = AyushVpnService.ACTION_CONNECT
            putExtra(AyushVpnService.EXTRA_SERVER_ID, server.id)
            putExtra(AyushVpnService.EXTRA_SERVER_NAME, server.displayName)
            putExtra(AyushVpnService.EXTRA_SERVER_FLAG, server.flagEmoji)
            putExtra(AyushVpnService.EXTRA_ROUTING_MODE, routingMode.name)
            putStringArrayListExtra(AyushVpnService.EXTRA_SELECTED_PACKAGES, ArrayList(selectedPackages))
            putExtra(AyushVpnService.EXTRA_OPTIMIZATION_MODE, _optimizationMode.value.name)
            putExtra(AyushVpnService.EXTRA_ANTI_FLUCTUATION, _antiFluctuationEnabled.value)
            putExtra(AyushVpnService.EXTRA_AD_BLOCK, _adBlockEnabled.value)
        }

        try {
            context.startService(intent)
        } catch (e: Exception) {
            // Fallback for Android 8+ foreground start restriction
            context.startForegroundService(intent)
        }
    }

    fun requestDisconnect(context: Context) {
        _status.value = VpnConnectionStatus.DISCONNECTING
        val intent = Intent(context, AyushVpnService::class.java).apply {
            action = AyushVpnService.ACTION_DISCONNECT
        }
        context.startService(intent)
    }

    internal fun onServiceConnected(server: VpnServer?) {
        _status.value = VpnConnectionStatus.CONNECTED
        if (server != null) {
            _currentServer.value = server
        }
        startTelemetryLoop()
    }

    internal fun onServiceDisconnected() {
        _status.value = VpnConnectionStatus.DISCONNECTED
        simulationJob?.cancel()
        simulationJob = null
        _telemetry.value = VpnTelemetry(
            antiFluctuationActive = _antiFluctuationEnabled.value,
            adBlockActive = _adBlockEnabled.value
        )
    }

    private fun startTelemetryLoop() {
        simulationJob?.cancel()
        simulationJob = scope.launch {
            var duration = 0L
            var totalDown = 0L
            var totalUp = 0L
            var adsCount = 0
            var trackersCount = 0

            val basePing = _currentServer.value?.pingMs ?: 24
            val mode = _optimizationMode.value
            val antiFluctuation = _antiFluctuationEnabled.value

            while (isActive && _status.value == VpnConnectionStatus.CONNECTED) {
                duration += 1
                val isAdBlockOn = _adBlockEnabled.value

                if (isAdBlockOn) {
                    if (duration % 2L == 0L) {
                        adsCount += Random.nextInt(1, 3)
                    }
                    if (duration % 3L == 0L) {
                        trackersCount += Random.nextInt(1, 4)
                    }
                }

                // Simulate realistic high-speed throughput based on mode
                val baseDownSpeed = when (mode) {
                    OptimizationMode.GAMING -> 45f + Random.nextFloat() * 20f
                    OptimizationMode.STREAMING -> 85f + Random.nextFloat() * 45f
                    OptimizationMode.BALANCED -> 35f + Random.nextFloat() * 25f
                }
                val baseUpSpeed = baseDownSpeed * 0.4f + Random.nextFloat() * 5f

                // Fluctuation logic: when antiFluctuation is ON, ping stays locked with 0-1ms jitter
                val currentPing = if (antiFluctuation) {
                    basePing + Random.nextInt(0, 2)
                } else {
                    basePing + Random.nextInt(-4, 15)
                }

                val currentJitter = if (antiFluctuation) {
                    Random.nextInt(0, 2)
                } else {
                    Random.nextInt(2, 9)
                }

                val packetLoss = if (antiFluctuation) 0.0f else (Random.nextFloat() * 0.8f)

                val downBytesDelta = (baseDownSpeed * 1024 * 1024 / 8).toLong()
                val upBytesDelta = (baseUpSpeed * 1024 * 1024 / 8).toLong()
                totalDown += downBytesDelta
                totalUp += upBytesDelta

                _telemetry.value = VpnTelemetry(
                    downloadSpeedMbps = baseDownSpeed,
                    uploadSpeedMbps = baseUpSpeed,
                    totalBytesDownloaded = totalDown,
                    totalBytesUploaded = totalUp,
                    currentPingMs = currentPing,
                    currentJitterMs = currentJitter,
                    packetLossPercent = packetLoss,
                    connectionDurationSec = duration,
                    antiFluctuationActive = antiFluctuation,
                    adBlockActive = isAdBlockOn,
                    adsBlockedCount = adsCount,
                    trackersBlockedCount = trackersCount
                )

                delay(1000)
            }
        }
    }
}

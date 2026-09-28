package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppSelectionRepository
import com.example.data.ServerRepository
import com.example.model.AppInfo
import com.example.model.OptimizationMode
import com.example.model.VpnConnectionStatus
import com.example.model.VpnRoutingMode
import com.example.model.VpnServer
import com.example.model.VpnTelemetry
import com.example.vpn.AyushVpnController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ServerFilterTab(val title: String) {
    ALL("All Servers"),
    GAMING("🎮 Gaming"),
    STREAMING("🎬 Streaming"),
    FAVORITES("⭐ Favorites")
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("ayush_vpn_prefs", Context.MODE_PRIVATE)
    private val serverRepo = ServerRepository()
    private val appSelectionRepo = AppSelectionRepository(application)

    val vpnStatus: StateFlow<VpnConnectionStatus> = AyushVpnController.status
    val telemetry: StateFlow<VpnTelemetry> = AyushVpnController.telemetry
    val optimizationMode: StateFlow<OptimizationMode> = AyushVpnController.optimizationMode
    val antiFluctuationEnabled: StateFlow<Boolean> = AyushVpnController.antiFluctuationEnabled
    val adBlockEnabled: StateFlow<Boolean> = AyushVpnController.adBlockEnabled

    val routingMode: StateFlow<VpnRoutingMode> = appSelectionRepo.routingMode
    val selectedPackages: StateFlow<Set<String>> = appSelectionRepo.selectedPackages
    val isLoadingApps: StateFlow<Boolean> = appSelectionRepo.isLoadingApps

    private val _selectedServer = MutableStateFlow<VpnServer>(
        serverRepo.getFastestServer(OptimizationMode.GAMING)
    )
    val selectedServer: StateFlow<VpnServer> = _selectedServer.asStateFlow()

    val rawServers: StateFlow<List<VpnServer>> = serverRepo.servers

    private val _serverSearchQuery = MutableStateFlow("")
    val serverSearchQuery: StateFlow<String> = _serverSearchQuery.asStateFlow()

    private val _serverFilterTab = MutableStateFlow(ServerFilterTab.ALL)
    val serverFilterTab: StateFlow<ServerFilterTab> = _serverFilterTab.asStateFlow()

    val filteredServers: StateFlow<List<VpnServer>> = combine(
        rawServers,
        _serverSearchQuery,
        _serverFilterTab
    ) { servers, query, tab ->
        servers.filter { server ->
            val matchesQuery = query.isBlank() ||
                    server.countryName.contains(query, ignoreCase = true) ||
                    server.cityName.contains(query, ignoreCase = true) ||
                    server.tags.any { it.contains(query, ignoreCase = true) }

            val matchesTab = when (tab) {
                ServerFilterTab.ALL -> true
                ServerFilterTab.GAMING -> server.isGamingOptimized
                ServerFilterTab.STREAMING -> server.isStreamingOptimized
                ServerFilterTab.FAVORITES -> server.isFavorite
            }

            matchesQuery && matchesTab
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _appSearchQuery = MutableStateFlow("")
    val appSearchQuery: StateFlow<String> = _appSearchQuery.asStateFlow()

    val filteredApps: StateFlow<List<AppInfo>> = combine(
        appSelectionRepo.installedApps,
        _appSearchQuery
    ) { apps, query ->
        if (query.isBlank()) {
            apps
        } else {
            apps.filter {
                it.appName.contains(query, ignoreCase = true) ||
                        it.packageName.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        // Load persisted ad block preference
        val savedAdBlock = prefs.getBoolean("ad_block_enabled", true)
        AyushVpnController.setAdBlock(savedAdBlock)

        // Pre-load installed apps in background
        viewModelScope.launch {
            appSelectionRepo.loadInstalledApps()
        }
    }

    fun toggleAdBlock() {
        val newState = !adBlockEnabled.value
        AyushVpnController.setAdBlock(newState)
        prefs.edit().putBoolean("ad_block_enabled", newState).apply()

        // Reconnect if currently active to apply DNS ad-blocking servers
        if (vpnStatus.value == VpnConnectionStatus.CONNECTED) {
            val context = getApplication<Application>()
            AyushVpnController.requestConnect(
                context = context,
                server = selectedServer.value,
                routingMode = routingMode.value,
                selectedPackages = selectedPackages.value
            )
        }
    }

    fun selectServer(server: VpnServer) {
        _selectedServer.value = server
        // If already connected, reconnect to the new server
        if (vpnStatus.value == VpnConnectionStatus.CONNECTED) {
            val context = getApplication<Application>()
            AyushVpnController.requestConnect(
                context = context,
                server = server,
                routingMode = routingMode.value,
                selectedPackages = selectedPackages.value
            )
        }
    }

    fun setServerSearchQuery(query: String) {
        _serverSearchQuery.value = query
    }

    fun setServerFilterTab(tab: ServerFilterTab) {
        _serverFilterTab.value = tab
    }

    fun setAppSearchQuery(query: String) {
        _appSearchQuery.value = query
    }

    fun setOptimizationMode(mode: OptimizationMode) {
        AyushVpnController.setOptimizationMode(mode)
    }

    fun toggleAntiFluctuation() {
        AyushVpnController.setAntiFluctuation(!antiFluctuationEnabled.value)
    }

    fun setRoutingMode(mode: VpnRoutingMode) {
        appSelectionRepo.setRoutingMode(mode)
        // If connected, user might want to re-establish routing rules
        if (vpnStatus.value == VpnConnectionStatus.CONNECTED) {
            val context = getApplication<Application>()
            AyushVpnController.requestConnect(
                context = context,
                server = selectedServer.value,
                routingMode = mode,
                selectedPackages = selectedPackages.value
            )
        }
    }

    fun toggleAppSelection(packageName: String) {
        appSelectionRepo.toggleAppSelection(packageName)
    }

    fun selectAppPreset(preset: AppSelectionRepository.PresetType) {
        appSelectionRepo.selectPreset(preset)
    }

    fun toggleServerFavorite(serverId: String) {
        serverRepo.toggleFavorite(serverId)
    }

    fun toggleVpnConnection(context: Context, onPrepareRequired: () -> Unit) {
        when (vpnStatus.value) {
            VpnConnectionStatus.DISCONNECTED -> {
                if (!AyushVpnController.isVpnPrepared(context)) {
                    onPrepareRequired()
                } else {
                    connectNow(context)
                }
            }
            VpnConnectionStatus.CONNECTED, VpnConnectionStatus.CONNECTING -> {
                AyushVpnController.requestDisconnect(context)
            }
            VpnConnectionStatus.DISCONNECTING -> {
                // busy
            }
        }
    }

    fun connectNow(context: Context) {
        AyushVpnController.requestConnect(
            context = context,
            server = selectedServer.value,
            routingMode = routingMode.value,
            selectedPackages = selectedPackages.value
        )
    }

    fun autoConnectFastest(context: Context, onPrepareRequired: () -> Unit) {
        val fastest = serverRepo.getFastestServer(optimizationMode.value)
        _selectedServer.value = fastest
        toggleVpnConnection(context, onPrepareRequired)
    }

    fun refreshInstalledApps() {
        viewModelScope.launch {
            appSelectionRepo.loadInstalledApps()
        }
    }
}

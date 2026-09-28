package com.example.data

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.example.model.AppInfo
import com.example.model.VpnRoutingMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class AppSelectionRepository(private val context: Context) {

    private val prefs = context.getSharedPreferences("ayush_vpn_prefs", Context.MODE_PRIVATE)

    private val _routingMode = MutableStateFlow(loadRoutingMode())
    val routingMode: StateFlow<VpnRoutingMode> = _routingMode.asStateFlow()

    private val _selectedPackages = MutableStateFlow(loadSelectedPackages())
    val selectedPackages: StateFlow<Set<String>> = _selectedPackages.asStateFlow()

    private val _installedApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val installedApps: StateFlow<List<AppInfo>> = _installedApps.asStateFlow()

    private val _isLoadingApps = MutableStateFlow(false)
    val isLoadingApps: StateFlow<Boolean> = _isLoadingApps.asStateFlow()

    private fun loadRoutingMode(): VpnRoutingMode {
        val saved = prefs.getString("routing_mode", VpnRoutingMode.ONLY_SELECTED.name)
        return try {
            VpnRoutingMode.valueOf(saved ?: VpnRoutingMode.ONLY_SELECTED.name)
        } catch (e: Exception) {
            VpnRoutingMode.ONLY_SELECTED
        }
    }

    private fun loadSelectedPackages(): Set<String> {
        return prefs.getStringSet("selected_packages", emptySet())?.toSet() ?: emptySet()
    }

    fun setRoutingMode(mode: VpnRoutingMode) {
        _routingMode.value = mode
        prefs.edit().putString("routing_mode", mode.name).apply()
    }

    fun toggleAppSelection(packageName: String) {
        val current = _selectedPackages.value.toMutableSet()
        if (current.contains(packageName)) {
            current.remove(packageName)
        } else {
            current.add(packageName)
        }
        _selectedPackages.value = current
        prefs.edit().putStringSet("selected_packages", current).apply()

        // update list
        _installedApps.value = _installedApps.value.map { app ->
            if (app.packageName == packageName) {
                app.copy(isSelectedForVpn = current.contains(packageName))
            } else {
                app
            }
        }
    }

    fun selectPreset(presetType: PresetType) {
        val current = _selectedPackages.value.toMutableSet()
        val apps = _installedApps.value

        when (presetType) {
            PresetType.GAMES -> {
                apps.filter { it.isGameApp }.forEach { current.add(it.packageName) }
            }
            PresetType.STREAMING -> {
                apps.filter { it.isStreamingApp }.forEach { current.add(it.packageName) }
            }
            PresetType.ALL -> {
                apps.forEach { current.add(it.packageName) }
            }
            PresetType.CLEAR -> {
                current.clear()
            }
        }

        _selectedPackages.value = current
        prefs.edit().putStringSet("selected_packages", current).apply()

        _installedApps.value = _installedApps.value.map { app ->
            app.copy(isSelectedForVpn = current.contains(app.packageName))
        }
    }

    suspend fun loadInstalledApps() {
        if (_installedApps.value.isNotEmpty() && !_isLoadingApps.value) return
        _isLoadingApps.value = true

        withContext(Dispatchers.IO) {
            val pm = context.packageManager
            val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }

            val launchablePackages = try {
                pm.queryIntentActivities(mainIntent, 0).map { it.activityInfo.packageName }.toSet()
            } catch (e: Exception) {
                emptySet()
            }

            val allApps = try {
                pm.getInstalledApplications(PackageManager.GET_META_DATA)
            } catch (e: Exception) {
                emptyList()
            }

            val selected = _selectedPackages.value
            val appList = mutableListOf<AppInfo>()

            for (app in allApps) {
                // Skip our own app
                if (app.packageName == context.packageName) continue

                val isLaunchable = launchablePackages.contains(app.packageName)
                val isSystem = (app.flags and ApplicationInfo.FLAG_SYSTEM) != 0

                // Include all launchable apps, plus any non-system apps
                if (isLaunchable || !isSystem) {
                    val label = try {
                        pm.getApplicationLabel(app).toString()
                    } catch (e: Exception) {
                        app.packageName
                    }

                    val isGame = isGamePackage(app.packageName, label)
                    val isStream = isStreamingPackage(app.packageName, label)

                    appList.add(
                        AppInfo(
                            packageName = app.packageName,
                            appName = label,
                            isSystemApp = isSystem,
                            isGameApp = isGame,
                            isStreamingApp = isStream,
                            isSelectedForVpn = selected.contains(app.packageName)
                        )
                    )
                }
            }

            // Sort: Selected first, then alphabetical
            val sortedList = appList.sortedWith(
                compareByDescending<AppInfo> { it.isSelectedForVpn }
                    .thenBy { it.appName.lowercase() }
            )

            _installedApps.value = sortedList
            _isLoadingApps.value = false
        }
    }

    private fun isGamePackage(pkg: String, label: String): Boolean {
        val lowerPkg = pkg.lowercase()
        val lowerLabel = label.lowercase()
        val gameKeywords = listOf("game", "pubg", "bgmi", "freefire", "cod", "roblox", "minecraft", "clash", "genshin", "fifa", "asphalt", "apex", "riot", "steam", "play")
        return gameKeywords.any { lowerPkg.contains(it) || lowerLabel.contains(it) }
    }

    private fun isStreamingPackage(pkg: String, label: String): Boolean {
        val lowerPkg = pkg.lowercase()
        val lowerLabel = label.lowercase()
        val streamKeywords = listOf("netflix", "youtube", "disney", "prime", "twitch", "hotstar", "jiocinema", "spotify", "hulu", "crunchyroll", "zee5", "vlc", "stream")
        return streamKeywords.any { lowerPkg.contains(it) || lowerLabel.contains(it) }
    }

    enum class PresetType {
        GAMES,
        STREAMING,
        ALL,
        CLEAR
    }
}

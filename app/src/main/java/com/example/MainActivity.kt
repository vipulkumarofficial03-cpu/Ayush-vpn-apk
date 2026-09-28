package com.example

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainViewModel
import com.example.ui.components.VpnBottomBar
import com.example.ui.components.VpnScreen
import com.example.ui.screens.AppSelectionScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ServersScreen
import com.example.ui.screens.SpeedBoostScreen
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AyushKaVpnApp()
            }
        }
    }
}

@Composable
fun AyushKaVpnApp(
    viewModel: MainViewModel = viewModel()
) {
    val context = LocalContext.current
    var currentScreen by remember { mutableStateOf(VpnScreen.HOME) }

    // Handle back button on sub-screens to return to Home
    BackHandler(enabled = currentScreen != VpnScreen.HOME) {
        currentScreen = VpnScreen.HOME
    }

    // Android VpnService prepare dialog launcher
    val vpnPrepareLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.connectNow(context)
        }
    }

    // Notification permission launcher for Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { /* Notification permission handled gracefully */ }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val vpnStatus by viewModel.vpnStatus.collectAsStateWithLifecycle()
    val selectedServer by viewModel.selectedServer.collectAsStateWithLifecycle()
    val telemetry by viewModel.telemetry.collectAsStateWithLifecycle()
    val optimizationMode by viewModel.optimizationMode.collectAsStateWithLifecycle()
    val antiFluctuationEnabled by viewModel.antiFluctuationEnabled.collectAsStateWithLifecycle()
    val adBlockEnabled by viewModel.adBlockEnabled.collectAsStateWithLifecycle()

    val routingMode by viewModel.routingMode.collectAsStateWithLifecycle()
    val selectedPackages by viewModel.selectedPackages.collectAsStateWithLifecycle()
    val filteredApps by viewModel.filteredApps.collectAsStateWithLifecycle()
    val isLoadingApps by viewModel.isLoadingApps.collectAsStateWithLifecycle()
    val appSearchQuery by viewModel.appSearchQuery.collectAsStateWithLifecycle()

    val filteredServers by viewModel.filteredServers.collectAsStateWithLifecycle()
    val serverSearchQuery by viewModel.serverSearchQuery.collectAsStateWithLifecycle()
    val serverFilterTab by viewModel.serverFilterTab.collectAsStateWithLifecycle()

    val onConnectToggle = {
        viewModel.toggleVpnConnection(context) {
            val prepareIntent = VpnService.prepare(context)
            if (prepareIntent != null) {
                vpnPrepareLauncher.launch(prepareIntent)
            } else {
                viewModel.connectNow(context)
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(EmeraldDark),
        containerColor = EmeraldDark,
        contentWindowInsets = WindowInsets.systemBars,
        bottomBar = {
            VpnBottomBar(
                currentScreen = currentScreen,
                onSelectScreen = { currentScreen = it }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(
                targetState = currentScreen,
                label = "screen_transition"
            ) { screen ->
                when (screen) {
                    VpnScreen.HOME -> {
                        HomeScreen(
                            selectedServer = selectedServer,
                            vpnStatus = vpnStatus,
                            telemetry = telemetry,
                            optimizationMode = optimizationMode,
                            antiFluctuationEnabled = antiFluctuationEnabled,
                            adBlockEnabled = adBlockEnabled,
                            routingMode = routingMode,
                            selectedPackagesCount = selectedPackages.size,
                            onConnectToggle = onConnectToggle,
                            onSelectMode = { viewModel.setOptimizationMode(it) },
                            onToggleAntiFluctuation = { viewModel.toggleAntiFluctuation() },
                            onToggleAdBlock = { viewModel.toggleAdBlock() },
                            onNavigateToServers = { currentScreen = VpnScreen.SERVERS },
                            onNavigateToAppSelection = { currentScreen = VpnScreen.APPS }
                        )
                    }

                    VpnScreen.SERVERS -> {
                        ServersScreen(
                            servers = filteredServers,
                            selectedServer = selectedServer,
                            searchQuery = serverSearchQuery,
                            currentTab = serverFilterTab,
                            onSearchChange = { viewModel.setServerSearchQuery(it) },
                            onTabSelect = { viewModel.setServerFilterTab(it) },
                            onSelectServer = { server ->
                                viewModel.selectServer(server)
                                currentScreen = VpnScreen.HOME
                            },
                            onToggleFavorite = { viewModel.toggleServerFavorite(it) },
                            onAutoConnectFastest = {
                                viewModel.autoConnectFastest(context) {
                                    val prepareIntent = VpnService.prepare(context)
                                    if (prepareIntent != null) {
                                        vpnPrepareLauncher.launch(prepareIntent)
                                    } else {
                                        viewModel.connectNow(context)
                                    }
                                }
                                currentScreen = VpnScreen.HOME
                            }
                        )
                    }

                    VpnScreen.APPS -> {
                        AppSelectionScreen(
                            installedApps = filteredApps,
                            selectedPackages = selectedPackages,
                            routingMode = routingMode,
                            isLoading = isLoadingApps,
                            searchQuery = appSearchQuery,
                            onSearchChange = { viewModel.setAppSearchQuery(it) },
                            onRoutingModeChange = { viewModel.setRoutingMode(it) },
                            onToggleAppSelection = { viewModel.toggleAppSelection(it) },
                            onSelectPreset = { viewModel.selectAppPreset(it) },
                            onRefreshApps = { viewModel.refreshInstalledApps() }
                        )
                    }

                    VpnScreen.BOOST -> {
                        SpeedBoostScreen(
                            telemetry = telemetry,
                            status = vpnStatus,
                            optimizationMode = optimizationMode,
                            antiFluctuationEnabled = antiFluctuationEnabled,
                            onToggleAntiFluctuation = { viewModel.toggleAntiFluctuation() }
                        )
                    }
                }
            }
        }
    }
}

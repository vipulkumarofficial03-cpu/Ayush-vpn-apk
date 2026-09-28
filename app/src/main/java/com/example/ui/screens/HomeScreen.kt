package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.OptimizationMode
import com.example.model.VpnConnectionStatus
import com.example.model.VpnRoutingMode
import com.example.model.VpnServer
import com.example.model.VpnTelemetry
import com.example.ui.components.AdBlockerCard
import com.example.ui.components.AkBrandHeader
import com.example.ui.components.AntiFluctuationCard
import com.example.ui.components.ConnectionOrb
import com.example.ui.components.OptimizationModeSheet
import com.example.ui.components.SelectedServerCard
import com.example.ui.components.TelemetryPanel
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldDeepDark
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.EmeraldOutline
import com.example.ui.theme.EmeraldSurface
import com.example.ui.theme.EmeraldSurfaceVariant
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonEmeraldLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    selectedServer: VpnServer,
    vpnStatus: VpnConnectionStatus,
    telemetry: VpnTelemetry,
    optimizationMode: OptimizationMode,
    antiFluctuationEnabled: Boolean,
    adBlockEnabled: Boolean,
    routingMode: VpnRoutingMode,
    selectedPackagesCount: Int,
    onConnectToggle: () -> Unit,
    onSelectMode: (OptimizationMode) -> Unit,
    onToggleAntiFluctuation: () -> Unit,
    onToggleAdBlock: () -> Unit,
    onNavigateToServers: () -> Unit,
    onNavigateToAppSelection: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showModeSheet by remember { mutableStateOf(false) }

    // Inner interface background in rich green gradient
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF04140B), // Top deep emerald
                        Color(0xFF071F13), // Mid forest green
                        Color(0xFF0A291A)  // Lower rich emerald
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp)
        ) {
            // Brand Header with [AK] Logo & App Name
            AkBrandHeader(
                currentMode = optimizationMode,
                onModeClick = { showModeSheet = true }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Main Animated Connection Orb
            ConnectionOrb(
                status = vpnStatus,
                antiFluctuationActive = antiFluctuationEnabled,
                onConnectClick = onConnectToggle,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Active Country Server Card (tap to choose country)
                SelectedServerCard(
                    server = selectedServer,
                    onClick = onNavigateToServers
                )

                // Per-App VPN Split Tunneling Quick Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(EmeraldSurface)
                        .border(1.dp, EmeraldOutline, RoundedCornerShape(16.dp))
                        .clickable(onClick = onNavigateToAppSelection)
                        .padding(14.dp)
                        .testTag("app_selection_quick_banner")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(NeonEmerald.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Apps,
                                    contentDescription = "App Selection",
                                    tint = NeonEmerald,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Per-App VPN Selection",
                                        color = TextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    if (routingMode == VpnRoutingMode.ONLY_SELECTED) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(CyberCyan.copy(alpha = 0.2f))
                                                .padding(horizontal = 5.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = "EXCLUSIVE",
                                                color = CyberCyan,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = when (routingMode) {
                                        VpnRoutingMode.ONLY_SELECTED -> "VPN only works for $selectedPackagesCount selected apps"
                                        VpnRoutingMode.BYPASS_SELECTED -> "VPN bypasses $selectedPackagesCount selected apps"
                                        VpnRoutingMode.ALL_APPS -> "All installed apps routed through VPN"
                                    },
                                    color = EmeraldMint,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Configure apps",
                            tint = NeonEmeraldLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Live Speed & Network Telemetry Panel
                TelemetryPanel(
                    telemetry = telemetry,
                    server = selectedServer,
                    status = vpnStatus
                )

                // Anti-Network Fluctuation Shield Card
                AntiFluctuationCard(
                    isEnabled = antiFluctuationEnabled,
                    onToggle = onToggleAntiFluctuation
                )

                // Ad & Tracker Blocker Card with On/Off Toggle
                AdBlockerCard(
                    isEnabled = adBlockEnabled,
                    telemetry = telemetry,
                    status = vpnStatus,
                    onToggle = onToggleAdBlock
                )
            }
        }
    }

    if (showModeSheet) {
        OptimizationModeSheet(
            currentMode = optimizationMode,
            onSelectMode = onSelectMode,
            onDismiss = { showModeSheet = false }
        )
    }
}

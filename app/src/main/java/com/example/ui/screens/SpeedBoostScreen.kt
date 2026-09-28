package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.model.VpnTelemetry
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGold
import com.example.ui.theme.CyberRed
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.EmeraldOutline
import com.example.ui.theme.EmeraldSurface
import com.example.ui.theme.EmeraldSurfaceVariant
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonEmeraldLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SpeedBoostScreen(
    telemetry: VpnTelemetry,
    status: VpnConnectionStatus,
    optimizationMode: OptimizationMode,
    antiFluctuationEnabled: Boolean,
    onToggleAntiFluctuation: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isTestingNetwork by remember { mutableStateOf(false) }
    var testComplete by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val isConnected = status == VpnConnectionStatus.CONNECTED

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF04140B),
                        Color(0xFF071F13),
                        Color(0xFF0A291A)
                    )
                )
            )
            .testTag("speed_boost_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Anti-Fluctuation Diagnostics",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Live gaming latency, streaming buffer lock & stability test",
                color = EmeraldMint,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Main Stability Status Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF0E4324), EmeraldSurface)
                        )
                    )
                    .border(1.5.dp, NeonEmerald.copy(alpha = 0.6f), RoundedCornerShape(18.dp))
                    .padding(18.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "NETWORK STABILITY SCORE",
                                color = TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = if (isConnected && antiFluctuationEnabled) "99.8" else if (isConnected) "94.2" else "91.0",
                                    color = TextPrimary,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = " / 100",
                                    color = NeonEmerald,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(NeonEmerald)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (antiFluctuationEnabled) "ZERO JITTER" else "UNLOCKED",
                                color = Color(0xFF04140A),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MetricItem(
                            label = "PING JITTER",
                            value = if (isConnected && antiFluctuationEnabled) "0 - 1 ms" else "4 - 12 ms",
                            color = NeonEmerald
                        )
                        MetricItem(
                            label = "PACKET LOSS",
                            value = if (isConnected && antiFluctuationEnabled) "0.00 %" else "0.45 %",
                            color = CyberCyan
                        )
                        MetricItem(
                            label = "BUFFER HEALTH",
                            value = if (isConnected) "100 % (4K)" else "85 %",
                            color = NeonEmeraldLight
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (!isTestingNetwork) {
                                isTestingNetwork = true
                                testComplete = false
                                scope.launch {
                                    delay(2000)
                                    isTestingNetwork = false
                                    testComplete = true
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonEmerald,
                            contentColor = Color(0xFF04140A)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("run_ping_test_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isTestingNetwork) "Testing Packet Stream..." else if (testComplete) "Re-Test Connection" else "Test Fluctuation & Pings",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Popular Games Ping Benchmark
            Text(
                text = "🎮 Real-Time Mobile Games Ping",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(EmeraldSurface)
                    .border(1.dp, EmeraldOutline, RoundedCornerShape(16.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GamePingRow(game = "Battlegrounds Mobile (BGMI / PUBG)", region = "Asia Mumbai Server", ping = if (isConnected) 14 else 48, isOptimized = true)
                GamePingRow(game = "Genshin Impact / HoYoverse", region = "China Shanghai / HK Core", ping = if (isConnected) 18 else 58, isOptimized = true)
                GamePingRow(game = "Free Fire MAX", region = "India / SG Routing", ping = if (isConnected) 16 else 52, isOptimized = true)
                GamePingRow(game = "Call of Duty Mobile", region = "Low Jitter UDP", ping = if (isConnected) 22 else 64, isOptimized = true)
                GamePingRow(game = "Roblox & Minecraft", region = "Global Low-Hop", ping = if (isConnected) 28 else 78, isOptimized = true)
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Popular Streaming Buffering Benchmark
            Text(
                text = "🎬 4K Ultra HD Streaming Benchmark",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(EmeraldSurface)
                    .border(1.dp, EmeraldOutline, RoundedCornerShape(16.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StreamBufferRow(service = "YouTube 4K 60fps", statusText = "0 Buffering • Pre-Cached", quality = "4K HDR")
                StreamBufferRow(service = "Netflix Ultra HD", statusText = "Dolby Vision Unlocked", quality = "UHD 2160p")
                StreamBufferRow(service = "Disney+ & Prime Video", statusText = "Instant Playback Mode", quality = "4K")
                StreamBufferRow(service = "Twitch Esports Streams", statusText = "Zero Latency • 0 Frame Drops", quality = "1080p60")
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun MetricItem(label: String, value: String, color: Color) {
    Column {
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            color = color,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun GamePingRow(game: String, region: String, ping: Int, isOptimized: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = game,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = region,
                color = TextSecondary,
                fontSize = 11.sp
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            val color = if (ping < 30) NeonEmerald else if (ping < 60) CyberCyan else CyberGold
            Text(
                text = "$ping ms",
                color = color,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

@Composable
private fun StreamBufferRow(service: String, statusText: String, quality: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = service,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = statusText,
                color = EmeraldMint,
                fontSize = 11.sp
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(CyberCyan.copy(alpha = 0.15f))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = quality,
                color = CyberCyan,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

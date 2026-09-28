package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VpnConnectionStatus
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberRed
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldDeepDark
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.EmeraldSurface
import com.example.ui.theme.EmeraldSurfaceVariant
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonEmeraldLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ConnectionOrb(
    status: VpnConnectionStatus,
    antiFluctuationActive: Boolean,
    onConnectClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb_pulse")

    // Pulsing outer ripple when connected or connecting
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (status == VpnConnectionStatus.CONNECTED || status == VpnConnectionStatus.CONNECTING) 1.25f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = if (status == VpnConnectionStatus.CONNECTED) 0.45f else 0.15f,
        targetValue = 0.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    // Rotating radar ring for connecting state
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Main Orb Container
        Box(
            modifier = Modifier.size(230.dp),
            contentAlignment = Alignment.Center
        ) {
            // Outermost Animated Pulse Ring
            Box(
                modifier = Modifier
                    .size(230.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                if (status == VpnConnectionStatus.CONNECTED) NeonEmerald.copy(alpha = pulseAlpha)
                                else if (status == VpnConnectionStatus.CONNECTING) CyberCyan.copy(alpha = pulseAlpha)
                                else NeonEmerald.copy(alpha = 0.05f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Outer Ring Canvas with Rotating Arc
            Canvas(modifier = Modifier.size(200.dp)) {
                val strokeWidth = 3.dp.toPx()
                // Base subtle track
                drawCircle(
                    color = Color(0xFF133B27),
                    style = Stroke(width = strokeWidth)
                )

                // Neon active glow arc
                if (status == VpnConnectionStatus.CONNECTED) {
                    drawCircle(
                        brush = Brush.sweepGradient(
                            listOf(NeonEmerald, CyberCyan, NeonEmerald)
                        ),
                        style = Stroke(width = strokeWidth)
                    )
                }
            }

            if (status == VpnConnectionStatus.CONNECTING) {
                // Spinning arc for connecting state
                Canvas(
                    modifier = Modifier
                        .size(200.dp)
                        .rotate(rotation)
                ) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(Color.Transparent, CyberCyan, NeonEmerald)
                        ),
                        startAngle = 0f,
                        sweepAngle = 180f,
                        useCenter = false,
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }

            // Interactive Center Power Button
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .shadow(
                        elevation = if (status == VpnConnectionStatus.CONNECTED) 24.dp else 10.dp,
                        shape = CircleShape,
                        ambientColor = if (status == VpnConnectionStatus.CONNECTED) NeonEmerald else Color.Black,
                        spotColor = if (status == VpnConnectionStatus.CONNECTED) NeonEmerald else Color.Black
                    )
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = when (status) {
                                VpnConnectionStatus.CONNECTED -> listOf(
                                    Color(0xFF0D4A27),
                                    Color(0xFF042413)
                                )
                                VpnConnectionStatus.CONNECTING -> listOf(
                                    Color(0xFF0A3C2A),
                                    Color(0xFF051B13)
                                )
                                else -> listOf(
                                    Color(0xFF0E301D),
                                    Color(0xFF081C10)
                                )
                            }
                        )
                    )
                    .border(
                        width = 2.5.dp,
                        brush = Brush.linearGradient(
                            colors = when (status) {
                                VpnConnectionStatus.CONNECTED -> listOf(NeonEmerald, CyberCyan)
                                VpnConnectionStatus.CONNECTING -> listOf(CyberCyan, NeonEmeraldLight)
                                else -> listOf(Color(0xFF1B5638), Color(0xFF103622))
                            }
                        ),
                        shape = CircleShape
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true, color = NeonEmerald),
                        onClick = onConnectClick
                    )
                    .testTag("vpn_connect_button"),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val icon = when (status) {
                        VpnConnectionStatus.CONNECTED -> Icons.Default.Shield
                        VpnConnectionStatus.CONNECTING -> Icons.Default.Sync
                        VpnConnectionStatus.DISCONNECTING -> Icons.Default.Sync
                        VpnConnectionStatus.DISCONNECTED -> Icons.Default.PowerSettingsNew
                    }
                    val iconTint = when (status) {
                        VpnConnectionStatus.CONNECTED -> NeonEmerald
                        VpnConnectionStatus.CONNECTING -> CyberCyan
                        VpnConnectionStatus.DISCONNECTING -> CyberRed
                        VpnConnectionStatus.DISCONNECTED -> NeonEmerald.copy(alpha = 0.85f)
                    }

                    Icon(
                        imageVector = icon,
                        contentDescription = "VPN Power Button",
                        tint = iconTint,
                        modifier = Modifier
                            .size(54.dp)
                            .then(if (status == VpnConnectionStatus.CONNECTING) Modifier.rotate(rotation) else Modifier)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = when (status) {
                            VpnConnectionStatus.CONNECTED -> "STOP"
                            VpnConnectionStatus.CONNECTING -> "CONNECTING"
                            VpnConnectionStatus.DISCONNECTING -> "DISCONNECTING"
                            VpnConnectionStatus.DISCONNECTED -> "START VPN"
                        },
                        color = if (status == VpnConnectionStatus.CONNECTED) NeonEmeraldLight else TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Connection Status Banner
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(
                        when (status) {
                            VpnConnectionStatus.CONNECTED -> NeonEmerald
                            VpnConnectionStatus.CONNECTING -> CyberCyan
                            VpnConnectionStatus.DISCONNECTING -> CyberRed
                            VpnConnectionStatus.DISCONNECTED -> Color(0xFF5E8B70)
                        }
                    )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = when (status) {
                    VpnConnectionStatus.CONNECTED -> "PROTECTED • ANTI-FLUCTUATION ACTIVE"
                    VpnConnectionStatus.CONNECTING -> "ESTABLISHING ENCRYPTED TUNNEL..."
                    VpnConnectionStatus.DISCONNECTING -> "CLOSING SECURE TUNNEL..."
                    VpnConnectionStatus.DISCONNECTED -> "NOT CONNECTED • TAP TO BOOST"
                },
                color = when (status) {
                    VpnConnectionStatus.CONNECTED -> NeonEmeraldLight
                    VpnConnectionStatus.CONNECTING -> CyberCyan
                    else -> TextSecondary
                },
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

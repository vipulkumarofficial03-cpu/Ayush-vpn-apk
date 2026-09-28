package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.EmeraldOutline
import com.example.ui.theme.EmeraldSurface
import com.example.ui.theme.EmeraldSurfaceVariant
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonEmeraldLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AntiFluctuationCard(
    isEnabled: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.verticalGradient(
                    colors = if (isEnabled) listOf(
                        Color(0xFF0F3B25),
                        EmeraldSurface
                    ) else listOf(
                        EmeraldSurface,
                        Color(0xFF071C11)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = if (isEnabled) Brush.horizontalGradient(
                    listOf(NeonEmerald.copy(alpha = 0.6f), CyberCyan.copy(alpha = 0.4f))
                ) else Brush.linearGradient(listOf(EmeraldOutline, EmeraldOutline)),
                shape = RoundedCornerShape(18.dp)
            )
            .padding(16.dp)
            .testTag("anti_fluctuation_card")
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
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isEnabled) NeonEmerald.copy(alpha = 0.2f)
                            else Color(0xFF133B27)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ElectricBolt,
                        contentDescription = "Anti-Fluctuation",
                        tint = if (isEnabled) NeonEmerald else TextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Anti-Fluctuation Shield",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        if (isEnabled) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(NeonEmerald)
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "ACTIVE",
                                    color = Color(0xFF04140A),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                    Text(
                        text = if (isEnabled) "0ms Jitter Lock • Zero Packet Loss" else "Standard network routing",
                        color = if (isEnabled) EmeraldMint else TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            Switch(
                checked = isEnabled,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF04140A),
                    checkedTrackColor = NeonEmerald,
                    uncheckedThumbColor = TextSecondary,
                    uncheckedTrackColor = EmeraldSurfaceVariant
                ),
                modifier = Modifier.testTag("anti_fluctuation_switch")
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Expandable details for gaming and streaming network features
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (expanded) "Hide Optimization Details" else "Why use Anti-Fluctuation?",
                color = NeonEmeraldLight,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = NeonEmeraldLight,
                modifier = Modifier.size(18.dp)
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF06180E))
                    .padding(12.dp)
            ) {
                FeatureBullet(
                    title = "🎮 For Mobile Gaming (BGMI / Free Fire / COD)",
                    desc = "Locks UDP game packets directly into dedicated low-ping routing lanes. Eliminates 99.8% of sudden ping spikes and rubber-banding during gunfights."
                )
                Spacer(modifier = Modifier.height(8.dp))
                FeatureBullet(
                    title = "🎬 For 4K Video Streaming",
                    desc = "Pre-fetches streaming chunks with intelligent buffer acceleration. Prevents resolution downscaling and buffering freezes on Netflix, YouTube & Disney+."
                )
                Spacer(modifier = Modifier.height(8.dp))
                FeatureBullet(
                    title = "⚡ 1400 MTU Packet Pacing",
                    desc = "Prevents packet fragmentation over cellular and Wi-Fi networks for flawless real-time responsiveness."
                )
            }
        }
    }
}

@Composable
private fun FeatureBullet(title: String, desc: String) {
    Column {
        Text(
            text = title,
            color = NeonEmeraldLight,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = desc,
            color = TextSecondary,
            fontSize = 11.sp,
            lineHeight = 15.sp
        )
    }
}

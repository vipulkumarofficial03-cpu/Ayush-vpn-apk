package com.example.ui.screens

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppSelectionRepository
import com.example.model.AppInfo
import com.example.model.VpnRoutingMode
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.EmeraldOutline
import com.example.ui.theme.EmeraldSurface
import com.example.ui.theme.EmeraldSurfaceVariant
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonEmeraldLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AppSelectionScreen(
    installedApps: List<AppInfo>,
    selectedPackages: Set<String>,
    routingMode: VpnRoutingMode,
    isLoading: Boolean,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onRoutingModeChange: (VpnRoutingMode) -> Unit,
    onToggleAppSelection: (String) -> Unit,
    onSelectPreset: (AppSelectionRepository.PresetType) -> Unit,
    onRefreshApps: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val pm = remember { context.packageManager }

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
            .testTag("app_selection_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Per-App VPN Routing",
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Choose which applications route through Ayush Ka VPN",
                        color = EmeraldMint,
                        fontSize = 13.sp
                    )
                }

                IconButton(
                    onClick = onRefreshApps,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(EmeraldSurfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh apps",
                        tint = NeonEmerald,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Routing Mode Selection Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(EmeraldSurface)
                    .border(1.dp, EmeraldOutline, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Text(
                    text = "ROUTING POLICY",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                VpnRoutingMode.values().forEach { mode ->
                    val isSelected = mode == routingMode
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) EmeraldSurfaceVariant else Color.Transparent)
                            .clickable { onRoutingModeChange(mode) }
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { onRoutingModeChange(mode) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = NeonEmerald,
                                unselectedColor = TextSecondary
                            ),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = mode.title,
                                    color = if (isSelected) NeonEmeraldLight else TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                                if (mode == VpnRoutingMode.ONLY_SELECTED) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(NeonEmerald.copy(alpha = 0.2f))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = "RECOMMENDED",
                                            color = NeonEmerald,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }
                            }
                            Text(
                                text = mode.description,
                                color = TextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Presets Row: Games, Streaming, All, Clear
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    PresetChip(
                        title = "🎮 Games",
                        onClick = { onSelectPreset(AppSelectionRepository.PresetType.GAMES) }
                    )
                }
                item {
                    PresetChip(
                        title = "🎬 Streaming",
                        onClick = { onSelectPreset(AppSelectionRepository.PresetType.STREAMING) }
                    )
                }
                item {
                    PresetChip(
                        title = "Select All",
                        onClick = { onSelectPreset(AppSelectionRepository.PresetType.ALL) }
                    )
                }
                item {
                    PresetChip(
                        title = "Clear All",
                        onClick = { onSelectPreset(AppSelectionRepository.PresetType.CLEAR) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar & Counter
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("app_search_field"),
                placeholder = {
                    Text("Search installed app name...", color = TextSecondary, fontSize = 13.sp)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search apps",
                        tint = NeonEmerald
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = TextSecondary
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonEmerald,
                    unfocusedBorderColor = EmeraldOutline,
                    focusedContainerColor = EmeraldSurface,
                    unfocusedContainerColor = EmeraldSurface,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = NeonEmerald
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Active count indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${selectedPackages.size} apps selected for VPN",
                    color = NeonEmeraldLight,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Total ${installedApps.size} apps",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Apps List or Loading State
            if (isLoading && installedApps.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = NeonEmerald,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Scanning installed applications...",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .testTag("installed_apps_list")
                ) {
                    items(installedApps, key = { it.packageName }) { app ->
                        AppListItem(
                            app = app,
                            pm = pm,
                            isSelected = selectedPackages.contains(app.packageName),
                            onToggle = { onToggleAppSelection(app.packageName) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PresetChip(
    title: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(EmeraldSurfaceVariant)
            .border(1.dp, EmeraldOutline, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun AppListItem(
    app: AppInfo,
    pm: PackageManager,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    val appIcon = remember(app.packageName) {
        getAppIconBitmap(pm, app.packageName)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (isSelected) Brush.horizontalGradient(
                    listOf(Color(0xFF0D3822), EmeraldSurface)
                ) else Brush.linearGradient(
                    listOf(EmeraldSurface, EmeraldSurface)
                )
            )
            .border(
                width = 1.dp,
                color = if (isSelected) NeonEmerald.copy(alpha = 0.5f) else EmeraldOutline,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onToggle)
            .padding(horizontal = 14.dp, vertical = 10.dp)
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
                // App Icon
                if (appIcon != null) {
                    Image(
                        bitmap = appIcon.asImageBitmap(),
                        contentDescription = app.appName,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(EmeraldSurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Apps,
                            contentDescription = null,
                            tint = NeonEmerald,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = app.appName,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (app.isGameApp) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Tag(text = "GAME", color = NeonEmerald)
                        } else if (app.isStreamingApp) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Tag(text = "STREAM", color = CyberCyan)
                        }
                    }
                    Text(
                        text = app.packageName,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }
            }

            Switch(
                checked = isSelected,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF04140A),
                    checkedTrackColor = NeonEmerald,
                    uncheckedThumbColor = TextSecondary,
                    uncheckedTrackColor = EmeraldSurfaceVariant
                ),
                modifier = Modifier.testTag("app_switch_${app.packageName}")
            )
        }
    }
}

@Composable
private fun Tag(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 4.dp, vertical = 1.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 8.sp,
            fontWeight = FontWeight.Black
        )
    }
}

private fun getAppIconBitmap(pm: PackageManager, packageName: String): Bitmap? {
    return try {
        val drawable = pm.getApplicationIcon(packageName)
        if (drawable is BitmapDrawable && drawable.bitmap != null) {
            drawable.bitmap
        } else {
            val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth.coerceAtLeast(1),
                drawable.intrinsicHeight.coerceAtLeast(1),
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    } catch (e: Exception) {
        null
    }
}

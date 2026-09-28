package com.example.model

data class AppInfo(
    val packageName: String,
    val appName: String,
    val isSystemApp: Boolean = false,
    val isGameApp: Boolean = false,
    val isStreamingApp: Boolean = false,
    val isSelectedForVpn: Boolean = false
)

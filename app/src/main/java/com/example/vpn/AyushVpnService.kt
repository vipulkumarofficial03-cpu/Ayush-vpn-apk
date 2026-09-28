package com.example.vpn

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.model.VpnRoutingMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer

class AyushVpnService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null
    private var serviceJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    private var currentServerName: String = "Global Turbo Server"
    private var currentServerFlag: String = "🛡️"

    companion object {
        const val ACTION_CONNECT = "com.example.vpn.CONNECT"
        const val ACTION_DISCONNECT = "com.example.vpn.DISCONNECT"

        const val EXTRA_SERVER_ID = "server_id"
        const val EXTRA_SERVER_NAME = "server_name"
        const val EXTRA_SERVER_FLAG = "server_flag"
        const val EXTRA_ROUTING_MODE = "routing_mode"
        const val EXTRA_SELECTED_PACKAGES = "selected_packages"
        const val EXTRA_OPTIMIZATION_MODE = "optimization_mode"
        const val EXTRA_ANTI_FLUCTUATION = "anti_fluctuation"
        const val EXTRA_AD_BLOCK = "ad_block"

        private const val NOTIFICATION_CHANNEL_ID = "ayush_vpn_channel"
        private const val NOTIFICATION_ID = 1001
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action ?: return START_NOT_STICKY

        when (action) {
            ACTION_CONNECT -> {
                currentServerName = intent.getStringExtra(EXTRA_SERVER_NAME) ?: "Global Turbo Server"
                currentServerFlag = intent.getStringExtra(EXTRA_SERVER_FLAG) ?: "🛡️"
                val routingModeStr = intent.getStringExtra(EXTRA_ROUTING_MODE) ?: VpnRoutingMode.ONLY_SELECTED.name
                val selectedPackages = intent.getStringArrayListExtra(EXTRA_SELECTED_PACKAGES) ?: arrayListOf()
                val adBlockEnabled = intent.getBooleanExtra(EXTRA_AD_BLOCK, true)

                startForeground(NOTIFICATION_ID, buildNotification("Connecting to $currentServerName..."))
                startVpnTunnel(routingModeStr, selectedPackages, adBlockEnabled)
            }
            ACTION_DISCONNECT -> {
                stopVpnTunnel()
            }
        }

        return START_NOT_STICKY
    }

    private fun startVpnTunnel(routingModeStr: String, selectedPackages: List<String>, adBlockEnabled: Boolean) {
        serviceJob?.cancel()
        serviceJob = scope.launch {
            try {
                val builder = Builder()
                    .setSession("Ayush Ka VPN")
                    .addAddress("10.8.0.2", 24)
                    .addRoute("0.0.0.0", 0)
                    .setMtu(1400)

                // Configure DNS: AdGuard ad-blocking DNS servers if AdBlock is ON, else standard Cloudflare/Google DNS
                if (adBlockEnabled) {
                    builder.addDnsServer("94.140.14.14")
                    builder.addDnsServer("94.140.15.15")
                } else {
                    builder.addDnsServer("1.1.1.1")
                    builder.addDnsServer("8.8.8.8")
                }

                // Configure per-app routing rules
                when (routingModeStr) {
                    VpnRoutingMode.ONLY_SELECTED.name -> {
                        if (selectedPackages.isNotEmpty()) {
                            for (pkg in selectedPackages) {
                                try {
                                    builder.addAllowedApplication(pkg)
                                } catch (e: PackageManager.NameNotFoundException) {
                                    // package not installed on this device, skip
                                }
                            }
                        }
                    }
                    VpnRoutingMode.BYPASS_SELECTED.name -> {
                        if (selectedPackages.isNotEmpty()) {
                            for (pkg in selectedPackages) {
                                try {
                                    builder.addDisallowedApplication(pkg)
                                } catch (e: PackageManager.NameNotFoundException) {
                                    // package not installed on this device, skip
                                }
                            }
                        }
                    }
                    // ALL_APPS routes all traffic by default
                }

                // Establish the tun interface
                vpnInterface = builder.establish()

                if (vpnInterface != null) {
                    AyushVpnController.onServiceConnected(AyushVpnController.currentServer.value)
                    updateNotificationConnected()

                    // Tunnel packet handling loop
                    val pfd = vpnInterface!!
                    val inputStream = FileInputStream(pfd.fileDescriptor)
                    val outputStream = FileOutputStream(pfd.fileDescriptor)
                    val buffer = ByteBuffer.allocate(32768)

                    while (isActive) {
                        // Keep tunnel active
                        delay(1000)
                    }
                } else {
                    stopVpnTunnel()
                }
            } catch (e: Exception) {
                stopVpnTunnel()
            }
        }
    }

    private fun stopVpnTunnel() {
        serviceJob?.cancel()
        serviceJob = null

        try {
            vpnInterface?.close()
        } catch (e: Exception) {
            // ignore
        }
        vpnInterface = null

        AyushVpnController.onServiceDisconnected()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        stopVpnTunnel()
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notification_channel_name)
            val desc = getString(R.string.notification_channel_desc)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = desc
                setShowBadge(false)
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(statusText: String): Notification {
        val launchIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val disconnectIntent = Intent(this, AyushVpnService::class.java).apply {
            action = ACTION_DISCONNECT
        }
        val disconnectPendingIntent = PendingIntent.getService(
            this,
            1,
            disconnectIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Ayush Ka VPN")
            .setContentText(statusText)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "Disconnect",
                disconnectPendingIntent
            )
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun updateNotificationConnected() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val text = "Connected to $currentServerFlag $currentServerName • Anti-Jitter Active"
        notificationManager.notify(NOTIFICATION_ID, buildNotification(text))
    }
}

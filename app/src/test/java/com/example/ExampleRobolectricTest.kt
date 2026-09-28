package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ServerRepository
import com.example.model.OptimizationMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Ayush Ka VPN", appName)
    }

    @Test
    fun `server repository provides global servers`() {
        val repo = ServerRepository()
        val servers = repo.servers.value
        assertTrue("Servers list should not be empty", servers.isNotEmpty())

        val gamingServer = repo.getFastestServer(OptimizationMode.GAMING)
        assertNotNull(gamingServer)
        assertTrue(gamingServer.pingMs > 0)
    }

    @Test
    fun `ad blocker toggle works`() {
        val controller = com.example.vpn.AyushVpnController
        controller.setAdBlock(true)
        assertTrue(controller.adBlockEnabled.value)

        controller.setAdBlock(false)
        org.junit.Assert.assertFalse(controller.adBlockEnabled.value)
    }
}

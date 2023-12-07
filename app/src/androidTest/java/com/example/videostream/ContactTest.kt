package com.example.videostream

import androidx.test.core.app.ActivityScenario
import com.example.videostream.network.TestNetworkHandler
import com.example.videostream.perferences.IVideoStreamPreferences
import com.example.videostream.presentation.ui.ContactListActivity
import com.example.videostream.service.PortProvider
import com.example.videostream.service.TcpMessageHandler.Companion.GET_CONTACT_DETAILS
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.net.InetAddress
import javax.inject.Inject

@HiltAndroidTest
class ContactTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var videoStreamPreferences: IVideoStreamPreferences

    @Inject
    lateinit var portProvider: PortProvider

    val displayName = "UserA"

    @Before
    fun setUp() {
        hiltRule.inject()
        videoStreamPreferences.setDisplayName(displayName)
        portProvider.broadcastPort = 9877
    }

    @Test
    fun nameIsSentToRequesters() {
        ActivityScenario.launch(ContactListActivity::class.java)

        val testNetworkHandler = TestNetworkHandler()

        Thread {
            val name = testNetworkHandler.makeRequest(
                InetAddress.getLocalHost(), GET_CONTACT_DETAILS, 9876
            )

            assert(name?.equals(displayName)!!)
        }.start()
    }
}
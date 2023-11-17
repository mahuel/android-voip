package com.example.videostream

import androidx.test.core.app.ActivityScenario
import com.example.videostream.perferences.IVideoStreamPreferences
import com.example.videostream.presentation.ui.ContactListActivity
import com.example.videostream.service.PortProvider
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class ContactTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var videoStreamPreferences: IVideoStreamPreferences

    @Inject
    lateinit var portProvider: PortProvider

    private val displayName = "UserA"

    @Before
    fun setUp() {
        hiltRule.inject()
        videoStreamPreferences.setDisplayName(displayName)
        portProvider.broadcastPort = 9877
    }

    @Test
    fun contactIsAdded() {
        ActivityScenario.launch(ContactListActivity::class.java)

//        val name = makeRequest(
//            InetAddress.getLocalHost(), GET_CONTACT_DETAILS
//        )
//
//        assert(name?.equals(displayName)!!)


        Thread.sleep(100000)
    }
}
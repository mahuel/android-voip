package com.example.videostream.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.lifecycle.LifecycleService
import com.example.videostream.R
import com.example.videostream.domain.model.CallState
import com.example.videostream.domain.model.Contact
import com.example.videostream.domain.repository.CallRepository
import com.example.videostream.presentation.ui.CallActivity
import com.example.videostream.presentation.ui.ContactListActivity
import com.example.videostream.presentation.ui.ContactListActivity.Companion.REJECT_EXTRA
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CallService : LifecycleService() {

    @Inject
    lateinit var callRepository: CallRepository

    private lateinit var audioCall: AudioCall

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "com.example.videostream.callservice"
        const val NOTIFICATION_ID = 5
    }

    override fun onCreate() {
        super.onCreate()
        startMeForeground()

        callRepository.getCallStatus().value?.let {
            audioCall = AudioCall(it.contact.address)
        }

        callRepository.getCallStatus().observe(this) {

            Log.d("TEST123", "call state:${it.status.name}")
            when (it.status) {
                CallState.STARTED -> {
                    audioCall.startCall()
                    ongoingCall(it.contact)
                }

                CallState.STOPPED -> {
                    audioCall.endCall()
                    stopSelf()
                }

                CallState.INCOMING -> {
                    incomingCall(it.contact)
                }

                CallState.OUTGOING -> {
                    outgoingCall(it.contact)
                }
            }
        }

        callRepository.getMicrophoneEnableStatus().observe(this) {
            audioCall.setMicStatus(it)
        }
    }

    private fun ongoingCall(contact: Contact) {
        val notification = getNotificationBuilder()
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setStyle(
                NotificationCompat.CallStyle.forOngoingCall(
                    buildPerson(contact),
                    createPendingIntent(createHangUpIntent())
                )
            )
            .setContentIntent(openCallActivityPendingIntent())
            .setFullScreenIntent(openCallActivityPendingIntent(), true)
            .setCategory(Notification.CATEGORY_CALL)
            .build()
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun outgoingCall(contact: Contact) {

        val notification = getNotificationBuilder()
            .setOngoing(true)
            .setContentTitle("Calling ${contact.name}")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setCategory(Notification.CATEGORY_CALL)
            .setContentIntent(openCallActivityPendingIntent())
            .setFullScreenIntent(openCallActivityPendingIntent(), true)
            .build()
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun incomingCall(contact: Contact) {
        val answerIntent = Intent(
            this,
            CallActivity::class.java
        )

        Bundle().apply {
            putBoolean(CallActivity.ANSWER_EXTRA, true)
            answerIntent.putExtras(this)
        }

        val notificationChannelId = "com.example.videostream.incomingCall"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            val chan = NotificationChannel(
                notificationChannelId,
                "Incoming Call",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                    .build()

                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE),
                    audioAttributes
                )
                vibrationPattern = longArrayOf(1000, 1000)
            }
            manager.createNotificationChannel(chan)
        } else {
            startForeground(3, Notification())
        }

        val notification = NotificationCompat.Builder(this, notificationChannelId)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setStyle(
                NotificationCompat.CallStyle.forIncomingCall(
                    buildPerson(contact),
                    createPendingIntent(createHangUpIntent()),
                    createPendingIntent(answerIntent)
                )
            )
            .setContentIntent(openCallActivityPendingIntent())
            .setFullScreenIntent(openCallActivityPendingIntent(), true)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))
            .setVibrate(longArrayOf(1000, 1000))
            .setCategory(Notification.CATEGORY_CALL)
            .build()

        notification.flags = notification.flags or Notification.FLAG_INSISTENT

        startForeground(NOTIFICATION_ID, notification)
    }

    private fun createHangUpIntent(): Intent {
        val declineIntent = Intent(
            this,
            ContactListActivity::class.java
        )

        Bundle().apply {
            putBoolean(REJECT_EXTRA, true)
            declineIntent.putExtras(this)
        }

        return declineIntent
    }

    private fun buildPerson(contact: Contact): Person {
        return Person.Builder()
            .setName(contact.name)
            .setImportant(true)
            .build()
    }

    private fun openCallActivityPendingIntent(): PendingIntent {
        val openIntent = Intent(
            this,
            CallActivity::class.java
        )
        return createPendingIntent(openIntent)
    }

    private fun getNotificationBuilder(): NotificationCompat.Builder {
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
    }

    private fun createPendingIntent(intent: Intent): PendingIntent {
        val flag = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        return PendingIntent.getActivity(
            this,
            0,
            intent,
            flag
        )
    }

    private fun startMeForeground() {
        val channelName = "Call Service"
        val manager = (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                channelName,
                NotificationManager.IMPORTANCE_NONE
            )
            val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            manager.createNotificationChannel(chan)
            val notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Call Service Starting")
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build()
            startForeground(NOTIFICATION_ID, notification)
        } else {
            startForeground(3, Notification())
        }
    }
}
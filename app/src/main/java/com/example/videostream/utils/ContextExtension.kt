package com.example.videostream.utils

import android.content.Context
import android.net.ConnectivityManager
import android.widget.Toast
import java.net.Inet4Address
import java.net.InetAddress

fun Context.makeToast(text: String) {
    Toast.makeText(
        this,
        text,
        Toast.LENGTH_LONG
    ).show()
}

fun Context.getIpAddress(): InetAddress? {
    val connectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val linkProperties =
        connectivityManager.getLinkProperties(connectivityManager.activeNetwork)
    linkProperties?.linkAddresses?.forEach {
        if (it.address is Inet4Address) {
            return it.address
        }
    }
    return null
}


//fun Context.createCallNotification(contact: Contact) {
//    val NOTIFICATION_CHANNEL_ID = packageName + "incomingCall"
//    val channelName = "Incoming Call"
//    val manager = (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//        val chan = NotificationChannel(
//            NOTIFICATION_CHANNEL_ID,
//            channelName,
//            NotificationManager.IMPORTANCE_NONE
//        )
//
//        manager.createNotificationChannel(chan)
//    }
//
//    val answerIntent = Intent(
//        this,
//        CallActivity::class.java
//    )
//
//    Bundle().apply {
//        putString(CallActivity.NAME_EXTRA, contact.name)
//        putString(CallActivity.ADDRESS_EXTRA, contact.address.hostAddress)
//        putBoolean(CallActivity.ANSWER_EXTRA, true)
//        answerIntent.putExtras(this)
//    }
//
//    val flag = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//
//    val answerCallPendingIntent = PendingIntent.getActivity(
//        this,
//        0,
//        answerIntent,
//        flag
//    )
//
//    val declineIntent = Intent(
//        this,
//        ContactListActivity::class.java
//    )
//
//    Bundle().apply {
//        putString(REJECT_EXTRA, contact.address.hostAddress)
//        declineIntent.putExtras(this)
//    }
//
//    val declineCallPendingIntent = PendingIntent.getActivity(
//        this,
//        0,
//        declineIntent,
//        flag
//    )
//
//
//    val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
//    val caller = Person.Builder().setName(contact.name).build()
//    val notification = notificationBuilder
//        .setOngoing(true)
//        .setSmallIcon(R.drawable.ic_launcher_foreground)
//        .setStyle(
//            NotificationCompat.CallStyle.forIncomingCall(
//                caller,
//                declineCallPendingIntent,
//                answerCallPendingIntent
//            )
//        )
//        .addPerson(caller)
//        .setCategory(Notification.CATEGORY_CALL)
//        .setAutoCancel(true)
//        .setFullScreenIntent(
//            answerCallPendingIntent,
//            true
//        )
//        .build()
//
//    manager.notify(contact.name.hashCode(), notification)
//}
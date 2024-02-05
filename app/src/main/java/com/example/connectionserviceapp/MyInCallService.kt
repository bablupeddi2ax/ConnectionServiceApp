package com.example.connectionserviceapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.telecom.Call
import android.telecom.InCallService

class MyInCallService : InCallService() {

    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)

        // Handle the incoming call and show the notification
        showIncomingCallNotification(call)
    }

    private fun showIncomingCallNotification(call: Call) {
        val YOUR_CHANNEL_ID = "Your_Channel_Id"
        val YOUR_TAG = "Your_Tag"
        val YOUR_ID = 1

        // Create an intent which triggers your fullscreen incoming call user interface.
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_IMMUTABLE)

        // Build the notification as an ongoing high priority item; this ensures it will show as
        // a heads-up notification which slides down over top of the current content.
        val builder = Notification.Builder(this)
        builder.setOngoing(true)
        builder.setPriority(Notification.PRIORITY_HIGH)

        // Set notification content intent to take the user to the fullscreen UI if the user taps on the
        // notification body.
        builder.setContentIntent(pendingIntent)
        // Set full screen intent to trigger the display of the fullscreen UI when the notification
        // manager deems it appropriate.
        builder.setFullScreenIntent(pendingIntent, true)

        // Setup notification content.
        builder.setSmallIcon(R.drawable.ic_launcher_background)
        builder.setContentTitle("Your notification title")
        builder.setContentText("Your notification content.")

        // Use builder.addAction(..) to add buttons to answer or reject the call.

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                YOUR_CHANNEL_ID,
                "Incoming Calls",
                NotificationManager.IMPORTANCE_HIGH
            )
            // Set up other channel attributes, e.g., sound, vibration, etc.
            val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            channel.setSound(
                ringtoneUri,
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(YOUR_TAG, YOUR_ID, builder.build())
    }
}
//val phoneAccountHandle = PhoneAccountHandle(
//    ComponentName(this@MainActivity, MyConnectionService::class.java),
//    "ADMIN"
//)
//if (!telecomManager.isIncomingCallPermitted(phoneAccountHandle)) {
//    val intent = Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
//    intent.putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, packageName)
//    startActivity(intent)
//}
//val uri = Uri.fromParts("tel", "+91 88579 77254", null)
//val extras = Bundle()*/




/**/
/*/*  makeCallButton.setOnClickListener {
            val phoneNumber = phoneNumberEditText.text.toString().trim()
            if (phoneNumber.isNotEmpty()) {
                makeCall(phoneNumber)
            } else {
                Toast.makeText(this, "Enter a valid phone number", Toast.LENGTH_SHORT).show()
            }
        }*/


/*        // Use the correct phoneAccountHandle based on your implementation
      */
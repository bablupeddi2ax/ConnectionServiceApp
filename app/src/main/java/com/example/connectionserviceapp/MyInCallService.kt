package com.example.connectionserviceapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioDescriptor
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.media.AudioProfile
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.telecom.Call
import android.telecom.CallAudioState
import android.telecom.DisconnectCause
import android.telecom.InCallService
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService

class MyInCallService : InCallService() {
private var currentCall:Call? = null
    private var notificationManager:NotificationManager? = null
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)
        currentCall = call
        notificationManager = getSystemService(NotificationManager::class.java) as NotificationManager
        Log.i("MyIncoming", "inside onCallAdded")
        // Handle the incoming call and show the notification

        showIncomingCallNotification(call)
        Log.i("MyIncoming", "inside onCallAdded")
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun showIncomingCallNotification(call: Call) {
        val YOUR_CHANNEL_ID = "Your_Channel_Id"
        val YOUR_TAG = "Your_Tag"
        val YOUR_ID = 1

        // Create an intent which triggers your fullscreen incoming call user interface.
        val intent = Intent(this, InCallActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        intent.putExtra("",call.details.contactDisplayName)
        if(currentCall != null) {
            intent.putExtra("displayName", currentCall?.details?.contactDisplayName?.toString())
            intent.putExtra("callName", currentCall?.details?.callerDisplayName.toString())
            //intent.putExtra("callObject", currentCall) // Pass the Call o

        val acceptIntent = Intent(this, MyInCallService::class.java)
        acceptIntent.action = "AcceptCall"
        val rejectIntent = Intent(this, MyInCallService::class.java)
        rejectIntent.action = "RejectCall"

        val pendingIntentAccept = PendingIntent.getService(this, 1, acceptIntent, PendingIntent.FLAG_IMMUTABLE)
        val pendingIntentReject = PendingIntent.getService(this, 2, rejectIntent, PendingIntent.FLAG_IMMUTABLE)
        // Build the notification as an ongoing high priority item.
        val builder: NotificationCompat.Builder

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = NotificationCompat.Builder(this, YOUR_CHANNEL_ID)
        } else {
            builder = NotificationCompat.Builder(this)
        }

        builder.setSmallIcon(R.drawable.ic_launcher_background)
        builder.setContentTitle(call.details.callerDisplayName)
        builder.setContentText(call.details.accountHandle.id)
        builder.setExtras(call.details.extras)
        builder.setAutoCancel(false)
        builder.setContentIntent(PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_IMMUTABLE))

        // Set full-screen intent to trigger display of the fullscreen UI when the notification
        // manager deems it appropriate.
        builder.addAction(androidx.core.R.drawable.notification_bg, "Accept", pendingIntentAccept)
        builder.addAction(androidx.appcompat.R.drawable.abc_btn_switch_to_on_mtrl_00001, "Reject", pendingIntentReject)

        // Setup notification content.
        builder.setSmallIcon(R.drawable.ic_launcher_background)  // Replace with your actual icon resource ID
        builder.setContentTitle(call.details.callerDisplayName)
        builder.setContentText(call.details.callDirection.toString())

        // Set notification as insistent to cause your ringtone to loop.
        builder.priority = NotificationCompat.PRIORITY_HIGH
        builder.setCategory(NotificationCompat.CATEGORY_CALL)



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
            notificationManager?.createNotificationChannel(channel)

        }

        notificationManager?.notify(YOUR_TAG, YOUR_ID, builder.build())
    }

   }
    @RequiresApi(Build.VERSION_CODES.R)
    private fun rejectCall(call: Call) {
        call.reject(Call.REJECT_REASON_DECLINED)
        notificationManager?.cancelAll()
    }
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        when(intent?.action){
            "AcceptCall"->{
                currentCall?.let { setCallActive(it) }
                val inCallActivityIntent = Intent(this,InCallActivity::class.java)
                inCallActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                if(currentCall!=null) {
                    intent.putExtra("displayName", currentCall?.details?.contactDisplayName?.toString())
                    intent.putExtra("callName",currentCall?.details?.callerDisplayName.toString())

                }
                startActivity(inCallActivityIntent)
            }
            "RejectCall"->{
                currentCall?.let { rejectCall(it)
                    notificationManager?.cancel(1)
                }

            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCallRemoved(call: Call) {
        super.onCallRemoved(call)

        // Handle call removal as needed.
    }
    private fun setCallActive(call:Call){
        call.answer(0)

    }
    @RequiresApi(Build.VERSION_CODES.S)
    fun hold(){
        currentCall?.let {
        if(it.details.state==Call.STATE_ACTIVE){
           it.hold()
        }
        }
    }
    inner class MyBroadCastReceiver:BroadcastReceiver(){
        @RequiresApi(Build.VERSION_CODES.S)
        override fun onReceive(context: Context?, intent: Intent?) {
            when(intent?.action){
                "ACTION_SPEAKER_ON"->{
                    val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
                    audioManager.mode = AudioManager.MODE_IN_CALL
// Get the AudioDeviceInfo object for the built-in speaker
                    val speakerDevice = audioManager.availableCommunicationDevices.firstOrNull {
                        it.type == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER
                    }

// Ensure the speaker device is found
                    if (speakerDevice != null) {
                        audioManager.setCommunicationDevice(speakerDevice)
                    } else {
                        // Handle the error, e.g., log a message or inform the user
                    }
//
//                    audioManager.isSpeakerphoneOn = true

                }
                "ACTION_END"->{
                    currentCall?.reject(Call.REJECT_REASON_DECLINED)
                    notificationManager?.cancel(1)
                }
            }
        }
    }



}

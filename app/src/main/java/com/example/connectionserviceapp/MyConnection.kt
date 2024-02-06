package com.example.connectionserviceapp

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telecom.CallAudioState
import android.telecom.Connection
import android.telecom.ConnectionRequest
import android.telecom.DisconnectCause
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class MyConnection(private val context: Context) : Connection() {
//    init {
//        setCallerDisplayName("Your App Name", TelecomManager.PRESENTATION_ALLOWED)
//        setAddress(phoneNumber, TelecomManager.PRESENTATION_ALLOWED)
//        audioModeIsVoip = true
//        setRinging()
//    }
private var isIncomingCall = false
    val TAG=MyConnection::class.java.canonicalName

    /**
     * This callback is triggered whenever there is a change in the call state.
     * The onStateChanged method provides the new state as an integer parameter.
     */

    override fun onStateChanged(state: Int) {
        super.onStateChanged(state)
        Log.d(TAG,"onStateChanged")
        when(state){
            STATE_NEW-> if(isIncomingCall){
                onShowIncomingCallUi()
            }
            STATE_INITIALIZING->Log.d(TAG,"STATE_INITIALIZING")
            STATE_RINGING->Log.d(TAG,"STATE_RINGING")
            STATE_DIALING->Log.d(TAG,"STATE_DIALING")
            STATE_ACTIVE-> {
                if(isIncomingCall){
                    navigateToInCallUI()
                }
            }

            STATE_HOLDING->Log.d(TAG,"STATE_HOLDING")
            STATE_DISCONNECTED->destroy()
        }
    }

    /**
     * This callback is triggered when it's time to show the incoming call UI to the user.
     * It allows you to customize the UI when an incoming call is received.
     */
    //show incoming call UI.
    override fun onShowIncomingCallUi() {
        super.onShowIncomingCallUi()
        isIncomingCall = true
        // Trigger the display of the incoming call user interface using a Notification
        val context: Context = context
        val YOUR_CHANNEL_ID = "Your_Channel_Id"
        val YOUR_TAG = "Your_Tag"
        val YOUR_ID = 1

        // Create an intent which triggers your fullscreen incoming call user interface.
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_MUTABLE)

        // Build the notification as an ongoing high priority item; this ensures it will show as
        // a heads-up notification which slides down over top of the current content.
        val builder = NotificationCompat.Builder(context, YOUR_CHANNEL_ID)
        builder.setOngoing(true)
        builder.setPriority(NotificationCompat.PRIORITY_HIGH)

        // Set notification content intent to take the user to fullscreen UI if the user taps on the
        // notification body.
        builder.setContentIntent(pendingIntent)
        // Set full-screen intent to trigger display of the fullscreen UI when the notification
        // manager deems it appropriate.
        builder.setFullScreenIntent(pendingIntent, true)

        // Setup notification content.
        builder.setSmallIcon(R.drawable.ic_launcher_background)
        builder.setContentTitle("Your notification title")
        builder.setContentText("Your notification content.")

        // Set notification as insistent to cause your ringtone to loop.
        val notification = builder.build()
        notification.flags = notification.flags or Notification.FLAG_INSISTENT

        // Use NotificationManagerCompat to handle notifications on devices with different API levels.
        val notificationManager = NotificationManagerCompat.from(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                YOUR_CHANNEL_ID,
                "Incoming Calls",
                NotificationManager.IMPORTANCE_HIGH
            )
            // Set up other channel attributes, e.g., sound, vibration, etc.
            channel.setSound(
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE),
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            notificationManager.createNotificationChannel(channel)
        }

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        notificationManager.notify(YOUR_TAG, YOUR_ID, notification)
    }
    private fun navigateToInCallUI() {
        // Start the InCallActivity or use your existing UI logic
        val intent = Intent(context, InCallActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    override fun onHold() {
        super.onHold()
        setOnHold()
    }

    override fun onUnhold() {
        super.onUnhold()
        setActive()
    }

    /**
     * This callback is triggered when you want to accept or answer an incoming call.
     * It is typically called in response to user interaction when the user decides to accept the incoming call.
     */
    override fun onAnswer() {
        super.onAnswer()
        setActive()
        Log.d(TAG,"onAnswer")
    }


    override fun onReject() {
        super.onReject()
        setDisconnected(DisconnectCause(DisconnectCause.REJECTED))
        destroy()
        Log.d(TAG,"onReject")
    }

    /**
     * This callback is triggered when a call is being disconnected.
     * It is called when you initiate the disconnection of a call or when the call naturally ends.
     */
    override fun onDisconnect() {
        super.onDisconnect()
        Log.d(TAG,"onDisconnect")
        setDisconnected(DisconnectCause(DisconnectCause.LOCAL))
        destroy()
    }

    override fun onAbort() {
        super.onAbort()
        Log.d(TAG,"onAbort")
    }

    override fun onAnswer(videoState: Int) {
        super.onAnswer(videoState)
        Log.d(TAG,"onAnswer")
    }


    override fun onCallEvent(event: String?, extras: Bundle?) {
        super.onCallEvent(event, extras)
        Log.d(TAG,"onCallEvent")
    }

    override fun onMuteStateChanged(isMuted: Boolean) {
        super.onMuteStateChanged(isMuted)
    }
    override fun onCallAudioStateChanged(state: CallAudioState?) {
        super.onCallAudioStateChanged(state)
        Log.d(TAG,"onCallAudioStateChanged"+state)
        state?.let {
            if(it.isMuted){

            }else{

            }
        }
    }
    fun setCallOnHold() {
        // Set the capabilities to indicate support for hold
        connectionCapabilities = Connection.CAPABILITY_HOLD or Connection.CAPABILITY_SUPPORT_HOLD
    }
    fun setCallerName(displayName:String){
        setCallerDisplayName(displayName,TelecomManager.PRESENTATION_ALLOWED)
    }
    fun setVideoStateFromRequest(connectionRequest:ConnectionRequest){
        videoState = connectionRequest.videoState
    }
    // ... (Other existing methods)

    fun placeOutgoingCall(destinationUri: Uri, phoneAccountHandle: PhoneAccountHandle) {
        // Implement the logic to place the outgoing call
        // You can use the provided URI and phone account handle to initiate the call
        // For example, you can use TelecomManager to place the call
        val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
        val extras = Bundle()
        extras.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, phoneAccountHandle)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        telecomManager.placeCall(destinationUri, extras)
    }
}
//  val phoneAccountHandle = PhoneAccountHandle(
//            ComponentName(this, MyConnectionService::class.java),
//            "UID234"
//        )
//
//        val phoneAccount = PhoneAccount.builder(phoneAccountHandle, "Your App Name")
//            .setCapabilities(PhoneAccount.CAPABILITY_SELF_MANAGED)
//            .build()
//
//        val telecomManager: TelecomManager = getSystemService(TelecomManager::class.java)
//        telecomManager.registerPhoneAccount(phoneAccount)




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
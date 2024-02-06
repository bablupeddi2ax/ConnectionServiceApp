package com.example.connectionserviceapp

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.telecom.PhoneAccount
import android.telecom.TelecomManager
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import org.w3c.dom.Text

class InCallActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_in_call)
        val txtDisplayName = findViewById<TextView>(R.id.txtDiaplyName)
        val txtCallName = findViewById<TextView>(R.id.txtName)

        val callIntent = intent
        val displayName = callIntent.getStringExtra("displayName")
        val callName = callIntent.getStringExtra("callName")

        if (!displayName.isNullOrBlank() && !callName.isNullOrBlank()) {
            // If the intent extras contain valid information, update the UI
            txtDisplayName.text = displayName
            txtCallName.text = callName
        } else {
            // Handle the case where the intent does not contain valid information
            // You may want to finish the activity or display an error message

        }
        val telecomManager = getSystemService(TelecomManager::class.java) as TelecomManager
        if (ActivityCompat.checkSelfPermission(
                this,
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
        val phoneNumberUri: Uri = Uri.fromParts(PhoneAccount.SCHEME_TEL,
            Uri.parse("+91 9390589284").toString(), "")
        telecomManager.placeCall(phoneNumberUri,Bundle())
        val btnSpeaker = findViewById<Button>(R.id.btnSpeaker)
        val btnEnd = findViewById<Button>(R.id.btnEnd)
        btnSpeaker.setOnClickListener {
            val speakerOnIntent = Intent(this, MyInCallService::class.java)
                .setAction("ACTION_SPEAKER_ON")
            val pendingIntent = PendingIntent.getService(this, 0, speakerOnIntent,
                PendingIntent.FLAG_IMMUTABLE)
            sendBroadcast(speakerOnIntent)
        }
        btnEnd.setOnClickListener{
            val endIntent   = Intent(this,MyInCallService::class.java).setAction("ACTION_END")
        }
    }
    /*speakerButton.setOnClickListener {
    val speakerOnIntent = Intent(this, MyInCallService::class.java)
        .setAction("ACTION_SPEAKER_ON")
        .putExtra("callId", currentCallId) // Pass relevant call information
    val pendingIntent = PendingIntent.getService(this, 0, speakerOnIntent, 0)
    sendBroadcast(speakerOnIntent)
}*/

}
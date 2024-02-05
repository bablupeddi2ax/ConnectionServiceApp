package com.example.connectionserviceapp
import android.app.NotificationManager
import android.os.Build
import android.telecom.Connection
import android.telecom.ConnectionRequest
import android.telecom.ConnectionService
import android.telecom.DisconnectCause
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

class MyConnectionService : ConnectionService() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateOutgoingConnection(
        phoneAccount: PhoneAccountHandle?,
        connectionRequest: ConnectionRequest?
    ): Connection {
        // Implement the creation of outgoing connections
        val myConnection = MyConnection(applicationContext)
        myConnection.connectionProperties = Connection.PROPERTY_SELF_MANAGED
        val destinationUri = connectionRequest?.address
        val phoneAccountHandle  = connectionRequest?.accountHandle
        if(destinationUri!=null && phoneAccountHandle!=null){
            myConnection.placeOutgoingCall(destinationUri,phoneAccountHandle)
        }else{
            Toast.makeText(applicationContext,"Outgoing call couldn ot be placed.Please try later",Toast.LENGTH_SHORT).show()
            onCreateOutgoingConnectionFailed(phoneAccount,connectionRequest)
        }
        val bundle = connectionRequest!!.extras
        val callerName = bundle.getString(TelecomManager.EXTRA_CALL_SUBJECT)
        myConnection.setCallerDisplayName(callerName,TelecomManager.PRESENTATION_ALLOWED)
        myConnection.setAddress(connectionRequest.address,TelecomManager.PRESENTATION_ALLOWED)
//        myConnection.setCallerName("name")
        myConnection.setVideoStateFromRequest(connectionRequest!!)
        myConnection.setCallOnHold()
        return myConnection
    }
    // telecom subsystem calls this method when app calls the placeCall(Uri,Bundle) in response of new incoing callapp return new instance of Connection implmentation
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateIncomingConnection(
        phoneAccount: PhoneAccountHandle?,
        connectionRequest: ConnectionRequest?
    ): Connection {
        // Implement the creation of incoming connections
        val connection = MyConnection(applicationContext)
        connection.connectionProperties = Connection.PROPERTY_SELF_MANAGED
        phoneAccount?.id?.let { connection.setCallerName(it) }
        val bundle = connectionRequest!!.extras
        val callerName = bundle.getString(TelecomManager.EXTRA_CALL_SUBJECT)
        connection.setCallerDisplayName(callerName,TelecomManager.PRESENTATION_ALLOWED)
        connection.setAddress(connectionRequest.address,TelecomManager.PRESENTATION_ALLOWED)

        connection.setVideoStateFromRequest(connectionRequest!!)
        connection.setCallOnHold()
        return connection
    }

    // Override other necessary methods
    override fun onCreateOutgoingConnectionFailed(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ) {
        super.onCreateOutgoingConnectionFailed(connectionManagerPhoneAccount, request)
        Toast.makeText(applicationContext,"Outgoing call could not be placed.Please try later",Toast.LENGTH_LONG).show()
    }

    override fun onCreateIncomingConnectionFailed(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ) {
        super.onCreateIncomingConnectionFailed(connectionManagerPhoneAccount, request)
        val connection = MyConnection(applicationContext)
        connection.connectionProperties = Connection.PROPERTY_SELF_MANAGED
        connection.setDisconnected(DisconnectCause(DisconnectCause.REJECTED))

        val notificationManager  = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(this,"DISCONNECT_CHANNEL")
            .setContentTitle("Missed Call")
            .setContentText("You have a missed call.")
            .build()
        notificationManager.notify(1,notification)
    }
}

//        // Use placeCall without the incorrect third parameter
//        val test = Bundle()
//        test.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, phoneAccountHandle)
//        test.putInt(TelecomManager.EXTRA_START_CALL_WITH_VIDEO_STATE, VideoProfile.STATE_BIDIRECTIONAL)
//        test.putParcelable(TelecomManager.EXTRA_OUTGOING_CALL_EXTRAS, extras)
//        telecomManager.getPhoneAccount(phoneAccountHandle)
//        telecomManager.showInCallScreen(true)
//        telecomManager.placeCall(uri, test)
//        updateCallStatus("Calling $phoneNumber")
//        val m = MyConnectionService()




//

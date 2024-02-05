package com.example.connectionserviceapp
import android.Manifest
import android.app.Activity
import android.app.Notification
import android.app.NotificationManager
import android.app.role.RoleManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telecom.PhoneAccount
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.telecom.VideoProfile
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {
    private lateinit var  myConnection :MyConnection
    private val requestRoleLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            // Your app is now the default dialer app
        } else {
            // Your app is not the default dialer app
        }

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        myConnection = MyConnection(applicationContext)
        requestRole()
        val edtPhone = findViewById<EditText>(R.id.editTextPhoneNumber)
        edtPhone.inputType = InputType.TYPE_CLASS_NUMBER

        val btnCall = findViewById<Button>(R.id.buttonMakeCall)
        btnCall.setOnClickListener{
           // make casll to repective phonwnumber for now it will be (90390589284) through whatever service or like  dont know exactly but chekcout services u have made o
        // r telecomnaznger or smthg to make cal
        }
        if (!checkNotificationPermission()) {
            requestPostNotificationPermission()
        }
        requestDefaultPhoneAppRole()
        if (!hasReadPhoneNumbers()) {
            requestReadPhoneNumberPermission()
        }
        if (!hasAnswerPhone()) {
            requestAnswerPhoneCallsPermission()
        }
        if (!hasCallPhonePermission()) {
            requestCallPhonePermission()
        }

        if (!hasReadPhoneStatePermission()) {
            requestPhoneStatePermission()
        }
        // Request permissions if not granted
        if (!hasCallPermission()) {
            requestInCallPermission()
        }

    }

    private fun requestPostNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            startActivity(intent)
        } else {
            // For devices prior to Android 8, show a toast to explain how to enable notifications
            Toast.makeText(
                this,
                "Please enable notifications for this app in system settings",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun checkNotificationPermission(): Boolean {
        return NotificationManagerCompat.from(this).areNotificationsEnabled()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun makeCall(phoneNumber: String) {
        val telecomManager: TelecomManager = getSystemService(TelecomManager::class.java)

        // Check for READ_PHONE_STATE permission
        if (!hasReadPhoneStatePermission()) {
            // Handle the case where permission is not granted
            Toast.makeText(this, "Read phone state permission not granted", Toast.LENGTH_SHORT)
                .show()
            return
        }


        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Handle the case where permission is not granted
            Toast.makeText(this, "Call permission not granted", Toast.LENGTH_SHORT).show()
            return
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun hasReadPhoneNumbers(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_PHONE_NUMBERS
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_PHONE_NUMBERS
                ) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun hasAnswerPhone(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ANSWER_PHONE_CALLS
        ) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun hasCallPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.MANAGE_OWN_CALLS
        ) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun hasCallPhonePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasReadPhoneStatePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_PHONE_STATE
        ) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun requestReadPhoneNumberPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_PHONE_NUMBERS,
            ),
            5
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun requestAnswerPhoneCallsPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ANSWER_PHONE_CALLS,
            ),
            4
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun requestInCallPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.MANAGE_OWN_CALLS,
            ),
            3
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun requestCallPhonePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.CALL_PHONE,
            ),
            2
        )
    }

    private fun requestPhoneStatePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_PHONE_STATE
            ),
            1
        )
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun requestDefaultPhoneAppRole() {
        val roleManager = getSystemService(RoleManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER)
            requestRoleLauncher.launch(intent)
        }

    }
    private val REQUEST_ID = 1

    @RequiresApi(Build.VERSION_CODES.Q)
    fun requestRole() {
        val roleManager = getSystemService(Context.ROLE_SERVICE) as RoleManager
        val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER)
        startActivityForResult(intent, REQUEST_ID)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ID) {
            if (resultCode == Activity.RESULT_OK) {
                // Your app is now the default dialer app
            } else {
                // Your app is not the default dialer app
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        myConnection.onDisconnect()
    }

}








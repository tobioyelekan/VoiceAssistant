package com.tobi.voiceassistant.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.tobi.voiceassistant.R
import com.tobi.voiceassistant.config.Talk
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.util.*

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener, View.OnClickListener {

    companion object {
        const val MY_DATA_CHECK_CODE = 1
        const val SMS_PERMISSION = 1
        const val PHONE_PERMISSION = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val checkIntent = Intent()
        checkIntent.action = TextToSpeech.Engine.ACTION_CHECK_TTS_DATA
        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE)

        msg.setOnClickListener(this)
        call.setOnClickListener(this)
        note.setOnClickListener(this)
        battery.setOnClickListener(this)

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.msg -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (smsPermitted()) {
                        ((applicationContext as Talk)).textToSpeech?.speak(
                                "you clicked message, click again to confirm", TextToSpeech.QUEUE_FLUSH, null)

                        openMessage();
                    } else {
                        toast("permission")
                    }
                } else {
                    openMessage();
                }
            }
            R.id.call -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (callPermitted()) {
                        ((applicationContext as Talk)).textToSpeech?.speak(
                                "you clicked call, click again to confirm", TextToSpeech.QUEUE_FLUSH, null)

                        openCall()
                    } else {
                        toast("permission needed")
                    }
                } else {
                    openCall()
                }

            }

            R.id.note -> {
                ((applicationContext as Talk)).textToSpeech?.speak(
                        "you clicked note, click again to confirm", TextToSpeech.QUEUE_FLUSH, null)
                startActivity<Notes>()
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
            }

            R.id.battery -> {
                ((applicationContext as Talk)).textToSpeech?.speak(
                        "you clicked battery, click again to confirm", TextToSpeech.QUEUE_FLUSH, null)
                startActivity<BatteryDetails>()
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
            }
        }
    }

    private fun openMessage() {
        startActivity<MessageDetails>()
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

    private fun openCall() {
        startActivity<CallDetails>()
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

    private fun smsPermitted(): Boolean {

        var permitted = true
        val permissions = arrayListOf(android.Manifest.permission.READ_SMS,
                android.Manifest.permission.SEND_SMS)

        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permitted = false
            }
        }

        if (!permitted)
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_SMS,
                    android.Manifest.permission.SEND_SMS), SMS_PERMISSION)

        return permitted

    }

    private fun callPermitted(): Boolean {

        var permitted = true
        val permissions = arrayListOf(android.Manifest.permission.READ_CONTACTS,
                android.Manifest.permission.READ_CALL_LOG, android.Manifest.permission.CALL_PHONE)

        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permitted = false
            }
        }

        if (!permitted)
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_CONTACTS,
                    android.Manifest.permission.READ_CALL_LOG, android.Manifest.permission.CALL_PHONE), PHONE_PERMISSION)

        return permitted

//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) !=
//                PackageManager.PERMISSION_GRANTED) {
//            ((applicationContext as Talk)).textToSpeech?.speak(
//                    "permission needed to read phone sms", TextToSpeech.QUEUE_FLUSH, null)
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CALL_LOG)) {
//                ((applicationContext as Talk)).textToSpeech?.speak(
//                        "permission needed to read phone sms", TextToSpeech.QUEUE_ADD, null)
//            } else {
//                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CALL_LOG),
//                        CALL_LOG_PERMISSION)
//            }
//        } else {
//            return true
//        }
//
//        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                ((applicationContext as Talk)).textToSpeech = TextToSpeech(this, this)
            } else {
                val installIntent = Intent()
                installIntent.action = TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA
                startActivity(installIntent)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            SMS_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openMessage()
                } else {
                    (applicationContext as Talk).textToSpeech!!.speak("unable to read messages as permission is not granted", TextToSpeech.QUEUE_FLUSH, null)
                }
            }

            PHONE_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCall()
                } else {
                    (applicationContext as Talk).textToSpeech!!.speak(
                            "unable to see call logs as permission is not granted", TextToSpeech.QUEUE_FLUSH, null)
                }
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            if (((applicationContext as Talk)).textToSpeech?.isLanguageAvailable(Locale.US) == TextToSpeech.LANG_COUNTRY_AVAILABLE) {
                ((applicationContext as Talk)).textToSpeech?.language = Locale.US

                (applicationContext as Talk).textToSpeech?.speak("Welcome to voice assistant app, click on the different size of the screen to know details",
                        TextToSpeech.QUEUE_FLUSH, null)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (applicationContext as Talk).textToSpeech?.speak("Welcome to voice assistant app, click on the different size of the screen to know details",
                TextToSpeech.QUEUE_FLUSH, null)
    }

    override fun onDestroy() {
        if ((applicationContext as Talk).textToSpeech != null) {
            (applicationContext as Talk).textToSpeech!!.shutdown()
        }
        super.onDestroy()
    }
}
package com.tobi.voiceassistant.activities

import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.support.v7.app.AppCompatActivity
import com.tobi.voiceassistant.R
import com.tobi.voiceassistant.config.Talk
import kotlinx.android.synthetic.main.activity_battery_details.*
import kotlinx.android.synthetic.main.content_battery_status.*

class BatteryDetails : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_battery_details)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        getBattery()

    }

    private fun getBattery() {
        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = registerReceiver(null, intentFilter)

        val batteryPct: Float? = batteryStatus?.let { intent ->
            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            level / scale.toFloat()
        }

        val batteryLevel = batteryPct?.times(100)?.toInt()
        percent.text = String.format("%s%s", batteryLevel.toString(), "%")

        ((applicationContext as Talk)).textToSpeech?.speak(
                "your battery level is ${batteryLevel.toString()} percent}", TextToSpeech.QUEUE_FLUSH, null)

        val statusBattery: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging: Boolean = statusBattery == BatteryManager.BATTERY_STATUS_CHARGING
                || statusBattery == BatteryManager.BATTERY_STATUS_FULL

        if (isCharging) {
            status.text = "battery is charging"
            ((applicationContext as Talk)).textToSpeech?.speak(
                    "and your device is charging", TextToSpeech.QUEUE_ADD, null)
        } else {
            status.text = "battery is not charging"
            ((applicationContext as Talk)).textToSpeech?.speak(
                    "and your device is not charging", TextToSpeech.QUEUE_ADD, null)
        }

        if (isCharging) {
            when (batteryLevel) {
                in 0..20 -> {
                    battery_icon.setImageDrawable(resources.getDrawable(R.drawable.battery_charging_20))
                }
                in 21..40 -> {
                    battery_icon.setImageDrawable(resources.getDrawable(R.drawable.battery_charging_30))
                }
                in 41..50 -> {
                    battery_icon.setImageDrawable(resources.getDrawable(R.drawable.battery_charging_50))
                }
                in 51..60 -> {
                    battery_icon.setImageDrawable(resources.getDrawable(R.drawable.battery_charging_60))
                }
                in 61..80 -> {
                    battery_icon.setImageDrawable(resources.getDrawable(R.drawable.battery_charging_80))
                }
                in 81..90 -> {
                    battery_icon.setImageDrawable(resources.getDrawable(R.drawable.battery_charging_90))
                }
                in 91..99 -> {
                    battery_icon.setImageDrawable(resources.getDrawable(R.drawable.battery_charging_full))
                }
                100 -> {
                    battery_icon.setImageDrawable(resources.getDrawable(R.drawable.battery_full))
                }
            }
        } else {

            when (batteryLevel) {
                in 0..20 -> {
                    battery_icon.setImageDrawable(resources.getDrawable(R.drawable.battery_20))
                }
                in 21..40 -> {
                    battery_icon.setImageDrawable(resources.getDrawable(R.drawable.battery_30))
                }
                in 41..50 -> {
                    battery_icon.setImageDrawable(resources.getDrawable(R.drawable.battery_50))
                }
                in 51..60 -> {
                    battery_icon.setImageDrawable(resources.getDrawable(R.drawable.battery_60))
                }
                in 61..80 -> {
                    battery_icon.setImageDrawable(resources.getDrawable(R.drawable.battery_80))
                }
                in 81..90 -> {
                    battery_icon.setImageDrawable(resources.getDrawable(R.drawable.battery_90))
                }
                in 91..100 -> {
                    battery_icon.setImageDrawable(resources.getDrawable(R.drawable.battery_full))
                }
            }

        }

// How are we charging?
//        val chargePlug: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: -1
//        val usbCharge: Boolean = chargePlug == BatteryManager.BATTERY_PLUGGED_USB
//        val acCharge: Boolean = chargePlug == BatteryManager.BATTERY_PLUGGED_AC

    }
}
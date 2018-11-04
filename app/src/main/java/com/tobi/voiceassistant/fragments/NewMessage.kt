package com.tobi.voiceassistant.fragments

import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.support.v4.app.Fragment
import android.telephony.SmsManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tobi.voiceassistant.R
import com.tobi.voiceassistant.activities.SmsOptions
import com.tobi.voiceassistant.config.Talk
import kotlinx.android.synthetic.main.fragment_new_message.*

class NewMessage : Fragment() {

    companion object {
        const val MESSAGE_OPTIONS = 1
    }

    private var sentStatusReceiver: BroadcastReceiver? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_new_message, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        number.setOnLongClickListener { openDialog(); true }
        message.setOnLongClickListener { openDialog(); true }
        number.addTextChangedListener(textWatcher)
        message.addTextChangedListener(textWatcher2)
        number.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) ((activity?.applicationContext as Talk)).textToSpeech?.speak(
                    "number is now focus", TextToSpeech.QUEUE_FLUSH, null)
        }
        message.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) ((activity?.applicationContext as Talk)).textToSpeech?.speak(
                    "message body is now focus", TextToSpeech.QUEUE_FLUSH, null)
        }
    }

    private fun openDialog() {
        var open = true;

        if (number.text.isNullOrEmpty() || message.text.isNullOrEmpty()) {
            open = false
        }

        if (!open) {
            ((activity?.applicationContext as Talk)).textToSpeech?.speak("cannot open options",
                    TextToSpeech.QUEUE_FLUSH, null)
            if (number.text.isNullOrEmpty()) {
                ((activity?.applicationContext as Talk)).textToSpeech?.speak("please enter a number",
                        TextToSpeech.QUEUE_ADD, null)
            }

            if (message.text.isNullOrEmpty()) {
                ((activity?.applicationContext as Talk)).textToSpeech?.speak("please enter a message",
                        TextToSpeech.QUEUE_ADD, null)
            }
        } else {
            val sms_options = Intent(activity, SmsOptions::class.java)
            sms_options.putExtra("phone", number.text.toString())
            sms_options.putExtra("message", message.text.toString())
            startActivityForResult(sms_options, MESSAGE_OPTIONS, null)
//            activity?.startActivityForResult<SmsOptions>(MESSAGE_OPTIONS, "phone" to number.text.toString(),
//                    "message" to message.text.toString())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == MESSAGE_OPTIONS) {
                sendSms()
            }
        }
    }

    private val textWatcher = object : TextWatcher {
        var previous: String = ""
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            previous = if (!s.isNullOrEmpty()) {
                compare(s.toString(), previous)
                s.toString()
            } else {
                ((activity?.applicationContext as Talk)).textToSpeech?.speak(
                        "you deleted $previous", TextToSpeech.QUEUE_FLUSH, null)
                ""
            }
        }
    }

    private val textWatcher2 = object : TextWatcher {
        var previous: String = ""

        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            previous = if (!s.isNullOrEmpty()) {
                compare(s.toString(), previous)
                s.toString()
            } else {
                ((activity?.applicationContext as Talk)).textToSpeech?.speak(
                        "you deleted $previous", TextToSpeech.QUEUE_FLUSH, null)
                ""
            }
        }
    }

    private fun compare(current: String, previous: String) {
        if (previous.isNotEmpty()) {
            if (current.length < previous.length) {
                ((activity?.applicationContext as Talk)).textToSpeech?.speak(
                        "you deleted ${previous.last()}", TextToSpeech.QUEUE_FLUSH, null)
            } else if (current.length > previous.length) {
                ((activity?.applicationContext as Talk)).textToSpeech?.speak(
                        "you typed ${current.last()}", TextToSpeech.QUEUE_FLUSH, null)
            }
        } else {
            ((activity?.applicationContext as Talk)).textToSpeech?.speak(
                    "you typed ${current.last()}", TextToSpeech.QUEUE_FLUSH, null)
        }
    }

    private fun sendSms() {

        ((activity?.applicationContext as Talk)).textToSpeech?.speak(
                "message is sending", TextToSpeech.QUEUE_FLUSH, null)

        try {
            val smsManager = SmsManager.getDefault()
            val sentIntent = PendingIntent.getBroadcast(activity, 0, Intent("SMS_SENT"), 0)
            smsManager.sendTextMessage(number.text.toString(), null, message.text.toString(),
                    sentIntent, null)
        } catch (e: Exception) {

        }
    }

    override fun onResume() {
        super.onResume()
        sentStatusReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        number.text = null
                        message.text = null

                        ((activity?.applicationContext as Talk)).textToSpeech?.speak(
                                "message was sent successfully", TextToSpeech.QUEUE_FLUSH, null)
                    }
                    SmsManager.RESULT_ERROR_GENERIC_FAILURE -> {
                        ((activity?.applicationContext as Talk)).textToSpeech?.speak(
                                "message was not sent", TextToSpeech.QUEUE_FLUSH, null)
                    }
                    SmsManager.RESULT_ERROR_NO_SERVICE -> {
                        ((activity?.applicationContext as Talk)).textToSpeech?.speak(
                                "message was not sent", TextToSpeech.QUEUE_FLUSH, null)
                    }
                    SmsManager.RESULT_ERROR_NULL_PDU -> {
                        ((activity?.applicationContext as Talk)).textToSpeech?.speak(
                                "message was not sent", TextToSpeech.QUEUE_FLUSH, null)
                    }
                    SmsManager.RESULT_ERROR_RADIO_OFF -> {
                        ((activity?.applicationContext as Talk)).textToSpeech?.speak(
                                "message was not sent", TextToSpeech.QUEUE_FLUSH, null)
                    }
                }
            }

        }

        activity?.registerReceiver(sentStatusReceiver, IntentFilter("SMS_SENT"))
    }

    override fun onPause() {
        super.onPause()
        if (sentStatusReceiver != null)
            activity?.unregisterReceiver(sentStatusReceiver)
    }

}
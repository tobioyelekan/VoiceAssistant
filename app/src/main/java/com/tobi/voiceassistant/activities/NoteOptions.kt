package com.tobi.voiceassistant.activities

import android.app.Activity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.support.v7.app.AppCompatActivity
import android.telephony.SmsManager
import android.view.View
import android.widget.Toast
import com.tobi.voiceassistant.R
import com.tobi.voiceassistant.config.DoubleClick
import com.tobi.voiceassistant.config.DoubleClickListener
import com.tobi.voiceassistant.config.Talk
import kotlinx.android.synthetic.main.sms_options_layout.*

class NoteOptions : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.note_options_layout)

        this.setFinishOnTouchOutside(false)

        val title = intent.getStringExtra("title")
        val body = intent.getStringExtra("body")

        click.setOnClickListener(DoubleClick(object : DoubleClickListener {
            override fun onSingleClick(view: View) {
                (applicationContext as Talk).textToSpeech?.speak("title is $title",
                        TextToSpeech.QUEUE_FLUSH, null)
                (applicationContext as Talk).textToSpeech?.speak("body of note is $body",
                        TextToSpeech.QUEUE_ADD, null)
            }

            override fun onDoubleClick(view: View) {
                setResult(Activity.RESULT_OK)
                finish()
            }

            override fun onTripleClick(view: View) {
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        }))

        (applicationContext as Talk).textToSpeech?.speak(
                "options open, tap the screen once to read the note",
                TextToSpeech.QUEUE_FLUSH, null)

        (applicationContext as Talk).textToSpeech?.speak(
                "twice to save note",
                TextToSpeech.QUEUE_ADD, null)

        (applicationContext as Talk).textToSpeech?.speak(
                "thrice to cancel",
                TextToSpeech.QUEUE_ADD, null)

    }

}
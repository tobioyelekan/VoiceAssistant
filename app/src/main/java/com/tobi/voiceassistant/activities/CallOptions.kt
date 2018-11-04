package com.tobi.voiceassistant.activities

import android.os.Bundle
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.support.v7.app.AppCompatActivity
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.tobi.voiceassistant.R
import kotlinx.android.synthetic.main.call_options_layout.*
import android.view.ViewConfiguration
import android.widget.Toast
import android.view.GestureDetector.OnDoubleTapListener
import com.tobi.voiceassistant.config.DoubleClick
import com.tobi.voiceassistant.config.DoubleClickListener
import com.tobi.voiceassistant.config.Talk
import org.jetbrains.anko.makeCall


class CallOptions : AppCompatActivity() {

    var number: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.call_options_layout)

        this.setFinishOnTouchOutside(false)

        number = intent.getStringExtra("number")

        click.setOnClickListener(DoubleClick(object : DoubleClickListener {
            override fun onSingleClick(view: View) {
                (applicationContext as Talk).textToSpeech?.speak(
                        "the number is $number",
                        TextToSpeech.QUEUE_FLUSH, null)
            }

            override fun onDoubleClick(view: View) {
                makeCall(number)
            }

            override fun onTripleClick(view: View) {
                finish()
            }
        }))

        (applicationContext as Talk).textToSpeech?.speak(
                "options open, tap the screen once to speak the number",
                TextToSpeech.QUEUE_FLUSH, null)

        (applicationContext as Talk).textToSpeech?.speak(
                "twice to call",
                TextToSpeech.QUEUE_ADD, null)

        (applicationContext as Talk).textToSpeech?.speak(
                "thrice to cancel",
                TextToSpeech.QUEUE_ADD, null)


    }
}
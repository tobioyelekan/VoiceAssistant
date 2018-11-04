package com.tobi.voiceassistant.config

import android.app.Application
import android.speech.tts.TextToSpeech
import java.util.Locale.*

class Talk : Application() {
    var textToSpeech: TextToSpeech? = null
}
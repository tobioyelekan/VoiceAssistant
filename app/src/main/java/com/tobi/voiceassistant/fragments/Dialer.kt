package com.tobi.voiceassistant.fragments

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.tobi.voiceassistant.R
import com.tobi.voiceassistant.config.Talk
import kotlinx.android.synthetic.main.fragment_dialer.*
import android.text.Editable
import android.text.TextWatcher
import com.tobi.voiceassistant.activities.CallOptions
import org.jetbrains.anko.startActivity

class Dialer : Fragment(), View.OnClickListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        welcomeSpeak()
        return inflater.inflate(R.layout.fragment_dialer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        screen.setOnLongClickListener { openOptions(); true }
        number.setOnLongClickListener { openOptions(); true }
        number.addTextChangedListener(numberWatcher)
        one.setOnClickListener(this)
        one.setOnLongClickListener { openOptions(); true }
        two.setOnClickListener(this)
        two.setOnLongClickListener { openOptions(); true }
        three.setOnClickListener(this)
        three.setOnLongClickListener { openOptions(); true }
        four.setOnClickListener(this)
        four.setOnLongClickListener { openOptions(); true }
        five.setOnClickListener(this)
        five.setOnLongClickListener { openOptions(); true }
        six.setOnClickListener(this)
        six.setOnLongClickListener { openOptions(); true }
        seven.setOnClickListener(this)
        seven.setOnLongClickListener { openOptions(); true }
        eight.setOnClickListener(this)
        eight.setOnLongClickListener { openOptions(); true }
        nine.setOnClickListener(this)
        nine.setOnLongClickListener { openOptions(); true }
        zero.setOnClickListener(this)
        zero.setOnLongClickListener { openOptions(); true }
        ast.setOnClickListener(this)
        ast.setOnLongClickListener { openOptions(); true }
        hash.setOnClickListener(this)
        hash.setOnLongClickListener { openOptions(); true }
        backspace.setOnClickListener(this)
        backspace.setOnLongClickListener {
            number.text = null
            (activity?.applicationContext as Talk).textToSpeech!!.speak("the number has been cleared",
                    TextToSpeech.QUEUE_ADD, null)
            true
        }
//        number.setOnTouchListener(OnTouchListener { v, event ->
//
//            val DRAWABLE_RIGHT = 2
//
//            if (event.action == MotionEvent.ACTION_UP) {
//                if (event.rawX >= number.right - number.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) {
//                    // your action here
//                    Toast.makeText(activity, "backspace", Toast.LENGTH_SHORT).show()
//                    return@OnTouchListener true
//                }
//            }
//            false
//        })
    }

//    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
//        super.setUserVisibleHint(isVisibleToUser)
//
//        if (isVisibleToUser) welcomeSpeak()
//    }

    private fun openOptions() {
        if (number.text.isNullOrBlank()) {
            (activity?.applicationContext as Talk).textToSpeech!!.speak(
                    "you have not typed in any number",
                    TextToSpeech.QUEUE_FLUSH, null)
        } else {
            activity?.startActivity<CallOptions>("number" to number.text.toString())
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.one -> {
                speak("1")
                displayNumber("1")
            }

            R.id.two -> {
                speak("2")
                displayNumber("2")
            }

            R.id.three -> {
                speak("3")
                displayNumber("3")
            }

            R.id.four -> {
                speak("4")
                displayNumber("4")
            }

            R.id.five -> {
                speak("5")
                displayNumber("5")
            }

            R.id.six -> {
                speak("6")
                displayNumber("6")
            }

            R.id.seven -> {
                speak("7")
                displayNumber("7")
            }

            R.id.eight -> {
                speak("8")
                displayNumber("8")
            }

            R.id.nine -> {
                speak("9")
                displayNumber("9")
            }

            R.id.zero -> {
                speak("0")
                displayNumber("0")
            }

            R.id.ast -> {
                speak("*")
                displayNumber("*")
            }

            R.id.hash -> {
                speak("#")
                displayNumber("#")
            }

            R.id.backspace -> {
                (activity?.applicationContext as Talk).textToSpeech!!.speak("you cleared ${number.text.last()}",
                        TextToSpeech.QUEUE_FLUSH, null)
                if (number.length() == 1) {
                    number.text = null
                    (activity?.applicationContext as Talk).textToSpeech!!.speak("the number is empty",
                            TextToSpeech.QUEUE_ADD, null)
                } else {
                    val newNo = number.text.subSequence(0, number.length() - 1)
                    number.setText(newNo)
                }
            }
        }
    }

    private fun displayNumber(no: String) {
        val str = number.text
        number.setText("$str$no")
    }

    private fun welcomeSpeak() {
        (activity?.applicationContext as Talk).textToSpeech!!.speak(
                "dialer is open, single click will add the numbers or long press for details",
                TextToSpeech.QUEUE_FLUSH, null)

        (activity?.applicationContext as Talk).textToSpeech!!.speak("you can swipe right for call log",
                TextToSpeech.QUEUE_ADD, null)
    }

    private fun speak(digit: String) {
        (activity?.applicationContext as Talk).textToSpeech!!.speak("you pressed $digit",
                TextToSpeech.QUEUE_FLUSH, null)
    }

    private var numberWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s == null) {
                backspace.visibility = View.GONE
            } else {
                if (s.isEmpty() || s.isBlank()) {
                    backspace.visibility = View.GONE
                } else {
                    backspace.visibility = View.VISIBLE
                }
            }
        }
    }

}
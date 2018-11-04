package com.tobi.voiceassistant.activities

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import com.tobi.voiceassistant.R
import com.tobi.voiceassistant.config.Note
import com.tobi.voiceassistant.config.NoteDatabase
import com.tobi.voiceassistant.config.Talk
import kotlinx.android.synthetic.main.activity_create_note.*
import kotlinx.android.synthetic.main.content_create_note.*
import org.jetbrains.anko.startActivityForResult
import java.text.SimpleDateFormat
import java.util.*

class CreateNote : AppCompatActivity() {

    var noteDatabase: NoteDatabase? = null

    companion object {
        const val NOTE_OPTIONS = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        note_title.setText(intent?.getStringExtra("title"))
        body.setText(intent?.getStringExtra("body"))

        firstSpeak()

        noteDatabase = NoteDatabase.getInstance(applicationContext)

        note_title.setOnLongClickListener { openDialog();true }
        body.setOnLongClickListener { openDialog();true }

        note_title.addTextChangedListener(textWatcher)
        body.addTextChangedListener(textWatcher2)

        note_title.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                ((applicationContext as Talk)).textToSpeech?.speak(
                        "title is focus, and you can now start typing", TextToSpeech.QUEUE_FLUSH, null)
            }
        }

        body.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                ((applicationContext as Talk)).textToSpeech?.speak(
                        "body is focus, and you can now start typing", TextToSpeech.QUEUE_FLUSH, null)
            }
        }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }

    private fun openDialog() {

        var open = true;

        if (note_title.text.isNullOrEmpty() || body.text.isNullOrEmpty()) {
            open = false
        }

        if (!open) {
            ((applicationContext as Talk)).textToSpeech?.speak("cannot open options",
                    TextToSpeech.QUEUE_FLUSH, null)
            if (note_title.text.isNullOrEmpty()) {
                ((applicationContext as Talk)).textToSpeech?.speak("please enter a title",
                        TextToSpeech.QUEUE_ADD, null)
            }

            if (body.text.isNullOrEmpty()) {
                ((applicationContext as Talk)).textToSpeech?.speak("please enter a text in body of the note",
                        TextToSpeech.QUEUE_ADD, null)
            }
        } else {
            startActivityForResult<NoteOptions>(NOTE_OPTIONS, "title" to note_title.text.toString(),
                    "body" to body.text.toString())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == NOTE_OPTIONS) {
                if (intent?.getStringExtra("mode").equals("update"))
                    InsertNote("update").execute(intent?.getIntExtra("id", 0).toString(),
                            note_title.text.toString(), body.text.toString())
                else
                    InsertNote("create").execute(note_title.text.toString(), body.text.toString())
            }
        }
    }

    inner class InsertNote(val mode: String) : AsyncTask<String, Void, Boolean>() {

        override fun doInBackground(vararg params: String): Boolean {
            val date = SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.getDefault()).format(Date())
            val note = Note(0, params[0], params[1], date)

            when (mode) {
                "create" -> noteDatabase?.getNoteDao()?.insert(note)
                "update" -> {
                    noteDatabase?.getNoteDao()?.update(params[0].toInt(), params[1], params[2])
                }
            }

            return true

        }

        override fun onPostExecute(result: Boolean) {
            if (result) {
                goBack()
            }
        }
    }

    private fun goBack() {
        setResult(Activity.RESULT_OK)
        finish()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }

    private fun firstSpeak() {
        ((applicationContext as Talk)).textToSpeech?.speak(
                "note pad is open, for more options long press anywhere on the screen",
                TextToSpeech.QUEUE_FLUSH, null)
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
                ((applicationContext as Talk)).textToSpeech?.speak(
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
                ((applicationContext as Talk)).textToSpeech?.speak(
                        "you deleted $previous", TextToSpeech.QUEUE_FLUSH, null)
                ""
            }
        }
    }

    private fun compare(current: String, previous: String) {
        if (previous.isNotEmpty()) {
            if (current.length < previous.length) {
                ((applicationContext as Talk)).textToSpeech?.speak(
                        "you deleted ${previous.last()}", TextToSpeech.QUEUE_FLUSH, null)
            } else if (current.length > previous.length) {
                ((applicationContext as Talk)).textToSpeech?.speak(
                        "you typed ${current.last()}", TextToSpeech.QUEUE_FLUSH, null)
            }
        } else {
            ((applicationContext as Talk)).textToSpeech?.speak(
                    "you typed ${current.last()}", TextToSpeech.QUEUE_FLUSH, null)
        }
    }
}
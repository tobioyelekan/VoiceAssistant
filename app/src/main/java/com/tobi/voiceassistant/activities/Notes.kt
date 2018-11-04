package com.tobi.voiceassistant.activities

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.tobi.voiceassistant.R
import com.tobi.voiceassistant.adapters.NoteAdapter
import com.tobi.voiceassistant.config.Note
import com.tobi.voiceassistant.config.NoteDatabase
import com.tobi.voiceassistant.config.Talk
import kotlinx.android.synthetic.main.activity_notes.*
import kotlinx.android.synthetic.main.content_notes.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast

class Notes : AppCompatActivity() {

    var noteDatabase: NoteDatabase? = null
    var noteAdapter: NoteAdapter? = null

    companion object {
        const val CREATE_NOTE_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        click.setOnLongClickListener { openCreateNote(); true }

        recycler.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        noteAdapter = NoteAdapter(ArrayList(), { note, mode, position ->
            processMode(note, mode, position)
        }, { openCreateNote() })

        recycler.adapter = noteAdapter

        noteDatabase = NoteDatabase.getInstance(applicationContext)

        GetNotes("getNotes", 0).execute()

    }

    private fun openCreateNote() {
        startActivityForResult<CreateNote>(CREATE_NOTE_REQUEST)
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

    private fun processMode(note: Note, mode: String, position: Int) {
        when (mode) {
            "single" -> speakNote(note)
            "double" -> startActivityForResult<CreateNote>(CREATE_NOTE_REQUEST,
                    "mode" to "update", "id" to note.id, "title" to note.title, "body" to note.body)
            "triple" -> GetNotes("deleteNote", position).execute(note.id.toString())
        }
    }

    private fun speakNote(note: Note) {
        ((applicationContext as Talk)).textToSpeech?.speak(
                "this note was created on ${note.date}", TextToSpeech.QUEUE_FLUSH, null)
        ((applicationContext as Talk)).textToSpeech?.speak(
                "Title of note is ${note.title}", TextToSpeech.QUEUE_ADD, null)
        ((applicationContext as Talk)).textToSpeech?.speak(
                "body of the note says ${note.body}", TextToSpeech.QUEUE_ADD, null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CREATE_NOTE_REQUEST -> GetNotes("getNotes", 0).execute()
            }
        } else {
            GetNotes("getNotes", 0).execute()
        }
    }

    inner class GetNotes(private val mode: String, private val pos: Int) : AsyncTask<String, Void, MutableList<Note>>() {

        override fun doInBackground(vararg params: String): MutableList<Note>? {
            when (mode) {
                "getNotes" -> return noteDatabase?.getNoteDao()?.getNotes()
                "deleteNote" -> {
                    noteDatabase?.getNoteDao()?.delete(params[0].toInt())
                }
            }

            return ArrayList()

        }

        override fun onPostExecute(result: MutableList<Note>) {

            when (mode) {
                "getNotes" -> {
                    progress.visibility = View.GONE
                    if (result.isNotEmpty()) {
                        ((applicationContext as Talk)).textToSpeech?.speak(
                                "note is open, single click on item will read the note," +
                                        "double click will edit the note, triple click will delete the note," +
                                        "or long press to create new note", TextToSpeech.QUEUE_ADD, null)

                        noteAdapter?.addItems(result)
                        empty.visibility = View.GONE
                        recycler.visibility = View.VISIBLE
                    } else {
                        ((applicationContext as Talk)).textToSpeech?.speak(
                                "note is open, there are no notes, long press to create a new note", TextToSpeech.QUEUE_ADD, null)
                        empty.visibility = View.VISIBLE
                    }
                }
                "deleteNote" -> {
                    noteAdapter?.removeItem(pos)
                    ((applicationContext as Talk)).textToSpeech?.speak(
                            "item has been deleted", TextToSpeech.QUEUE_FLUSH, null)

                    if (noteAdapter?.itemCount == 0) {
                        ((applicationContext as Talk)).textToSpeech?.speak(
                                "note is open, there are no notes, long press to create a new note", TextToSpeech.QUEUE_ADD, null)
                        recycler.visibility = View.GONE
                        empty.visibility = View.VISIBLE
                    }
                }
            }

        }
    }
}
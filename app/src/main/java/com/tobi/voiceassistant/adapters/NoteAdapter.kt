package com.tobi.voiceassistant.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.tobi.voiceassistant.R
import com.tobi.voiceassistant.config.DoubleClick
import com.tobi.voiceassistant.config.DoubleClickListener
import com.tobi.voiceassistant.config.Note
import kotlinx.android.synthetic.main.sms_layout.view.*

class NoteAdapter(var notes: MutableList<Note>, val listener: (Note, String, Int) -> Unit,
                  val longClickListener: () -> Unit) : RecyclerView.Adapter<NoteAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteAdapter.ViewHolder =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.sms_layout, parent, false))

    override fun getItemCount(): Int = notes.size

    fun addItems(notes: MutableList<Note>) {
        this.notes = notes
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        notes.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onBindViewHolder(holder: NoteAdapter.ViewHolder, position: Int) {
        val note = notes[position]
        holder.title.text = note.title
        holder.body.text = note.body
        holder.date.text = note.date

        holder.click.setOnClickListener(DoubleClick(object : DoubleClickListener {
            override fun onSingleClick(view: View) {
                listener(note, "single", holder.adapterPosition)
            }

            override fun onDoubleClick(view: View) {
                listener(note, "double", holder.adapterPosition)
            }

            override fun onTripleClick(view: View) {
                listener(note, "triple", holder.adapterPosition)
            }

        }))

        holder.click.setOnLongClickListener { longClickListener(); true }
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val title: TextView = itemView.title
        val body: TextView = itemView.body
        val date: TextView = itemView.date
        val click: LinearLayout = itemView.click
    }
}
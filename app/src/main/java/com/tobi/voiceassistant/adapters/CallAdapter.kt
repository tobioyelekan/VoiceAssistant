package com.tobi.voiceassistant.adapters

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.tobi.voiceassistant.R
import com.tobi.voiceassistant.models.CallData
import kotlinx.android.synthetic.main.call_layout.view.*
import java.text.SimpleDateFormat
import java.util.*

class CallAdapter(val context: Context, var callList: List<CallData>, val listener: (CallData) -> Unit) : RecyclerView.Adapter<CallAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallAdapter.ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.call_layout, parent, false))

    override fun getItemCount(): Int = callList.size

    fun addItems(callList: List<CallData>) {
        this.callList = callList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: CallAdapter.ViewHolder, position: Int) {
        val callData = callList.get(position)

        when (callData.type) {
            "OUTGOING" -> holder.img_type.setImageDrawable(context.resources.getDrawable(R.drawable.outgoing_call))
            "INCOMING" -> holder.img_type.setImageDrawable(context.resources.getDrawable(R.drawable.incoming_call))
            "MISSED" -> holder.img_type.setImageDrawable(context.resources.getDrawable(R.drawable.missed_call))
        }
        holder.title.text = callData.number
        holder.date.text = convertLongToTime(callData.date.toLong())
        holder.duration.text = getDuration(callData.duration.toInt())

        holder.click.setOnClickListener {
            listener(callData)
        }
    }

    private fun getDuration(s: Int): String {
        return if (s / 3600 == 0)
            String.format("%02d:%02d", (s % 3600) / 60, (s % 60))
        else
            String.format("%d:%02d:%02d", s / 3600, (s % 3600) / 60, (s % 60))
    }

    private fun convertLongToTime(time: Long): String {
        val msgDate = Date(time)
        val format = SimpleDateFormat("dd-MMM-yyyy hh:mm a")
        return format.format(msgDate)
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val img_type: ImageView = v.img_type
        val title: TextView = v.title
        val date: TextView = v.date
        val duration: TextView = v.duration
        val click: LinearLayout = v.click
    }
}
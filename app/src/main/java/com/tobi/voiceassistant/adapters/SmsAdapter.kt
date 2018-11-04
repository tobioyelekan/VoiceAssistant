package com.tobi.voiceassistant.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.tobi.voiceassistant.R
import com.tobi.voiceassistant.models.SmsData
import kotlinx.android.synthetic.main.sms_layout.view.*
import java.text.SimpleDateFormat
import java.util.*

class SmsAdapter(var smsList: List<SmsData>, val listener: (SmsData) -> Unit) : RecyclerView.Adapter<SmsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmsAdapter.ViewHolder =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.sms_layout, parent, false))

    fun addItems(smsDat: List<SmsData>) {
        smsList = smsDat
        notifyDataSetChanged()
    }

    override fun getItemCount() = smsList.size

    override fun onBindViewHolder(holder: SmsAdapter.ViewHolder, position: Int) {
        val smsData = smsList.get(position)
        holder.title.text = smsData.address
        holder.body.text = smsData.body
        holder.date.text = convertLongToTime(smsData.date.toLong())

        holder.click.setOnClickListener {
            listener(smsData)
        }

    }

    fun convertLongToTime(time: Long): String {
        val msgDate = Date(time)
        val format = SimpleDateFormat("dd-MMM-yyyy hh:mm a")
        return format.format(msgDate)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title : TextView = itemView.title
        val body: TextView = itemView.body
        val date: TextView = itemView.date
        val click: LinearLayout = itemView.click
    }


}
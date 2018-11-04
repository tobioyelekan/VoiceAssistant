package com.tobi.voiceassistant.adapters

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.tobi.voiceassistant.R
import com.tobi.voiceassistant.models.ContactData
import kotlinx.android.synthetic.main.contact_layout.view.*
import java.util.*
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.ShapeDrawable
import android.os.Build
import android.support.v4.content.ContextCompat
import android.graphics.drawable.shapes.OvalShape


class ContactAdapter(val context: Context, var contactList: List<ContactData>, val listener: (ContactData) -> Unit) : RecyclerView.Adapter<ContactAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactAdapter.ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.contact_layout, parent, false))

    override fun getItemCount(): Int = contactList.size

    fun addItems(contactList: List<ContactData>) {
        this.contactList = contactList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ContactAdapter.ViewHolder, position: Int) {
        val contactData = contactList[position]

        val colors = listOf(R.color.colorBlue, R.color.colorGreen, R.color.colorRed, R.color.colorPurple,
                R.color.darkGrey)
        holder.pre_text.text = contactData.title.first().toString()

        val drawable = ShapeDrawable(OvalShape())

        drawable.paint.color = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.resources.getColor(colors.random(), null)
        } else {
            context.resources.getColor(colors.random())
        }

        holder.pre_text.background = drawable

        holder.title.text = contactData.title
        holder.number.text = contactData.number
        holder.click.setOnClickListener { listener(contactData) }
    }

    fun <E> List<E>.random(): E = if (size > 0) get(Random().nextInt(size)) else get(0)

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val title: TextView = v.title
        val number: TextView = v.number
        val pre_text: TextView = v.pre_text
        val click: LinearLayout = v.click
    }
}
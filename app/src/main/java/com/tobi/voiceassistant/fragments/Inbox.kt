package com.tobi.voiceassistant.fragments

import android.os.AsyncTask
import android.os.Bundle
import android.provider.Telephony
import android.speech.tts.TextToSpeech
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.tobi.voiceassistant.R
import com.tobi.voiceassistant.adapters.SmsAdapter
import com.tobi.voiceassistant.config.Talk
import com.tobi.voiceassistant.models.SmsData
import kotlinx.android.synthetic.main.fragment_message.*

class Inbox : Fragment() {

    var smsAdapter: SmsAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_message, container, false)
        val recycler = view.findViewById(R.id.recycler) as RecyclerView

        smsAdapter = SmsAdapter(ArrayList()) { smsData ->
            speakMessage(smsData);
        }
        recycler.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recycler.adapter = smsAdapter

        return view
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

        if (isVisibleToUser) getInbox()
    }

    private fun speakMessage(smsData: SmsData) {
        ((activity?.applicationContext as Talk)).textToSpeech?.speak("message from ${smsData.address}",
                TextToSpeech.QUEUE_FLUSH, null)

        ((activity?.applicationContext as Talk)).textToSpeech?.speak(
                "on ${smsAdapter?.convertLongToTime(smsData.date.toLong())}", TextToSpeech.QUEUE_ADD, null)

        ((activity?.applicationContext as Talk)).textToSpeech?.speak(smsData.body, TextToSpeech.QUEUE_ADD, null)

    }

    private fun getInbox() {
        LoadSms().execute()
    }

    inner class LoadSms : AsyncTask<String, String, List<SmsData>>() {

        override fun doInBackground(vararg params: String?): List<SmsData>? {

            val smsList: MutableList<SmsData> = ArrayList()

            val cursor = activity?.contentResolver?.query(Telephony.Sms.Inbox.CONTENT_URI, null, null, null,
                    Telephony.Sms.Inbox.DEFAULT_SORT_ORDER)

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    for (i in 1..cursor.count) {
                        val smsData = SmsData(
                                cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)),
                                cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY)),
                                cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE))
                        )

                        smsList.add(smsData)

                        cursor.moveToNext()
                    }
                }

                cursor.close()
                return smsList;
            }

            return null
        }

        override fun onPostExecute(result: List<SmsData>?) {
            when {
                result == null -> {
                    progress.visibility = View.GONE
                    empty.visibility = View.VISIBLE
                    recycler.visibility = View.GONE

                    ((activity?.applicationContext as Talk)).textToSpeech?.speak(
                            "inbox is empty, you can swipe left to see sent message",
                            TextToSpeech.QUEUE_FLUSH, null)
                }
                result.isNotEmpty() -> {
                    progress.visibility = View.GONE
                    empty.visibility = View.GONE
                    smsAdapter?.addItems(result)
                    recycler.visibility = View.VISIBLE

                    ((activity?.applicationContext as Talk)).textToSpeech?.speak(
                            "inbox is open , click on any item and it will speak the details",
                            TextToSpeech.QUEUE_FLUSH, null)

                    ((activity?.applicationContext as Talk)).textToSpeech?.speak(
                            "or swipe left to see sent message",
                            TextToSpeech.QUEUE_ADD, null)
                }
                else -> {
                    progress.visibility = View.GONE
                    empty.visibility = View.VISIBLE
                    recycler.visibility = View.GONE

                    ((activity?.applicationContext as Talk)).textToSpeech?.speak(
                            "inbox is empty, you can swipe left to see sent message",
                            TextToSpeech.QUEUE_FLUSH, null)
                }
            }
        }

    }

}
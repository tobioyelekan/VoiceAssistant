package com.tobi.voiceassistant.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.CallLog
import android.speech.tts.TextToSpeech
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tobi.voiceassistant.R
import com.tobi.voiceassistant.adapters.CallAdapter
import com.tobi.voiceassistant.config.Talk
import com.tobi.voiceassistant.models.CallData
import kotlinx.android.synthetic.main.fragment_message.*
import java.text.SimpleDateFormat
import java.util.*
import android.provider.ContactsContract.PhoneLookup
import android.net.Uri

class CallLogs : Fragment() {

    companion object {
        const val CALL_LOG_PERMISSION = 1
    }

    var callAdapter: CallAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_message, container, false)
        val recycler = view.findViewById(R.id.recycler) as RecyclerView

        callAdapter = CallAdapter(activity?.applicationContext!!, ArrayList()) { callData ->
            speakCall(callData)
        }
        recycler.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recycler.adapter = callAdapter

        return view
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

        if (isVisibleToUser) getCallLog()
    }

    private fun speakCall(callData: CallData) {
        when (callData.type) {
            "OUTGOING" ->
                (activity?.applicationContext as Talk).textToSpeech!!.speak("" +
                        "you called ${callData.number}", TextToSpeech.QUEUE_FLUSH, null)

            "INCOMING" -> (activity?.applicationContext as Talk).textToSpeech!!.speak("" +
                    "${callData.number} called you", TextToSpeech.QUEUE_FLUSH, null)

            "MISSED" -> (activity?.applicationContext as Talk).textToSpeech!!.speak("" +
                    "you missed a call from ${callData.number}", TextToSpeech.QUEUE_FLUSH, null)
        }

        (activity?.applicationContext as Talk).textToSpeech!!.speak("" +
                "on ${convertLongToTime(callData.date.toLong())}", TextToSpeech.QUEUE_ADD, null)

        if (callData.type != "MISSED") {
            (activity?.applicationContext as Talk).textToSpeech!!.speak("" +
                    "the call lasted for ${getDuration(callData.duration.toInt())}", TextToSpeech.QUEUE_ADD, null)
        }
    }

    private fun getDuration(s: Int): String {
        return String.format("%d:%02d:%02d", s / 3600, (s % 3600) / 60, (s % 60))
    }

    private fun convertLongToTime(time: Long): String {
        val msgDate = Date(time)
        val format = SimpleDateFormat("dd-MMM-yyyy hh:mm a")
        return format.format(msgDate)
    }

    private fun getCallLog() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.READ_CALL_LOG) !=
                    PackageManager.PERMISSION_GRANTED) requestPermissions(arrayOf(Manifest.permission.READ_CALL_LOG), CALL_LOG_PERMISSION)
            else LoadCalls().execute()
        } else {
            LoadCalls().execute()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            CALL_LOG_PERMISSION -> {
                (activity?.applicationContext as Talk).textToSpeech!!.speak("" +
                        "permission needed to read call logs", TextToSpeech.QUEUE_FLUSH, null)

                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LoadCalls().execute()
                } else {
                    (activity?.applicationContext as Talk).textToSpeech!!.speak("" +
                            "permission needed to read call logs", TextToSpeech.QUEUE_FLUSH, null)
                }
            }
        }
    }

    inner class LoadCalls : AsyncTask<String, String, List<CallData>>() {

        @SuppressLint("MissingPermission")
        override fun doInBackground(vararg params: String?): List<CallData>? {

            val callList: MutableList<CallData> = ArrayList()

            val cursor = activity?.contentResolver?.query(CallLog.Calls.CONTENT_URI, null, null, null,
                    CallLog.Calls.DATE + " DESC")

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    for (i in 1..cursor.count) {
                        val callData = CallData(
                                getContactName(cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER))),
                                getTypeString(cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.TYPE)))!!,
                                cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.DATE)),
                                cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION))
                        )

                        callList.add(callData)

                        cursor.moveToNext()
                    }
                }

                cursor.close()
                return callList
            }

            return null
        }

        private fun getContactName(number: String): String? {
            val cr = context?.contentResolver
            val uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number))
            val cursor = cr?.query(uri, arrayOf(PhoneLookup.DISPLAY_NAME), null, null, null)
                    ?: return null
            var contactName: String? = null
            if (cursor.moveToFirst()) {
                contactName = cursor.getString(cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME))
            }

            if (!cursor.isClosed) {
                cursor.close()
            }

            if (contactName == null) return number

            return contactName

//            var phone = ""
//            val cr = activity?.contentResolver
//
//            val cp = cr?.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
//                    ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?", arrayOf(number), null)
//
//            if (cp != null && cp.moveToFirst()) {
//                phone = cp.getString(cp.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
//                cp.close()
//            }
//
//            return if (phone.isEmpty())
//                number else phone

        }

        private fun getTypeString(type: String): String? {
            when (type.toInt()) {
                CallLog.Calls.OUTGOING_TYPE -> return "OUTGOING"

                CallLog.Calls.INCOMING_TYPE -> return "INCOMING"

                CallLog.Calls.MISSED_TYPE -> return "MISSED"
            }

            return null
        }

        override fun onPostExecute(result: List<CallData>?) {
            when {
                result == null -> {
                    progress.visibility = View.GONE
                    empty.visibility = View.VISIBLE
                    recycler.visibility = View.GONE

                    ((activity?.applicationContext as Talk)).textToSpeech?.speak(
                            "call log is empty, you can swipe right for the dialer",
                            TextToSpeech.QUEUE_FLUSH, null)
                }
                result.isNotEmpty() -> {
                    progress.visibility = View.GONE
                    empty.visibility = View.GONE
                    callAdapter?.addItems(result)
                    recycler.visibility = View.VISIBLE

                    ((activity?.applicationContext as Talk)).textToSpeech?.speak(
                            "call log is open , click on any item and it will speak the details",
                            TextToSpeech.QUEUE_FLUSH, null)

                    ((activity?.applicationContext as Talk)).textToSpeech?.speak(
                            "or swipe right for the dialer",
                            TextToSpeech.QUEUE_ADD, null)
                }
                else -> {
                    progress.visibility = View.GONE
                    empty.visibility = View.VISIBLE
                    recycler.visibility = View.GONE

                    ((activity?.applicationContext as Talk)).textToSpeech?.speak(
                            "call log is empty, you can swipe right for the dialer",
                            TextToSpeech.QUEUE_FLUSH, null)
                }
            }
        }

    }

}
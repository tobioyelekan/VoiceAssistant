package com.tobi.voiceassistant.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.CallLog
import android.provider.ContactsContract
import android.speech.tts.TextToSpeech
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tobi.voiceassistant.R
import com.tobi.voiceassistant.activities.MainActivity
import com.tobi.voiceassistant.adapters.ContactAdapter
import com.tobi.voiceassistant.config.Talk
import com.tobi.voiceassistant.models.CallData
import com.tobi.voiceassistant.models.ContactData
import kotlinx.android.synthetic.main.fragment_message.*
import java.text.SimpleDateFormat
import java.util.*

class Contact : Fragment() {

    var contactAdapter: ContactAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_message, container, false)
        val recycler = view.findViewById(R.id.recycler) as RecyclerView

        contactAdapter = ContactAdapter(activity?.applicationContext!!, ArrayList()) { contactData ->
            speakCall(contactData)
        }
        recycler.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recycler.adapter = contactAdapter

        return view
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

        if (isVisibleToUser) getContacts()
    }

    private fun getContacts() {
        LoadContacts().execute()
    }

    private fun speakCall(contactData: ContactData) {
        ((activity?.applicationContext as Talk)).textToSpeech?.speak(
                contactData.title, TextToSpeech.QUEUE_FLUSH, null)

        ((activity?.applicationContext as Talk)).textToSpeech?.speak(
                contactData.number, TextToSpeech.QUEUE_ADD, null)
    }

    inner class LoadContacts : AsyncTask<String, String, List<ContactData>>() {

        override fun doInBackground(vararg params: String?): List<ContactData>? {

            val contactList: MutableList<ContactData> = ArrayList()

            val FILTER = ContactsContract.Contacts.DISPLAY_NAME + " NOT LIKE '%@%'"

            val ORDER = String.format("%1\$s COLLATE NOCASE", ContactsContract.Contacts.DISPLAY_NAME)

            @SuppressLint("InlinedApi")
            val PROJECTION = arrayOf(ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts.HAS_PHONE_NUMBER)

            val cr = activity?.contentResolver
            val cursor = cr?.query(ContactsContract.Contacts.CONTENT_URI, PROJECTION, FILTER, null, ORDER)

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                    val name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    val hasPhone = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))

                    var phone = ""

                    if (hasPhone > 0) {
                        val cp = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", arrayOf(id), null)

                        if (cp != null && cp.moveToFirst()) {
                            phone = cp.getString(cp.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            cp.close()
                        }
                    }

                    if (!phone.isBlank()) {
                        contactList.add(ContactData(id, name, phone))
                    }


                } while (cursor.moveToNext())

                cursor.close()

                return contactList
            }

            return null
        }

//        private fun getContactName(number: String): String? {
//            val cr = context?.contentResolver
//            val uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number))
//            val cursor = cr?.query(uri, arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME), null, null, null)
//                    ?: return null
//            var contactName: String? = null
//            if (cursor.moveToFirst()) {
//                contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME))
//            }
//
//            if (!cursor.isClosed) {
//                cursor.close()
//            }
//
//            if (contactName == null) return number
//
//            return contactName
//        }

        override fun onPostExecute(result: List<ContactData>?) {
            when {
                result == null -> {
                    progress.visibility = View.GONE
                    empty.visibility = View.VISIBLE
                    recycler.visibility = View.GONE

                    ((activity?.applicationContext as Talk)).textToSpeech?.speak(
                            "contact is empty, you can swipe left for call log",
                            TextToSpeech.QUEUE_FLUSH, null)
                }
                result.isNotEmpty() -> {
                    progress.visibility = View.GONE
                    empty.visibility = View.GONE
                    contactAdapter?.addItems(result)
                    recycler.visibility = View.VISIBLE

                    ((activity?.applicationContext as Talk)).textToSpeech?.speak(
                            "contact is open, single click on item will speak the contact",
                            TextToSpeech.QUEUE_FLUSH, null)

                    ((activity?.applicationContext as Talk)).textToSpeech?.speak(
                            "you can swipe left for call log",
                            TextToSpeech.QUEUE_ADD, null)

                }
                else -> {
                    progress.visibility = View.GONE
                    empty.visibility = View.VISIBLE
                    recycler.visibility = View.GONE

                    ((activity?.applicationContext as Talk)).textToSpeech?.speak(
                            "contact is empty, you can swipe left for call log",
                            TextToSpeech.QUEUE_FLUSH, null)
                }
            }
        }

    }

}
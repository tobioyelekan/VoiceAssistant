package com.tobi.voiceassistant.activities

import android.os.Bundle
import android.provider.CallLog
import android.provider.ContactsContract
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.tobi.voiceassistant.R
import com.tobi.voiceassistant.config.Talk
import com.tobi.voiceassistant.fragments.CallLogs
import com.tobi.voiceassistant.fragments.Contact
import com.tobi.voiceassistant.fragments.Dialer
import kotlinx.android.synthetic.main.activity_call_details.*

class CallDetails : AppCompatActivity() {
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_details)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        container.adapter = mSectionsPagerAdapter
        container.offscreenPageLimit = 0

        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment? {
            when (position) {
                0 -> return Dialer()
                1 -> return CallLogs()
                2 -> return Contact()
            }

            return null
        }

        override fun getCount(): Int = 3
    }

}

package com.tobi.voiceassistant.config

import android.view.View

interface DoubleClickListener {

    /**
     * Called when the user make a single click.
     */
    fun onSingleClick(view: View)

    /**
     * Called when the user make a double click.
     */
    fun onDoubleClick(view: View)

    fun onTripleClick(view: View)
}
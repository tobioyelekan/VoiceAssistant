package com.tobi.voiceassistant.config

import android.os.Handler
import android.view.View

open class DoubleClick
/**
 * Builds a DoubleClick.
 *
 * @param doubleClickListener the click listener to notify clicks.
 */
(/*
   * Click callback.
   */
        private val doubleClickListener: DoubleClickListener) : View.OnClickListener {

    /*
   * Handler to process click event.
   */
    private val mHandler = Handler()

    /*
   * Number of clicks in @DOUBLE_CLICK_INTERVAL interval.
   */
    private var clicks: Int = 0

    /*
   * Flag to check if click handler is busy.
   */
    private var isBusy = false

    override fun onClick(view: View) {

        if (!isBusy) {
            //  Prevent multiple click in this short time
            isBusy = true

            // Increase clicks count
            clicks++

            mHandler.postDelayed({

                if (clicks == 3) {
                    doubleClickListener.onTripleClick(view)
                }

                if (clicks == 2) {  // Double tap.
                    doubleClickListener.onDoubleClick(view)
                }

                if (clicks == 1) {  // Single tap
                    doubleClickListener.onSingleClick(view)
                }

                // we need to  restore clicks count
                clicks = 0
            }, DOUBLE_CLICK_INTERVAL)
            isBusy = false
        }

    }

    companion object {

        /*
   * Duration of click interval.
   * 200 milliseconds is a best fit to double click interval.
   */
        private const val DOUBLE_CLICK_INTERVAL = 500L  // Time to wait the second click.
    }
}
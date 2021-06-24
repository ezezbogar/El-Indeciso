package com.example.el_indeciso

import android.view.View

abstract class DoubleClickListener : View.OnClickListener {
    private var last_click_time: Long = 0
    private val DOUBLE_CLICK_TIME_DELTA: Long = 300 //milliseconds

    override fun onClick(v: View) {
        val click_time = System.currentTimeMillis()
        if (click_time - last_click_time < DOUBLE_CLICK_TIME_DELTA) {
            onDoubleClick(v)
            last_click_time = 0
        }
        last_click_time = click_time
    }
    abstract fun onDoubleClick(v: View)
}
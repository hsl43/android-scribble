package com.labs2160.scribble

import android.support.v4.view.MotionEventCompat
import android.view.MotionEvent
import android.view.View

fun View.centerY(motionEvent: MotionEvent) {
    y = MotionEventCompat.getY(motionEvent, motionEvent.actionIndex) - (height / 2)
}
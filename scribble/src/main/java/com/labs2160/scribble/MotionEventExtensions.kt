package com.labs2160.scribble

import android.support.v4.view.MotionEventCompat
import android.view.MotionEvent

fun MotionEvent.compatAction() = MotionEventCompat.getActionMasked(this)

fun MotionEvent.compatPointerId() = MotionEventCompat.getPointerId(this, this.actionIndex)

fun MotionEvent.compatPointerCount() = MotionEventCompat.getPointerCount(this)

fun MotionEvent.compatX(pointerId: Int) = MotionEventCompat.getX(this, MotionEventCompat.findPointerIndex(this, pointerId))

fun MotionEvent.compatY(pointerId: Int) = MotionEventCompat.getY(this, MotionEventCompat.findPointerIndex(this, pointerId))
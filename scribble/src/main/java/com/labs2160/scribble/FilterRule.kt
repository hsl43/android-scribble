package com.labs2160.scribble

import android.view.MotionEvent

interface FilterRule {
    enum class Result { Success, Failure, Skipped }

    fun apply(motionEvent: MotionEvent): Result
}
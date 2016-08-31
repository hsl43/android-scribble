package com.labs2160.scribble

import android.view.MotionEvent

class WindowPositionFilterRule : FilterRule {
    override fun apply(motionEvent: MotionEvent) = FilterRule.Result.Success
}
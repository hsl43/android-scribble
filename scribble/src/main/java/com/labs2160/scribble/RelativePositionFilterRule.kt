package com.labs2160.scribble

import android.view.MotionEvent

class RelativePositionFilterRule : FilterRule {
    override fun apply(motionEvent: MotionEvent) = FilterRule.Result.Success
}
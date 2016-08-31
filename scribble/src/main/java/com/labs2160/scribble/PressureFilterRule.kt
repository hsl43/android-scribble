package com.labs2160.scribble

import android.view.MotionEvent

class PressureFilterRule(private val min: Float = .0F,
                         private val max: Float = .8F) : FilterRule {

    override fun apply(motionEvent: MotionEvent) = if(motionEvent.pressure in min..max) FilterRule.Result.Success else FilterRule.Result.Failure
}
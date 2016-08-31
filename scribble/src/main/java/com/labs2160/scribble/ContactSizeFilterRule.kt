package com.labs2160.scribble

import android.view.MotionEvent

class ContactSizeFilterRule(private val min: Float = 0F,
                            private val max: Float = .15F) : FilterRule {

    override fun apply(motionEvent: MotionEvent) = if(motionEvent.size in min..max) FilterRule.Result.Success else FilterRule.Result.Failure
}
package com.labs2160.scribble

import android.view.MotionEvent

class ContactPressureFilterRule(private val min: Float = .0F,
                                private val max: Float = 1.0F) : FilterRule {

    override fun apply(motionEvent: MotionEvent): FilterRule.Result {
        val result = if(motionEvent.pressure in min..max) FilterRule.Result.Success else FilterRule.Result.Failure

//        Log.d(javaClass.name, "## [${javaClass.name}] pressure == ${motionEvent.pressure} => $result")

        return result
    }
}
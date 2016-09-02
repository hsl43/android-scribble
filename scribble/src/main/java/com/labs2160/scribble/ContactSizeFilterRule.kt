package com.labs2160.scribble

import android.view.MotionEvent

class ContactSizeFilterRule(private val min: Float = 0F,
                            private val max: Float = .15F) : FilterRule {

    override fun apply(motionEvent: MotionEvent): FilterRule.Result {
        val result = if(motionEvent.size in min..max) FilterRule.Result.Success else FilterRule.Result.Failure

//        Log.d(javaClass.name, "## [${javaClass.name}] size == ${motionEvent.size} => $result")

        return result
    }
}
package com.labs2160.scribble

import android.view.MotionEvent

class ChainFilterRule(private val rules: Collection<FilterRule>) : FilterRule {
    override fun apply(context: MotionEvent): FilterRule.Result {
        var result = FilterRule.Result.Skipped

        if(rules.isNotEmpty()) {
            for(rule in rules) {
                result = rule.apply(context)

                if(result == FilterRule.Result.Failure) {
                    break
                }
            }
        }

        return result
    }
}
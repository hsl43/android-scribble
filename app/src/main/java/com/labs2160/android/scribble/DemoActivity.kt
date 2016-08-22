package com.labs2160.android.scribble

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.labs2160.scribble.ScribbleView

class DemoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(ScribbleView(this))
    }
}
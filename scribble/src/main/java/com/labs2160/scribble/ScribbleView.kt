package com.labs2160.scribble

import android.content.Context
import android.widget.TextView

class ScribbleView(context: Context): TextView(context) {
    init {
        text = "Hello, from ScribbleView!"
    }
}
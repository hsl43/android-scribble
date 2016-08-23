package com.labs2160.android.scribble

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.labs2160.scribble.ScribbleView

class ScribbleDemoActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var scribbleView: ScribbleView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.scribble_demo_activity)

        toolbar      = findViewById(R.id.scribble_demo_activity_toolbar)       as Toolbar
        scribbleView = findViewById(R.id.scribble_demo_activity_scribble_view) as ScribbleView

        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.scribble_demo_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.scribble_demo_activity_clear -> {
                scribbleView.clear()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }
}
package com.festivalwidget

import android.app.Activity
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/** Tiny launcher screen: upcoming festivals + how to add a widget. Built in code (no layout inflation). */
class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dp = resources.displayMetrics.density
        val pad = (20 * dp).toInt()

        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setPadding(pad, pad * 2, pad, pad)

        root.addView(text(getString(R.string.app_name), 28f, true, 0xFF111111.toInt()))
        root.addView(text(getString(R.string.hint), 14f, false, 0xFF555555.toInt(), (8 * dp).toInt()))
        root.addView(text(getString(R.string.upcoming), 16f, true, 0xFFE65100.toInt(), (24 * dp).toInt()))

        val fmt = SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault())
        for (f in Festivals.upcoming(this, 15)) {
            val c = Calendar.getInstance()
            c.add(Calendar.DAY_OF_YEAR, f.days)
            root.addView(text(f.name, 18f, true, 0xFF111111.toInt(), (14 * dp).toInt()))
            root.addView(text(fmt.format(c.time), 13f, false, 0xFF777777.toInt()))
        }

        val scroll = ScrollView(this)
        scroll.addView(root)
        setContentView(scroll)

        Updater.updateAll(this) // re-calibrate widgets whenever the app is opened
    }

    private fun text(s: String, sp: Float, bold: Boolean, color: Int, top: Int = 0): TextView {
        val t = TextView(this)
        t.text = s
        t.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp)
        t.setTextColor(color)
        if (bold) t.setTypeface(Typeface.DEFAULT_BOLD)
        t.setPadding(0, top, 0, 0)
        return t
    }
}

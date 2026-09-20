package com.wordguessing.game

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class GameSetupActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val names = intent.getStringArrayListExtra("playerNames") ?: arrayListOf()

        val textView = TextView(this)
        textView.textSize = 24f
        textView.setPadding(24, 24, 24, 24)

        textView.text = "Players:\n\n" + names.joinToString("\n")

        setContentView(textView)
    }
}

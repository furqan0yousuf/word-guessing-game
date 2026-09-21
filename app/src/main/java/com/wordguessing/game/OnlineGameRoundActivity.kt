package com.wordguessing.game

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.TextView

class OnlineGameRoundActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val text = TextView(this)

        text.text = "ONLINE GAME STARTED"

        text.textSize = 28f

        text.setTextColor(Color.BLACK)

        text.gravity = Gravity.CENTER

        setContentView(text)
    }
}

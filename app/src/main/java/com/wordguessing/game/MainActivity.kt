package com.wordguessing.game

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = android.widget.LinearLayout(this)
        layout.orientation = android.widget.LinearLayout.VERTICAL
        layout.gravity = android.view.Gravity.CENTER
        layout.setPadding(24, 24, 24, 24)

        val title = android.widget.TextView(this)
        title.text = "Word Guessing Game"
        title.textSize = 28f
        title.gravity = android.view.Gravity.CENTER

        layout.addView(title)

        val singleButton = Button(this)
        singleButton.text = "Single Player"
        singleButton.setOnClickListener {
            startActivity(Intent(this, SinglePlayerSetupActivity::class.java))
        }
        layout.addView(singleButton)

        val localButton = Button(this)
        localButton.text = "Local Multiplayer"
        localButton.setOnClickListener {
            startActivity(Intent(this, PlayerSetupActivity::class.java))
        }
        layout.addView(localButton)

        val onlineButton = Button(this)
        onlineButton.text = "Online Multiplayer"
        onlineButton.setOnClickListener {
            startActivity(Intent(this, OnlineGameActivity::class.java))
        }
        layout.addView(onlineButton)

        setContentView(layout)
    }
}

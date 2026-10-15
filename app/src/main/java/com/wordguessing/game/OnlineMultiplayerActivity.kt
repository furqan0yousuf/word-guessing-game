package com.wordguessing.game

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class OnlineMultiplayerActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createScreen()
    }

    private fun createScreen() {
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.gravity = Gravity.CENTER
        layout.setPadding(24, 24, 24, 24)

        // TITLE
        val title = TextView(this)
        title.text = "Online Multiplayer"
        title.textSize = 28f
        title.gravity = Gravity.CENTER
        layout.addView(title)

        // CREATE A GAME
        val createButton = Button(this)
        createButton.text = "Create a Game"
        createButton.textSize = 18f

        createButton.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    CreateGameActivity::class.java
                )
            )
        }

        layout.addView(createButton)

        // JOIN A GAME
        val joinButton = Button(this)
        joinButton.text = "Join a Game"
        joinButton.textSize = 18f

        joinButton.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    JoinGameActivity::class.java
                )
            )
        }

        layout.addView(joinButton)

        // BROWSE PUBLIC GAMES
        val publicButton = Button(this)
        publicButton.text = "Browse Public Games"
        publicButton.textSize = 18f

        publicButton.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    JoinGameActivity::class.java
                )
            )
        }

        layout.addView(publicButton)

        setContentView(layout)
    }
}

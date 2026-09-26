package com.wordguessing.game

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val singlePlayerButton =
            findViewById<Button>(resources.getIdentifier(
                "singlePlayerButton",
                "id",
                packageName
            ))

        val localGameButton =
            findViewById<Button>(resources.getIdentifier(
                "localGameButton",
                "id",
                packageName
            ))

        val onlineGameButton =
            findViewById<Button>(resources.getIdentifier(
                "onlineGameButton",
                "id",
                packageName
            ))

        singlePlayerButton.setOnClickListener {
            startActivity(
                Intent(this, SinglePlayerActivity::class.java)
            )
        }

        localGameButton.setOnClickListener {
            startActivity(
                Intent(this, PlayerSetupActivity::class.java)
            )
        }

        onlineGameButton.setOnClickListener {
            startActivity(
                Intent(this, OnlineGameActivity::class.java)
            )
        }
    }
}

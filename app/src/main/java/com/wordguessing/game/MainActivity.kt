package com.wordguessing.game

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.singlePlayerButton).setOnClickListener {
            startActivity(
                Intent(this, SinglePlayerActivity::class.java)
            )
        }

        findViewById<Button>(R.id.localGameButton).setOnClickListener {
            startActivity(
                Intent(this, PlayerSetupActivity::class.java)
            )
        }

        findViewById<Button>(R.id.onlineGameButton).setOnClickListener {
            startActivity(
                Intent(this, OnlineGameActivity::class.java)
            )
        }
    }
}

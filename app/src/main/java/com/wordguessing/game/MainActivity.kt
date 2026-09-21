package com.wordguessing.game

import android.app.Activity
import android.content.Intent
import android.os.Bundle

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<android.widget.Button>(
            R.id.localGameButton
        ).setOnClickListener {

            val intent =
                Intent(
                    this,
                    PlayerSetupActivity::class.java
                )

            startActivity(intent)
        }

        findViewById<android.widget.Button>(
            R.id.onlineGameButton
        ).setOnClickListener {

            val intent =
                Intent(
                    this,
                    OnlineGameActivity::class.java
                )

            startActivity(intent)
        }
    }
}

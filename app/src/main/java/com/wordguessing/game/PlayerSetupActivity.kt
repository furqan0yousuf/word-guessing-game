package com.wordguessing.game

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class PlayerSetupActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_setup)

        findViewById<Button>(R.id.twoPlayersButton).setOnClickListener {
            Toast.makeText(this, "2 Players selected", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.threePlayersButton).setOnClickListener {
            Toast.makeText(this, "3 Players selected", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.fourPlayersButton).setOnClickListener {
            Toast.makeText(this, "4 Players selected", Toast.LENGTH_SHORT).show()
        }
    }
}

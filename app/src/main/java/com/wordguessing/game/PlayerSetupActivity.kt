package com.wordguessing.game

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView

class PlayerSetupActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_setup)

        val twoPlayersButton = findViewById<Button>(R.id.twoPlayersButton)
        val threePlayersButton = findViewById<Button>(R.id.threePlayersButton)
        val fourPlayersButton = findViewById<Button>(R.id.fourPlayersButton)

        twoPlayersButton.setOnClickListener {
            showNameFields(2)
        }

        threePlayersButton.setOnClickListener {
            showNameFields(3)
        }

        fourPlayersButton.setOnClickListener {
            showNameFields(4)
        }
    }

    private fun showNameFields(playerCount: Int) {

        val layout = findViewById<LinearLayout>(R.id.playerSetupLayout)

        twoPlayersButtonVisibility(false)

        for (i in 1..playerCount) {

            val label = TextView(this)
            label.text = "Player $i"
            label.textSize = 18f

            val nameInput = EditText(this)
            nameInput.hint = "Enter player $i name"
            nameInput.tag = "player$i"

            layout.addView(label)
            layout.addView(nameInput)
        }

        val continueButton = Button(this)
        continueButton.text = "Continue"
        layout.addView(continueButton)

        continueButton.setOnClickListener {
            val names = ArrayList<String>()

            for (i in 1..playerCount) {
                val input = layout.findViewWithTag<EditText>("player$i")
                val name = input.text.toString().trim()

                if (name.isEmpty()) {
                    input.error = "Enter a name"
                    return@setOnClickListener
                }

                names.add(name)
            }

            val intent = Intent(this, GameSetupActivity::class.java)
            intent.putStringArrayListExtra("playerNames", names)
            startActivity(intent)
        }
    }

    private fun twoPlayersButtonVisibility(show: Boolean) {
        findViewById<Button>(R.id.twoPlayersButton).visibility =
            if (show) Button.VISIBLE else Button.GONE

        findViewById<Button>(R.id.threePlayersButton).visibility =
            if (show) Button.VISIBLE else Button.GONE

        findViewById<Button>(R.id.fourPlayersButton).visibility =
            if (show) Button.VISIBLE else Button.GONE
    }
}

package com.wordguessing.game

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class JoinGameActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)

        layout.orientation =
            LinearLayout.VERTICAL

        layout.setPadding(
            24,
            24,
            24,
            24
        )

        val title = TextView(this)

        title.text =
            "Join Game"

        title.textSize =
            28f

        title.gravity =
            Gravity.CENTER

        title.setTextColor(
            Color.BLACK
        )

        layout.addView(title)

        // GAME CODE
        val codeTitle =
            TextView(this)

        codeTitle.text =
            "Game Code"

        codeTitle.textSize =
            20f

        codeTitle.setPadding(
            0,
            30,
            0,
            10
        )

        layout.addView(codeTitle)

        val codeInput =
            EditText(this)

        codeInput.hint =
            "Enter 6-digit game code"

        codeInput.textSize =
            20f

        codeInput.setSingleLine(true)

        codeInput.inputType =
            android.text.InputType.TYPE_CLASS_NUMBER

        layout.addView(codeInput)

        // PASSWORD
        val passwordTitle =
            TextView(this)

        passwordTitle.text =
            "Password (if required)"

        passwordTitle.textSize =
            20f

        passwordTitle.setPadding(
            0,
            30,
            0,
            10
        )

        layout.addView(passwordTitle)

        val passwordInput =
            EditText(this)

        passwordInput.hint =
            "Enter password"

        passwordInput.textSize =
            20f

        passwordInput.setSingleLine(true)

        layout.addView(passwordInput)

        // PLAYER NAME
        val nameTitle =
            TextView(this)

        nameTitle.text =
            "Your Name"

        nameTitle.textSize =
            20f

        nameTitle.setPadding(
            0,
            30,
            0,
            10
        )

        layout.addView(nameTitle)

        val nameInput =
            EditText(this)

        nameInput.hint =
            "Enter your name"

        nameInput.textSize =
            20f

        nameInput.setSingleLine(true)

        layout.addView(nameInput)

        // JOIN BUTTON
        val joinButton =
            Button(this)

        joinButton.text =
            "Join Game"

        joinButton.textSize =
            18f

        layout.addView(
            joinButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        joinButton.setOnClickListener {

            val gameCode =
                codeInput.text
                    .toString()
                    .trim()

            val playerName =
                nameInput.text
                    .toString()
                    .trim()

            if (gameCode.isEmpty()) {

                Toast.makeText(
                    this,
                    "Please enter the game code.",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (gameCode.length != 6) {

                Toast.makeText(
                    this,
                    "Game code must be 6 digits.",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (playerName.isEmpty()) {

                Toast.makeText(
                    this,
                    "Please enter your name.",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            /*
             * Temporary local waiting-room test.
             *
             * Firebase will later verify:
             * - game code
             * - password
             * - available player slots
             * - game settings
             */

            val intent =
                Intent(
                    this,
                    GameWaitingActivity::class.java
                )

            intent.putExtra(
                "gameCode",
                gameCode
            )

            intent.putExtra(
                "playerCount",
                4
            )

            intent.putExtra(
                "wordSelection",
                "Random Word"
            )

            intent.putExtra(
                "category",
                "Random"
            )

            intent.putExtra(
                "secondsPerTurn",
                10
            )

            intent.putExtra(
                "nextWordMaster",
                "Winner becomes Word Master"
            )

            intent.putExtra(
                "playerName",
                playerName
            )

            startActivity(intent)
        }

        setContentView(layout)
    }
}

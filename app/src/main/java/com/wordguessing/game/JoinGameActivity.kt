package com.wordguessing.game

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class JoinGameActivity : Activity {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(24, 24, 24, 24)

        val title = TextView(this)
        title.text = "Join Game"
        title.textSize = 28f
        title.gravity = Gravity.CENTER
        title.setTextColor(Color.BLACK)

        layout.addView(title)

        val codeTitle = TextView(this)
        codeTitle.text = "Game Code"
        codeTitle.textSize = 20f
        codeTitle.setPadding(0, 30, 0, 10)

        layout.addView(codeTitle)

        val codeInput = EditText(this)
        codeInput.hint = "Enter game code"
        codeInput.textSize = 20f
        codeInput.setSingleLine(true)

        layout.addView(codeInput)

        val passwordTitle = TextView(this)
        passwordTitle.text = "Password (if required)"
        passwordTitle.textSize = 20f
        passwordTitle.setPadding(0, 30, 0, 10)

        layout.addView(passwordTitle)

        val passwordInput = EditText(this)
        passwordInput.hint = "Enter password"
        passwordInput.textSize = 20f
        passwordInput.setSingleLine(true)

        layout.addView(passwordInput)

        val joinButton = Button(this)
        joinButton.text = "Join Game"
        joinButton.textSize = 18f

        layout.addView(
            joinButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        joinButton.setOnClickListener {

            val gameCode =
                codeInput.text.toString().trim()

            if (gameCode.isEmpty()) {

                Toast.makeText(
                    this,
                    "Please enter a game code.",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            Toast.makeText(
                this,
                "Game code accepted. Online connection will be added later.",
                Toast.LENGTH_LONG
            ).show()
        }

        setContentView(layout)
    }
}

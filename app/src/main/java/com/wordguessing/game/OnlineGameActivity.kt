package com.wordguessing.game

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class OnlineGameActivity : Activity {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(24, 24, 24, 24)

        val title = TextView(this)
        title.text = "Online Game"
        title.textSize = 28f
        title.gravity = Gravity.CENTER
        title.setTextColor(Color.BLACK)

        layout.addView(title)

        val createButton = Button(this)
        createButton.text = "Create Game"
        createButton.textSize = 18f

        layout.addView(
            createButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

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

        val rulesButton = Button(this)
        rulesButton.text = "Game Rules"
        rulesButton.textSize = 18f

        layout.addView(
            rulesButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        createButton.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    CreateGameActivity::class.java
                )
            )
        }

        joinButton.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    JoinGameActivity::class.java
                )
            )
        }

        rulesButton.setOnClickListener {
            showRules()
        }

        setContentView(layout)
    }

    private fun showRules() {

        android.app.AlertDialog.Builder(this)
            .setTitle("Online Game Rules")
            .setMessage(
                """
                • 2 to 4 players

                • Host creates the game

                • Players join using a game code

                • Random Word:
                  Everyone plays.

                • Manual Word:
                  The Word Master chooses the word.
                  The Word Master does not play that round.

                • The player who guesses the whole word wins the round.

                • The host can choose the game settings.

                • The next Word Master can be selected according to the game settings.
                """.trimIndent()
            )
            .setPositiveButton("OK", null)
            .show()
    }
}

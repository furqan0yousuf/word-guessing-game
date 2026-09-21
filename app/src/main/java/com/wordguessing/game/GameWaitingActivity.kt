package com.wordguessing.game

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class GameWaitingActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gameCode =
            intent.getStringExtra("gameCode")
                ?: "------"

        val playerCount =
            intent.getIntExtra(
                "playerCount",
                2
            )

        val wordSelection =
            intent.getStringExtra(
                "wordSelection"
            )
                ?: "Random Word"

        val category =
            intent.getStringExtra(
                "category"
            )
                ?: "Random"

        val secondsPerTurn =
            intent.getIntExtra(
                "secondsPerTurn",
                10
            )

        val nextWordMaster =
            intent.getStringExtra(
                "nextWordMaster"
            )
                ?: "Same Word Master"

        val playerName =
            intent.getStringExtra(
                "playerName"
            )
                ?: "Player"

        val isHost =
            intent.getBooleanExtra(
                "isHost",
                true
            )

        val layout =
            LinearLayout(this)

        layout.orientation =
            LinearLayout.VERTICAL

        layout.gravity =
            Gravity.CENTER_HORIZONTAL

        layout.setPadding(
            24,
            24,
            24,
            24
        )

        val title =
            TextView(this)

        title.text =
            "Game Waiting Room"

        title.textSize =
            28f

        title.gravity =
            Gravity.CENTER

        title.setTypeface(
            null,
            Typeface.BOLD
        )

        title.setTextColor(
            Color.BLACK
        )

        title.setPadding(
            0,
            0,
            0,
            25
        )

        layout.addView(title)

        // GAME CODE
        val codeText =
            TextView(this)

        codeText.text =
            "Game Code\n$gameCode"

        codeText.textSize =
            26f

        codeText.gravity =
            Gravity.CENTER

        codeText.setTypeface(
            null,
            Typeface.BOLD
        )

        codeText.setPadding(
            0,
            10,
            0,
            10
        )

        layout.addView(codeText)

        // COPY GAME CODE
        val copyButton =
            Button(this)

        copyButton.text =
            "Copy Game Code"

        copyButton.textSize =
            18f

        layout.addView(
            copyButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        copyButton.setOnClickListener {

            val clipboard =
                getSystemService(
                    Context.CLIPBOARD_SERVICE
                ) as ClipboardManager

            val clip =
                ClipData.newPlainText(
                    "Game Code",
                    gameCode
                )

            clipboard.setPrimaryClip(clip)

            Toast.makeText(
                this,
                "Game code copied.",
                Toast.LENGTH_SHORT
            ).show()
        }

        // PLAYERS
        val playersTitle =
            TextView(this)

        playersTitle.text =
            "Players"

        playersTitle.textSize =
            22f

        playersTitle.setTypeface(
            null,
            Typeface.BOLD
        )

        playersTitle.setPadding(
            0,
            25,
            0,
            10
        )

        layout.addView(playersTitle)

        val role =
            if (isHost) {
                "Host"
            } else {
                "Player"
            }

        val playersText =
            TextView(this)

        playersText.text =
            "Players joined: 1 of $playerCount\n\n1. $playerName — $role"

        playersText.textSize =
            18f

        playersText.setPadding(
            0,
            5,
            0,
            20
        )

        layout.addView(playersText)

        // GAME RULES
        val rulesTitle =
            TextView(this)

        rulesTitle.text =
            "Game Rules"

        rulesTitle.textSize =
            22f

        rulesTitle.setTypeface(
            null,
            Typeface.BOLD
        )

        rulesTitle.setPadding(
            0,
            10,
            0,
            10
        )

        layout.addView(rulesTitle)

        val rulesText =
            TextView(this)

        val wordMasterText =
            if (
                wordSelection ==
                "Random Word"
            ) {
                "Word Master: None\nEveryone plays"
            } else {
                "Word Master: Host\nWord Master does not play"
            }

        rulesText.text =
            """
            Word Selection: $wordSelection

            Category: $category

            Seconds per turn: $secondsPerTurn

            $wordMasterText

            Next Word Master:
            $nextWordMaster
            """.trimIndent()

        rulesText.textSize =
            18f

        rulesText.setPadding(
            0,
            10,
            0,
            20
        )

        layout.addView(rulesText)

        // STATUS
        val statusText =
            TextView(this)

        if (isHost) {

            statusText.text =
                """
                You are the host.

                You can start the game when
                at least 2 players have joined.

                Maximum players: $playerCount
                """.trimIndent()

        } else {

            statusText.text =
                """
                You have joined the game.

                Waiting for the host to start.
                """.trimIndent()
        }

        statusText.textSize =
            18f

        statusText.gravity =
            Gravity.CENTER

        statusText.setTypeface(
            null,
            Typeface.BOLD
        )

        statusText.setPadding(
            0,
            10,
            0,
            15
        )

        layout.addView(statusText)

        setContentView(layout)
    }
}

package com.wordguessing.game

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

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
            25
        )

        layout.addView(codeText)

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
            5,
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

        // RULES
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

        // START / READY INFORMATION
        val statusText =
            TextView(this)

        if (isHost) {

            statusText.text =
                """
                Minimum 2 players required to start.

                Maximum players: $playerCount
                """.trimIndent()

        } else {

            statusText.text =
                "Waiting for the host to start the game."

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

        // READY BUTTON
        val readyButton =
            Button(this)

        readyButton.text =
            "Ready"

        readyButton.textSize =
            18f

        readyButton.setTypeface(
            null,
            Typeface.BOLD
        )

        layout.addView(
            readyButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        val waitingText =
            TextView(this)

        waitingText.text =
            "Waiting for other players..."

        waitingText.textSize =
            18f

        waitingText.gravity =
            Gravity.CENTER

        waitingText.setTextColor(
            Color.DKGRAY
        )

        waitingText.setPadding(
            0,
            15,
            0,
            0
        )

        layout.addView(waitingText)

        readyButton.setOnClickListener {

            readyButton.isEnabled =
                false

            readyButton.text =
                "Ready ✓"

            waitingText.text =
                "You are ready.\nWaiting for other players..."
        }

        setContentView(layout)
    }
}

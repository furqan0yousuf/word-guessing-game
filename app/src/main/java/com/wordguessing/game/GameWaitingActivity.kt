package com.wordguessing.game

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView

class GameWaitingActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gameCode =
            intent.getStringExtra("gameCode")
                ?: "------"

        val playerCount =
            intent.getIntExtra("playerCount", 2)

        val wordSelection =
            intent.getStringExtra("wordSelection")
                ?: "Random Word"

        val category =
            intent.getStringExtra("category")
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
            android.graphics.Typeface.BOLD
        )

        codeText.setPadding(
            0,
            10,
            0,
            25
        )

        layout.addView(codeText)

        val playersText =
            TextView(this)

        playersText.text =
            "Players: 1 of $playerCount"

        playersText.textSize =
            20f

        playersText.gravity =
            Gravity.CENTER

        playersText.setPadding(
            0,
            10,
            0,
            20
        )

        layout.addView(playersText)

        val rulesTitle =
            TextView(this)

        rulesTitle.text =
            "Game Rules"

        rulesTitle.textSize =
            22f

        rulesTitle.setTypeface(
            null,
            android.graphics.Typeface.BOLD
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

        val waitingText =
            TextView(this)

        waitingText.text =
            "Waiting for players to join..."

        waitingText.textSize =
            19f

        waitingText.gravity =
            Gravity.CENTER

        waitingText.setTextColor(
            Color.DKGRAY
        )

        layout.addView(waitingText)

        setContentView(layout)
    }
}

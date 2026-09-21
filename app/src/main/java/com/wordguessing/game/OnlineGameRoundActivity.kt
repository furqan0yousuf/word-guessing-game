package com.wordguessing.game

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView

class OnlineGameRoundActivity : Activity() {

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

        val maxPlayers =
            intent.getIntExtra(
                "maxPlayers",
                playerCount
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
            "Online Game"

        title.textSize =
            30f

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
            30
        )

        layout.addView(title)

        val gameInfo =
            TextView(this)

        gameInfo.text =
            """
            Game Code: $gameCode

            Players: $playerCount of $maxPlayers

            Category: $category

            Seconds per turn: $secondsPerTurn

            Word Selection: $wordSelection

            Next Word Master:
            $nextWordMaster
            """.trimIndent()

        gameInfo.textSize =
            19f

        gameInfo.gravity =
            Gravity.CENTER

        gameInfo.setPadding(
            0,
            10,
            0,
            30
        )

        layout.addView(gameInfo)

        val turnText =
            TextView(this)

        turnText.text =
            "Player 1's Turn"

        turnText.textSize =
            24f

        turnText.gravity =
            Gravity.CENTER

        turnText.setTypeface(
            null,
            Typeface.BOLD
        )

        turnText.setPadding(
            0,
            20,
            0,
            20
        )

        layout.addView(turnText)

        val message =
            TextView(this)

        message.text =
            "Online game screen ready.\n\nThe actual multiplayer gameplay will be connected next."

        message.textSize =
            18f

        message.gravity =
            Gravity.CENTER

        layout.addView(message)

        setContentView(layout)
    }
}

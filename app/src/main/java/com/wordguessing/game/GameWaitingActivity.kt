package com.wordguessing.game

import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class GameWaitingActivity : Activity() {

    private var gameCode = "------"
    private var playerCount = 2
    private var wordSelection = "Random Word"
    private var category = "Random"
    private var secondsPerTurn = 10
    private var nextWordMaster = "Same Word Master"
    private var playerName = "Player 1"
    private var isHost = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gameCode =
            intent.getStringExtra("gameCode")
                ?: "------"

        playerCount =
            intent.getIntExtra(
                "playerCount",
                2
            )

        wordSelection =
            intent.getStringExtra(
                "wordSelection"
            )
                ?: "Random Word"

        category =
            intent.getStringExtra(
                "category"
            )
                ?: "Random"

        secondsPerTurn =
            intent.getIntExtra(
                "secondsPerTurn",
                10
            )

        nextWordMaster =
            intent.getStringExtra(
                "nextWordMaster"
            )
                ?: "Same Word Master"

        playerName =
            intent.getStringExtra(
                "playerName"
            )
                ?: "Player 1"

        isHost =
            intent.getBooleanExtra(
                "isHost",
                true
            )

        val layout =
            LinearLayout(this)

        layout.orientation =
            LinearLayout.VERTICAL

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

        layout.addView(title)

        val codeText =
            TextView(this)

        codeText.text =
            "Game Code\n$gameCode"

        codeText.textSize =
            22f

        codeText.gravity =
            Gravity.CENTER

        codeText.setPadding(
            0,
            25,
            0,
            10
        )

        codeText.setTypeface(
            null,
            Typeface.BOLD
        )

        layout.addView(codeText)

        val copyButton =
            Button(this)

        copyButton.text =
            "Copy Game Code"

        copyButton.setOnClickListener {

            val clipboard =
                getSystemService(
                    CLIPBOARD_SERVICE
                ) as ClipboardManager

            val clip =
                ClipData.newPlainText(
                    "Game Code",
                    gameCode
                )

            clipboard.setPrimaryClip(
                clip
            )

            Toast.makeText(
                this,
                "Game code copied.",
                Toast.LENGTH_SHORT
            ).show()
        }

        layout.addView(
            copyButton
        )

        val playersTitle =
            TextView(this)

        playersTitle.text =
            "Players"

        playersTitle.textSize =
            21f

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

        layout.addView(
            playersTitle
        )

        val playersText =
            TextView(this)

        playersText.text =
            buildPlayerList()

        playersText.textSize =
            18f

        playersText.setPadding(
            0,
            5,
            0,
            10
        )

        layout.addView(
            playersText
        )

        val settingsTitle =
            TextView(this)

        settingsTitle.text =
            "Game Settings"

        settingsTitle.textSize =
            21f

        settingsTitle.setTypeface(
            null,
            Typeface.BOLD
        )

        settingsTitle.setPadding(
            0,
            20,
            0,
            10
        )

        layout.addView(
            settingsTitle
        )

        val settingsText =
            TextView(this)

        settingsText.text =
            """
            Maximum Players: $playerCount
            Word Selection: $wordSelection
            Category: $category
            Seconds per Turn: $secondsPerTurn
            Next Word Master: $nextWordMaster
            """.trimIndent()

        settingsText.textSize =
            17f

        layout.addView(
            settingsText
        )

        val ruleText =
            TextView(this)

        ruleText.text =
            if (
                wordSelection == "Random Word"
            ) {
                "\nWord Master: None\nEveryone plays."
            } else {
                "\nWord Master: Player 1\nWord Master does not play."
            }

        ruleText.textSize =
            17f

        ruleText.gravity =
            Gravity.CENTER

        ruleText.setTypeface(
            null,
            Typeface.BOLD
        )

        layout.addView(
            ruleText
        )

        val statusText =
            TextView(this)

        statusText.text =
            "\nPlayers are ready."

        statusText.textSize =
            18f

        statusText.gravity =
            Gravity.CENTER

        layout.addView(
            statusText
        )

        if (isHost) {

            val startButton =
                Button(this)

            startButton.text =
                "Start Game"

            startButton.textSize =
                18f

            startButton.setOnClickListener {

                AlertDialog.Builder(this)
                    .setTitle(
                        "Start Game?"
                    )
                    .setMessage(
                        "Start the game with the current players?"
                    )
                    .setNegativeButton(
                        "Cancel",
                        null
                    )
                    .setPositiveButton(
                        "Start"
                    ) { _, _ ->

                        startGame()
                    }
                    .show()
            }

            layout.addView(
                startButton
            )
        }

        val leaveButton =
            Button(this)

        leaveButton.text =
            "Leave Game"

        leaveButton.textSize =
            18f

        leaveButton.setOnClickListener {

            finish()
        }

        layout.addView(
            leaveButton
        )

        setContentView(layout)
    }

    private fun buildPlayerList(): String {

        val builder =
            StringBuilder()

        builder.append(
            "1. $playerName"
        )

        builder.append(
            " — HOST"
        )

        for (
            player in 2..playerCount
        ) {

            builder.append(
                "\n$player. Player $player"
            )
        }

        return builder.toString()
    }

    private fun startGame() {

        val gameIntent =
            Intent(
                this@GameWaitingActivity,
                OnlineGameRoundActivity::class.java
            )

        gameIntent.putExtra(
            "gameCode",
            gameCode
        )

        gameIntent.putExtra(
            "playerCount",
            playerCount
        )

        gameIntent.putExtra(
            "maxPlayers",
            playerCount
        )

        gameIntent.putExtra(
            "wordSelection",
            wordSelection
        )

        gameIntent.putExtra(
            "category",
            category
        )

        gameIntent.putExtra(
            "secondsPerTurn",
            secondsPerTurn
        )

        gameIntent.putExtra(
            "nextWordMaster",
            nextWordMaster
        )

        gameIntent.putExtra(
            "playerName",
            playerName
        )

        startActivity(
            gameIntent
        )
    }

    override fun onDestroy() {

        super.onDestroy()
    }
}

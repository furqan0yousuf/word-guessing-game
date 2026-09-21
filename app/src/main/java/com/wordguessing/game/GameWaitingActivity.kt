package com.wordguessing.game

import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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

    private lateinit var playersText: TextView
    private lateinit var statusText: TextView
    private lateinit var startButton: Button

    private var joinedPlayers = 1

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

        val manualWord =
            intent.getStringExtra(
                "manualWord"
            )
                ?: ""

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

        // TITLE
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
            20,
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

        layout.addView(copyButton)

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

        // PLAYERS TITLE
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

        // PLAYER LIST
        playersText =
            TextView(this)

        playersText.textSize =
            18f

        playersText.setPadding(
            0,
            5,
            0,
            15
        )

        layout.addView(playersText)

        updatePlayerList(
            playerName,
            isHost,
            playerCount
        )

        // SIMULATE PLAYER JOINING
        if (isHost) {

            val addPlayerButton =
                Button(this)

            addPlayerButton.text =
                "Simulate Player Joining"

            addPlayerButton.textSize =
                18f

            layout.addView(
                addPlayerButton
            )

            addPlayerButton.setOnClickListener {

                if (joinedPlayers < playerCount) {

                    joinedPlayers++

                    updatePlayerList(
                        playerName,
                        isHost,
                        playerCount
                    )

                } else {

                    Toast.makeText(
                        this,
                        "Maximum players reached.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        // GAME SETTINGS
        val settingsTitle =
            TextView(this)

        settingsTitle.text =
            "Game Settings"

        settingsTitle.textSize =
            22f

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

        layout.addView(settingsTitle)

        val settingsText =
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

        settingsText.text =
            """
            Word Selection: $wordSelection

            Category: $category

            Seconds per turn: $secondsPerTurn

            $wordMasterText

            Next Word Master:
            $nextWordMaster
            """.trimIndent()

        settingsText.textSize =
            18f

        settingsText.setPadding(
            0,
            5,
            0,
            15
        )

        layout.addView(settingsText)

        // STATUS
        statusText =
            TextView(this)

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
            15,
            0,
            15
        )

        layout.addView(statusText)

        updateStatus(
            isHost
        )

        // START GAME
        if (isHost) {

            startButton =
                Button(this)

            startButton.text =
                "Start Game"

            startButton.textSize =
                18f

            startButton.setTypeface(
                null,
                Typeface.BOLD
            )

            startButton.isEnabled =
                joinedPlayers >= 2

            layout.addView(
                startButton,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            )

            startButton.setOnClickListener {

                if (joinedPlayers < 2) {

                    Toast.makeText(
                        this,
                        "At least 2 players are required.",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setOnClickListener
                }

                AlertDialog.Builder(this)
                    .setTitle("Start Game?")
                    .setMessage(
                        "Start the game with $joinedPlayers of $playerCount players?"
                    )
                    .setNegativeButton(
                        "Cancel",
                        null
                    )
                    .setPositiveButton(
                        "Start"
                    ) { _, _ ->

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
                            joinedPlayers
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
                            "manualWord",
                            manualWord
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
                    .show()
            }
        }

        setContentView(layout)
    }

    private fun updatePlayerList(
        hostName: String,
        isHost: Boolean,
        maxPlayers: Int
    ) {

        val names =
            mutableListOf<String>()

        names.add(
            "$hostName — Host"
        )

        if (joinedPlayers >= 2) {
            names.add("Player 2")
        }

        if (joinedPlayers >= 3) {
            names.add("Player 3")
        }

        if (joinedPlayers >= 4) {
            names.add("Player 4")
        }

        val text =
            "Players joined: $joinedPlayers of $maxPlayers\n\n" +
                    names.mapIndexed { index, name ->
                        "${index + 1}. $name"
                    }.joinToString("\n")

        playersText.text =
            text

        if (::startButton.isInitialized) {
            startButton.isEnabled =
                joinedPlayers >= 2
        }

        if (::statusText.isInitialized) {
            updateStatus(isHost)
        }
    }

    private fun updateStatus(
        isHost: Boolean
    ) {

        if (isHost) {

            statusText.text =
                if (joinedPlayers >= 2) {

                    "Ready to start.\n$joinedPlayers players have joined."

                } else {

                    "Waiting for players.\nAt least 2 players are required to start."
                }

        } else {

            statusText.text =
                "You have joined the game.\nWaiting for the host to start."
        }
    }
}

package com.wordguessing.game

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class OnlineGameRoundActivity : Activity() {

    private lateinit var timerText: TextView
    private lateinit var turnText: TextView
    private lateinit var playersText: TextView

    private var timer: CountDownTimer? = null

    private val letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

    private var currentPlayer = 1

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

        layout.setPadding(
            16,
            16,
            16,
            16
        )

        // TITLE
        val title =
            TextView(this)

        title.text =
            "Online Game"

        title.textSize =
            28f

        title.gravity =
            Gravity.CENTER

        title.setTypeface(
            null,
            Typeface.BOLD
        )

        layout.addView(title)

        // GAME INFORMATION
        val info =
            TextView(this)

        info.text =
            """
            Game Code: $gameCode
            Players: $playerCount of $maxPlayers
            Category: $category
            Next Word Master: $nextWordMaster
            """.trimIndent()

        info.textSize =
            16f

        info.gravity =
            Gravity.CENTER

        info.setPadding(
            0,
            15,
            0,
            10
        )

        layout.addView(info)

        // PLAYERS
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
            10,
            0,
            5
        )

        layout.addView(playersTitle)

        playersText =
            TextView(this)

        playersText.text =
            buildPlayerList(
                playerCount,
                wordSelection
            )

        playersText.textSize =
            17f

        playersText.setPadding(
            0,
            5,
            0,
            10
        )

        layout.addView(playersText)

        // WORD MASTER
        val wordMasterText =
            TextView(this)

        if (
            wordSelection ==
            "Random Word"
        ) {

            wordMasterText.text =
                "Word Master: None\nEveryone plays"

        } else {

            wordMasterText.text =
                "Word Master: Player 1\nWord Master does not play"
        }

        wordMasterText.textSize =
            17f

        wordMasterText.gravity =
            Gravity.CENTER

        wordMasterText.setTypeface(
            null,
            Typeface.BOLD
        )

        wordMasterText.setPadding(
            0,
            5,
            0,
            10
        )

        layout.addView(wordMasterText)

        // WORD DISPLAY
        val wordText =
            TextView(this)

        wordText.text =
            "_ _ _ _ _"

        wordText.textSize =
            30f

        wordText.gravity =
            Gravity.CENTER

        wordText.setTypeface(
            null,
            Typeface.BOLD
        )

        wordText.setPadding(
            0,
            10,
            0,
            10
        )

        layout.addView(wordText)

        // TURN
        turnText =
            TextView(this)

        turnText.text =
            getTurnText(
                wordSelection
            )

        turnText.textSize =
            22f

        turnText.gravity =
            Gravity.CENTER

        turnText.setTypeface(
            null,
            Typeface.BOLD
        )

        layout.addView(turnText)

        // TIMER
        timerText =
            TextView(this)

        timerText.text =
            "Time: $secondsPerTurn"

        timerText.textSize =
            24f

        timerText.gravity =
            Gravity.CENTER

        timerText.setTypeface(
            null,
            Typeface.BOLD
        )

        timerText.setPadding(
            0,
            5,
            0,
            10
        )

        layout.addView(timerText)

        // LETTER BOARD
        val letterBoard =
            LinearLayout(this)

        letterBoard.orientation =
            LinearLayout.VERTICAL

        layout.addView(letterBoard)

        var currentRow =
            LinearLayout(this)

        currentRow.orientation =
            LinearLayout.HORIZONTAL

        currentRow.gravity =
            Gravity.CENTER

        letterBoard.addView(
            currentRow
        )

        for (i in letters.indices) {

            val letter =
                letters[i].toString()

            val button =
                Button(this)

            button.text =
                letter

            button.textSize =
                14f

            button.setTextColor(
                Color.WHITE
            )

            // GREEN = NOT GUESSED
            button.setBackgroundColor(
                Color.rgb(
                    0,
                    100,
                    0
                )
            )

            button.setOnClickListener {

                // RED = GUESSED
                button.setBackgroundColor(
                    Color.RED
                )

                button.isEnabled =
                    false

                button.setTextColor(
                    Color.WHITE
                )
            }

            currentRow.addView(
                button,
                LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
            )

            if (
                (i + 1) % 6 == 0
            ) {

                currentRow =
                    LinearLayout(this)

                currentRow.orientation =
                    LinearLayout.HORIZONTAL

                currentRow.gravity =
                    Gravity.CENTER

                letterBoard.addView(
                    currentRow
                )
            }
        }

        // WHOLE WORD BUTTON
        val wholeWordButton =
            Button(this)

        wholeWordButton.text =
            "Guess Whole Word"

        wholeWordButton.textSize =
            18f

        wholeWordButton.setOnClickListener {

            android.app.AlertDialog.Builder(this)
                .setTitle(
                    "Guess Whole Word"
                )
                .setMessage(
                    "Whole-word guessing will be connected next."
                )
                .setPositiveButton(
                    "OK",
                    null
                )
                .show()
        }

        layout.addView(
            wholeWordButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        // LEAVE GAME
        val leaveButton =
            Button(this)

        leaveButton.text =
            "Leave Game"

        leaveButton.textSize =
            18f

        leaveButton.setOnClickListener {

            timer?.cancel()

            finish()
        }

        layout.addView(
            leaveButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        setContentView(layout)

        startTurnTimer(
            secondsPerTurn,
            wordSelection,
            playerCount
        )
    }

    private fun buildPlayerList(
        playerCount: Int,
        wordSelection: String
    ): String {

        val builder =
            StringBuilder()

        for (player in 1..playerCount) {

            builder.append(
                "$player. Player $player"
            )

            if (
                wordSelection !=
                "Random Word" &&
                player == 1
            ) {

                builder.append(
                    " — WORD MASTER"
                )

            } else if (
                player == currentPlayer
            ) {

                builder.append(
                    " — CURRENT TURN"
                )
            }

            builder.append(
                "\n"
            )
        }

        return builder.toString().trim()
    }

    private fun getTurnText(
        wordSelection: String
    ): String {

        if (
            wordSelection !=
            "Random Word" &&
            currentPlayer == 1
        ) {

            currentPlayer = 2
        }

        return "Player $currentPlayer's Turn"
    }

    private fun updatePlayerList(
        playerCount: Int,
        wordSelection: String
    ) {

        playersText.text =
            buildPlayerList(
                playerCount,
                wordSelection
            )
    }

    private fun startTurnTimer(
        seconds: Int,
        wordSelection: String,
        playerCount: Int
    ) {

        timer?.cancel()

        timer =
            object :
                CountDownTimer(
                    seconds * 1000L,
                    1000L
                ) {

                    override fun onTick(
                        millisUntilFinished: Long
                    ) {

                        val remaining =
                            millisUntilFinished /
                                1000L

                        timerText.text =
                            "Time: $remaining"
                    }

                    override fun onFinish() {

                        timerText.text =
                            "Time: 0"

                        currentPlayer++

                        if (
                            wordSelection !=
                            "Random Word" &&
                            currentPlayer == 1
                        ) {

                            currentPlayer = 2
                        }

                        if (
                            currentPlayer >
                            playerCount
                        ) {

                            currentPlayer =
                                if (
                                    wordSelection ==
                                    "Random Word"
                                ) {
                                    1
                                } else {
                                    2
                                }
                        }

                        turnText.text =
                            "Player $currentPlayer's Turn"

                        updatePlayerList(
                            playerCount,
                            wordSelection
                        )

                        startTurnTimer(
                            seconds,
                            wordSelection,
                            playerCount
                        )
                    }
                }
                .start()
    }

    override fun onDestroy() {

        timer?.cancel()

        super.onDestroy()
    }
}

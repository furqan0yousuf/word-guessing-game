package com.wordguessing.game

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class OnlineGameRoundActivity : Activity() {

    private lateinit var timerText: TextView
    private lateinit var turnText: TextView
    private lateinit var playersText: TextView
    private lateinit var wordText: TextView

    private var timer: CountDownTimer? = null

    private val letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

    /*
     * TEMPORARY TEST WORD
     *
     * Later this will come from the actual
     * word/category system.
     */
    private val secretWord = "APPLE"

    private var currentPlayer = 1

    private var roundFinished = false

    private val scores =
        mutableMapOf<Int, Int>()

    private val guessedLetters =
        mutableSetOf<Char>()

    private val revealedLetters =
        mutableSetOf<Char>()

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

        for (player in 1..playerCount) {
            scores[player] = 0
        }

        /*
         * In temporary Manual Word mode,
         * Player 1 is the Word Master.
         */
        if (wordSelection != "Random Word") {
            currentPlayer = 2
        }

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
            10,
            0,
            10
        )

        layout.addView(info)

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

        layout.addView(
            playersTitle
        )

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

        layout.addView(
            playersText
        )

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

        layout.addView(
            wordMasterText
        )

        wordText =
            TextView(this)

        wordText.text =
            buildWordDisplay()

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
            15,
            0,
            15
        )

        layout.addView(
            wordText
        )

        turnText =
            TextView(this)

        turnText.text =
            "Player $currentPlayer's Turn"

        turnText.textSize =
            22f

        turnText.gravity =
            Gravity.CENTER

        turnText.setTypeface(
            null,
            Typeface.BOLD
        )

        layout.addView(
            turnText
        )

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

        layout.addView(
            timerText
        )

        /*
         * LETTER BOARD
         */
        val letterBoard =
            LinearLayout(this)

        letterBoard.orientation =
            LinearLayout.VERTICAL

        layout.addView(
            letterBoard
        )

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
                letters[i]

            val button =
                Button(this)

            button.text =
                letter.toString()

            button.textSize =
                14f

            button.setTextColor(
                Color.WHITE
            )

            button.setBackgroundColor(
                Color.rgb(
                    0,
                    100,
                    0
                )
            )

            button.setOnClickListener {

                if (roundFinished) {
                    return@setOnClickListener
                }

                /*
                 * Repeated letter
                 *
                 * Same player continues.
                 */
                if (
                    guessedLetters.contains(
                        letter
                    )
                ) {

                    Toast.makeText(
                        this,
                        "Already guessed.",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setOnClickListener
                }

                guessedLetters.add(
                    letter
                )

                button.setBackgroundColor(
                    Color.RED
                )

                button.isEnabled =
                    false

                /*
                 * CORRECT LETTER
                 */
                if (
                    secretWord.contains(
                        letter
                    )
                ) {

                    revealedLetters.add(
                        letter
                    )

                    updateWordDisplay()

                    val occurrences =
                        secretWord.count {
                            it == letter
                        }

                    scores[currentPlayer] =
                        (scores[currentPlayer] ?: 0) +
                            occurrences

                    updatePlayerList(
                        playerCount,
                        wordSelection
                    )

                    Toast.makeText(
                        this,
                        "Correct! You earned $occurrences point(s).",
                        Toast.LENGTH_SHORT
                    ).show()

                    /*
                     * If word is complete,
                     * current player wins.
                     */
                    if (
                        isWordComplete()
                    ) {

                        finishRound(
                            "Player $currentPlayer completed the word and wins!"
                        )

                    } else {

                        /*
                         * IMPORTANT:
                         *
                         * Correct letter means
                         * same player continues,
                         * but gets a NEW FULL TIMER.
                         */
                        startTurnTimer(
                            secondsPerTurn,
                            wordSelection,
                            playerCount
                        )
                    }

                } else {

                    /*
                     * WRONG LETTER
                     *
                     * Turn ends immediately.
                     */
                    Toast.makeText(
                        this,
                        "Wrong letter.",
                        Toast.LENGTH_SHORT
                    ).show()

                    moveToNextPlayer(
                        wordSelection,
                        playerCount,
                        secondsPerTurn
                    )
                }
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

        /*
         * WHOLE WORD BUTTON
         */
        val wholeWordButton =
            Button(this)

        wholeWordButton.text =
            "Guess Whole Word"

        wholeWordButton.textSize =
            18f

        wholeWordButton.setOnClickListener {

            if (!roundFinished) {

                showWholeWordDialog(
                    wordSelection,
                    playerCount,
                    secondsPerTurn
                )
            }
        }

        layout.addView(
            wholeWordButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        /*
         * LEAVE GAME
         */
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
            leaveButton
        )

        setContentView(layout)

        startTurnTimer(
            secondsPerTurn,
            wordSelection,
            playerCount
        )
    }

    /*
     * Creates:
     *
     * _ _ _ _ _
     *
     * or:
     *
     * A _ P P _
     */
    private fun buildWordDisplay(): String {

        val builder =
            StringBuilder()

        for (letter in secretWord) {

            if (
                revealedLetters.contains(
                    letter
                )
            ) {

                builder.append(
                    letter
                )

            } else {

                builder.append(
                    "_"
                )
            }

            builder.append(" ")
        }

        return builder
            .toString()
            .trim()
    }

    private fun updateWordDisplay() {

        wordText.text =
            buildWordDisplay()
    }

    private fun isWordComplete(): Boolean {

        for (letter in secretWord) {

            if (
                !revealedLetters.contains(
                    letter
                )
            ) {

                return false
            }
        }

        return true
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

            builder.append(
                " — Score: ${scores[player] ?: 0}"
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

        return builder
            .toString()
            .trim()
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

    /*
     * START OR RESET TIMER
     */
    private fun startTurnTimer(
        seconds: Int,
        wordSelection: String,
        playerCount: Int
    ) {

        timer?.cancel()

        if (roundFinished) {
            return
        }

        /*
         * Every time this function is called,
         * the player receives a completely new timer.
         */
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

                        if (
                            remaining <= 3
                        ) {

                            timerText.setTextColor(
                                Color.RED
                            )

                        } else {

                            timerText.setTextColor(
                                Color.BLACK
                            )
                        }
                    }

                    override fun onFinish() {

                        if (roundFinished) {
                            return
                        }

                        timerText.text =
                            "Time: 0"

                        timerText.setTextColor(
                            Color.RED
                        )

                        Toast.makeText(
                            this@OnlineGameRoundActivity,
                            "Time is up.",
                            Toast.LENGTH_SHORT
                        ).show()

                        moveToNextPlayer(
                            wordSelection,
                            playerCount,
                            seconds
                        )
                    }
                }
                .start()
    }

    /*
     * MOVE TO NEXT PLAYER
     */
    private fun moveToNextPlayer(
        wordSelection: String,
        playerCount: Int,
        secondsPerTurn: Int
    ) {

        timer?.cancel()

        currentPlayer++

        /*
         * Manual Word:
         * Player 1 is Word Master,
         * so skip Player 1.
         */
        if (
            wordSelection !=
            "Random Word" &&
            currentPlayer == 1
        ) {

            currentPlayer = 2
        }

        /*
         * Wrap around.
         */
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

        /*
         * New player gets a completely
         * new full timer.
         */
        startTurnTimer(
            secondsPerTurn,
            wordSelection,
            playerCount
        )
    }

    /*
     * WHOLE WORD GUESS
     */
    private fun showWholeWordDialog(
        wordSelection: String,
        playerCount: Int,
        secondsPerTurn: Int
    ) {

        /*
         * Pause normal turn timer.
         */
        timer?.cancel()

        val input =
            EditText(this)

        input.hint =
            "Enter your word"

        input.setSingleLine(
            true
        )

        input.textSize =
            18f

        val dialog =
            AlertDialog.Builder(this)
                .setTitle(
                    "Guess Whole Word"
                )
                .setView(input)
                .setNegativeButton(
                    "Cancel",
                    null
                )
                .setPositiveButton(
                    "Guess",
                    null
                )
                .create()

        dialog.setOnShowListener {

            dialog.getButton(
                AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener {

                val guess =
                    input.text
                        .toString()
                        .trim()
                        .uppercase()

                if (
                    guess.isEmpty()
                ) {

                    input.error =
                        "Enter a word."

                    return@setOnClickListener
                }

                dialog.dismiss()

                /*
                 * Correct whole-word guess:
                 * immediate round winner.
                 */
                if (
                    guess ==
                    secretWord
                ) {

                    finishRound(
                        "Player $currentPlayer guessed the whole word and wins!"
                    )

                } else {

                    /*
                     * Wrong whole-word guess:
                     * turn ends.
                     */
                    Toast.makeText(
                        this,
                        "Wrong whole-word guess.",
                        Toast.LENGTH_SHORT
                    ).show()

                    moveToNextPlayer(
                        wordSelection,
                        playerCount,
                        secondsPerTurn
                    )
                }
            }
        }

        /*
         * If user cancels,
         * resume the current player's timer.
         */
        dialog.setOnCancelListener {

            if (!roundFinished) {

                startTurnTimer(
                    secondsPerTurn,
                    wordSelection,
                    playerCount
                )
            }
        }

        dialog.show()
    }

    /*
     * END ROUND
     */
    private fun finishRound(
        message: String
    ) {

        if (roundFinished) {
            return
        }

        roundFinished = true

        timer?.cancel()

        AlertDialog.Builder(this)
            .setTitle(
                "Round Over"
            )
            .setMessage(
                message
            )
            .setPositiveButton(
                "OK"
            ) { _, _ ->

                finish()
            }
            .setCancelable(false)
            .show()
    }

    override fun onDestroy() {

        timer?.cancel()

        super.onDestroy()
    }
}

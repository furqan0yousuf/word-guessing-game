package com.wordguessing.game

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Gravity
import android.view.View
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
    private lateinit var wholeWordButton: Button

    private var timer: CountDownTimer? = null
    private var wholeWordTimer: CountDownTimer? = null

    /*
     * TEMPORARY TEST WORD.
     *
     * If Create Game sends a manual word,
     * that word will be used instead.
     *
     * Later this will come from the real
     * word/category system.
     */
    private var secretWord = "APPLE"

    private val letters =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

    private var currentPlayer = 1
    private var playerCount = 2

    private var secondsPerTurn = 10

    private var roundFinished = false

    /*
     * Remaining time in the current normal turn.
     *
     * This is important because opening the
     * whole-word dialog pauses the normal timer.
     */
    private var remainingTurnSeconds = 10

    /*
     * One whole-word attempt per player
     * during each turn.
     */
    private var wholeWordAttemptUsed = false

    /*
     * For 3-4 player games.
     *
     * After 3 missed turns, a player is eliminated.
     */
    private val missedTurns =
        mutableMapOf<Int, Int>()

    private val eliminatedPlayers =
        mutableSetOf<Int>()

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

        playerCount =
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

        secondsPerTurn =
            intent.getIntExtra(
                "secondsPerTurn",
                10
            )

        val nextWordMaster =
            intent.getStringExtra(
                "nextWordMaster"
            )
                ?: "Winner becomes Word Master"

        /*
         * Use manual word if one was supplied.
         *
         * This lets us eventually connect the
         * real Create Game word without changing
         * this gameplay logic.
         */
        val manualWord =
            intent.getStringExtra(
                "manualWord"
            )
                ?.trim()
                ?.uppercase()

        if (
            !manualWord.isNullOrEmpty()
        ) {
            secretWord = manualWord
        }

        /*
         * Start scores and missed-turn counters.
         */
        for (player in 1..playerCount) {

            scores[player] = 0
            missedTurns[player] = 0
        }

        /*
         * Temporary Manual Word behavior:
         * Player 1 is Word Master and does not play.
         */
        if (
            wordSelection !=
            "Random Word"
        ) {

            currentPlayer = 2
        }

        remainingTurnSeconds =
            secondsPerTurn

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

        /*
         * TITLE
         */
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

        /*
         * GAME INFORMATION
         */
        val info =
            TextView(this)

        info.text =
            """
            Game Code: $gameCode
            Players: $playerCount of $maxPlayers
            Category: $category
            Seconds per Turn: $secondsPerTurn
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

        /*
         * PLAYERS
         */
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

        /*
         * WORD MASTER INFORMATION
         */
        val wordMasterText =
            TextView(this)

        if (
            wordSelection ==
            "Random Word"
        ) {

            wordMasterText.text =
                "Word Master: None\nEveryone plays."

        } else {

            wordMasterText.text =
                "Word Master: Player 1\nWord Master does not play."
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

        /*
         * WORD DISPLAY
         */
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

        /*
         * CURRENT TURN
         */
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

        /*
         * NORMAL TIMER
         */
        timerText =
            TextView(this)

        timerText.text =
            "Time: $remainingTurnSeconds"

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

                if (
                    roundFinished
                ) {
                    return@setOnClickListener
                }

                /*
                 * Already guessed:
                 *
                 * Same player continues.
                 * Timer is NOT reset.
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

                    /*
                     * One point for each occurrence.
                     */
                    val occurrences =
                        secretWord.count {
                            it == letter
                        }

                    scores[currentPlayer] =
                        (scores[currentPlayer] ?: 0) +
                            occurrences

                    updatePlayerList(
                        wordSelection
                    )

                    Toast.makeText(
                        this,
                        "Correct! You earned $occurrences point(s).",
                        Toast.LENGTH_SHORT
                    ).show()

                    /*
                     * Check whether the complete
                     * word has been revealed.
                     */
                    if (
                        isWordComplete()
                    ) {

                        finishRound(
                            "Player $currentPlayer completed the word and wins!"
                        )

                    } else {

                        /*
                         * CORRECT LETTER:
                         *
                         * Same player continues.
                         *
                         * IMPORTANT:
                         * Give that player a completely
                         * fresh full timer.
                         */
                        wholeWordAttemptUsed =
                            false

                        startTurnTimer(
                            secondsPerTurn,
                            wordSelection
                        )
                    }

                } else {

                    /*
                     * WRONG LETTER:
                     *
                     * Current turn immediately ends.
                     */
                    Toast.makeText(
                        this,
                        "Wrong letter.",
                        Toast.LENGTH_SHORT
                    ).show()

                    moveToNextPlayer(
                        wordSelection
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
        wholeWordButton =
            Button(this)

        wholeWordButton.text =
            "Guess Whole Word"

        wholeWordButton.textSize =
            18f

        wholeWordButton.setOnClickListener {

            if (
                !roundFinished
            ) {

                showWholeWordDialog(
                    wordSelection
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
            wholeWordTimer?.cancel()

            finish()
        }

        layout.addView(
            leaveButton
        )

        setContentView(layout)

        /*
         * Start first turn.
         */
        startTurnTimer(
            secondsPerTurn,
            wordSelection
        )
    }

    /*
     * WORD DISPLAY
     *
     * Spaces remain spaces.
     *
     * Example:
     *
     * NEW YORK
     *
     * _ _ _   _ _ _ _
     */
    private fun buildWordDisplay(): String {

        val builder =
            StringBuilder()

        for (letter in secretWord) {

            if (
                letter == ' '
            ) {

                builder.append(
                    "   "
                )

            } else if (
                revealedLetters.contains(
                    letter
                )
            ) {

                builder.append(
                    letter
                )

                builder.append(
                    " "
                )

            } else {

                builder.append(
                    "_ "
                )
            }
        }

        return builder
            .toString()
            .trim()
    }

    private fun updateWordDisplay() {

        wordText.text =
            buildWordDisplay()
    }

    /*
     * A word is complete when every
     * non-space character is revealed.
     */
    private fun isWordComplete(): Boolean {

        for (letter in secretWord) {

            if (
                letter != ' ' &&
                !revealedLetters.contains(
                    letter
                )
            ) {

                return false
            }
        }

        return true
    }

    /*
     * PLAYER LIST
     */
    private fun buildPlayerList(
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
                eliminatedPlayers.contains(
                    player
                )
            ) {

                builder.append(
                    " — ELIMINATED"
                )

            } else if (
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

            /*
             * Show missed turns for 3-4 player games.
             */
            if (
                playerCount >= 3 &&
                !eliminatedPlayers.contains(
                    player
                )
            ) {

                builder.append(
                    " — Misses: ${missedTurns[player] ?: 0}/3"
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
        wordSelection: String
    ) {

        playersText.text =
            buildPlayerList(
                wordSelection
            )
    }

    /*
     * NORMAL TURN TIMER
     */
    private fun startTurnTimer(
        seconds: Int,
        wordSelection: String
    ) {

        timer?.cancel()

        wholeWordTimer?.cancel()

        if (
            roundFinished
        ) {
            return
        }

        remainingTurnSeconds =
            seconds

        timer =
            object :
                CountDownTimer(
                    seconds * 1000L,
                    1000L
                ) {

                    override fun onTick(
                        millisUntilFinished: Long
                    ) {

                        remainingTurnSeconds =
                            (
                                millisUntilFinished /
                                    1000L
                                ).toInt()

                        timerText.text =
                            "Time: $remainingTurnSeconds"

                        if (
                            remainingTurnSeconds <= 3
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

                        if (
                            roundFinished
                        ) {
                            return
                        }

                        remainingTurnSeconds =
                            0

                        timerText.text =
                            "Time: 0"

                        timerText.setTextColor(
                            Color.RED
                        )

                        /*
                         * Timer expiration counts
                         * as a missed turn.
                         */
                        handleMissedTurn(
                            wordSelection
                        )
                    }
                }
                .start()
    }

    /*
     * HANDLE MISSED TURN
     */
    private fun handleMissedTurn(
        wordSelection: String
    ) {

        /*
         * In 2-player games there is no
         * elimination system.
         */
        if (
            playerCount >= 3
        ) {

            val misses =
                (missedTurns[currentPlayer] ?: 0) + 1

            missedTurns[currentPlayer] =
                misses

            /*
             * Three missed turns =
             * eliminated.
             */
            if (
                misses >= 3
            ) {

                eliminatedPlayers.add(
                    currentPlayer
                )

                Toast.makeText(
                    this,
                    "Player $currentPlayer is eliminated after 3 missed turns.",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                Toast.makeText(
                    this,
                    "Player $currentPlayer missed a turn.",
                    Toast.LENGTH_SHORT
                ).show()
            }

        } else {

            Toast.makeText(
                this,
                "Time is up.",
                Toast.LENGTH_SHORT
            ).show()
        }

        /*
         * Make sure there is still someone
         * who can play.
         */
        if (
            getActivePlayerCount(
                wordSelection
            ) <= 1
        ) {

            finishRound(
                "Only one player remains. Player $currentPlayer wins!"
            )

            return
        }

        moveToNextPlayer(
            wordSelection
        )
    }

    /*
     * MOVE TO NEXT ELIGIBLE PLAYER
     */
    private fun moveToNextPlayer(
        wordSelection: String
    ) {

        timer?.cancel()

        wholeWordTimer?.cancel()

        if (
            roundFinished
        ) {
            return
        }

        var attempts = 0

        do {

            currentPlayer++

            if (
                currentPlayer >
                playerCount
            ) {

                currentPlayer =
                    1
            }

            /*
             * Manual Word:
             * Player 1 is Word Master.
             */
            if (
                wordSelection !=
                "Random Word" &&
                currentPlayer == 1
            ) {

                currentPlayer =
                    2
            }

            attempts++

        } while (
            (
                eliminatedPlayers.contains(
                    currentPlayer
                )
            ) &&
            attempts <= playerCount + 1
        )

        /*
         * Reset whole-word attempt
         * for the new player's turn.
         */
        wholeWordAttemptUsed =
            false

        turnText.text =
            "Player $currentPlayer's Turn"

        updatePlayerList(
            wordSelection
        )

        startTurnTimer(
            secondsPerTurn,
            wordSelection
        )
    }

    /*
     * COUNT ACTIVE PLAYERS
     */
    private fun getActivePlayerCount(
        wordSelection: String
    ): Int {

        var count = 0

        for (player in 1..playerCount) {

            if (
                eliminatedPlayers.contains(
                    player
                )
            ) {
                continue
            }

            if (
                wordSelection !=
                "Random Word" &&
                player == 1
            ) {
                continue
            }

            count++
        }

        return count
    }

    /*
     * WHOLE-WORD DIALOG
     *
     * This has its OWN 10-second timer.
     *
     * The normal turn timer is paused.
     */
    private fun showWholeWordDialog(
        wordSelection: String
    ) {

        /*
         * One attempt per player per turn.
         */
        if (
            wholeWordAttemptUsed
        ) {

            Toast.makeText(
                this,
                "You already used your whole-word guess this turn.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        wholeWordAttemptUsed =
            true

        /*
         * Save the normal timer's remaining time.
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

        /*
         * Dialog layout.
         */
        val dialogLayout =
            LinearLayout(this)

        dialogLayout.orientation =
            LinearLayout.VERTICAL

        dialogLayout.setPadding(
            30,
            10,
            30,
            5
        )

        dialogLayout.addView(
            input
        )

        val wholeWordTimerText =
            TextView(this)

        wholeWordTimerText.text =
            "Time: 10"

        wholeWordTimerText.textSize =
            22f

        wholeWordTimerText.gravity =
            Gravity.CENTER

        wholeWordTimerText.setTypeface(
            null,
            Typeface.BOLD
        )

        wholeWordTimerText.setPadding(
            0,
            15,
            0,
            5
        )

        dialogLayout.addView(
            wholeWordTimerText
        )

        val dialog =
            AlertDialog.Builder(this)
                .setTitle(
                    "Guess Whole Word"
                )
                .setView(
                    dialogLayout
                )
                .setNegativeButton(
                    "Cancel",
                    null
                )
                .setPositiveButton(
                    "Guess",
                    null
                )
                .create()

        /*
         * Separate 10-second timer.
         */
        var wholeWordSeconds =
            10

        wholeWordTimer =
            object :
                CountDownTimer(
                    10000L,
                    1000L
                ) {

                    override fun onTick(
                        millisUntilFinished: Long
                    ) {

                        wholeWordSeconds =
                            (
                                millisUntilFinished /
                                    1000L
                                ).toInt()

                        wholeWordTimerText.text =
                            "Time: $wholeWordSeconds"

                        if (
                            wholeWordSeconds <= 3
                        ) {

                            wholeWordTimerText.setTextColor(
                                Color.RED
                            )

                        } else {

                            wholeWordTimerText.setTextColor(
                                Color.BLACK
                            )
                        }
                    }

                    override fun onFinish() {

                        wholeWordTimerText.text =
                            "Time: 0"

                        wholeWordTimerText.setTextColor(
                            Color.RED
                        )

                        dialog.dismiss()

                        Toast.makeText(
                            this@OnlineGameRoundActivity,
                            "Whole-word time is up.",
                            Toast.LENGTH_SHORT
                        ).show()

                        /*
                         * Timeout ends the turn.
                         */
                        moveToNextPlayer(
                            wordSelection
                        )
                    }
                }
                .start()

        /*
         * Positive button.
         */
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

                wholeWordTimer?.cancel()

                dialog.dismiss()

                /*
                 * CORRECT WHOLE WORD
                 *
                 * This player wins regardless
                 * of score.
                 */
                if (
                    guess ==
                    secretWord.uppercase()
                ) {

                    finishRound(
                        "Player $currentPlayer guessed the whole word and wins!"
                    )

                } else {

                    /*
                     * WRONG WHOLE WORD
                     *
                     * No points.
                     * Turn ends.
                     */
                    Toast.makeText(
                        this,
                        "Wrong whole-word guess!",
                        Toast.LENGTH_SHORT
                    ).show()

                    moveToNextPlayer(
                        wordSelection
                    )
                }
            }
        }

        /*
         * Cancel button / back button.
         *
         * IMPORTANT:
         * The whole-word attempt has already
         * been consumed.
         *
         * The normal timer resumes using
         * the time that was left before the
         * dialog was opened.
         */
        dialog.setOnCancelListener {

            wholeWordTimer?.cancel()

            if (
                !roundFinished
            ) {

                if (
                    remainingTurnSeconds <= 0
                ) {

                    moveToNextPlayer(
                        wordSelection
                    )

                } else {

                    startTurnTimer(
                        remainingTurnSeconds,
                        wordSelection
                    )
                }
            }
        }

        dialog.show()
    }

    /*
     * FINISH ROUND
     */
    private fun finishRound(
        message: String
    ) {

        if (
            roundFinished
        ) {
            return
        }

        roundFinished =
            true

        timer?.cancel()

        wholeWordTimer?.cancel()

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

        wholeWordTimer?.cancel()

        super.onDestroy()
    }
}

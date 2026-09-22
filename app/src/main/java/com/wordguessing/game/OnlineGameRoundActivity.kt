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
import kotlin.random.Random

class OnlineGameRoundActivity : Activity() {

    private lateinit var timerText: TextView
    private lateinit var turnText: TextView
    private lateinit var playersText: TextView
    private lateinit var wordText: TextView
    private lateinit var wholeWordButton: Button

    private var timer: CountDownTimer? = null
    private var wholeWordTimer: CountDownTimer? = null

    private var secretWord = ""

    private val letters =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

    private var currentPlayer = 1
    private var playerCount = 2

    /*
     * 20 seconds is now the default.
     *
     * 20 / 30 / 45 / 60 = timed
     * 0 or -1 = Unlimited
     */
    private var secondsPerTurn = 20

    private var roundFinished = false

    private var remainingTurnSeconds = 20

    private var wholeWordAttemptUsed = false

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

    private var previousWord = ""

    private var actualCategory = "Random"

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

        val selectedCategory =
            intent.getStringExtra(
                "category"
            )
                ?: "Random"

        /*
         * Default is now 20.
         *
         * Older screens may still send 10.
         * We intentionally convert anything below
         * 20 to 20 so the new minimum is enforced.
         *
         * 0 or -1 means Unlimited.
         */
        val requestedSeconds =
            intent.getIntExtra(
                "secondsPerTurn",
                20
            )

        secondsPerTurn =
            when {
                requestedSeconds <= 0 -> 0
                requestedSeconds < 20 -> 20
                requestedSeconds == 20 -> 20
                requestedSeconds == 30 -> 30
                requestedSeconds == 45 -> 45
                requestedSeconds == 60 -> 60
                else -> 20
            }

        val nextWordMaster =
            intent.getStringExtra(
                "nextWordMaster"
            )
                ?: "Winner becomes Word Master"

        val manualWord =
            intent.getStringExtra(
                "manualWord"
            )
                ?.trim()
                ?.uppercase()

        chooseWord(
            wordSelection,
            selectedCategory,
            manualWord
        )

        for (player in 1..playerCount) {
            scores[player] = 0
            missedTurns[player] = 0
        }

        /*
         * Manual Word:
         * Player 1 is Word Master.
         */
        if (
            wordSelection !=
            "Random Word"
        ) {
            currentPlayer = 2
        }

        remainingTurnSeconds =
            if (isUnlimited()) {
                0
            } else {
                secondsPerTurn
            }

        createGameScreen(
            gameCode,
            maxPlayers,
            wordSelection,
            selectedCategory,
            nextWordMaster
        )

        startTurnTimer(
            secondsPerTurn,
            wordSelection
        )
    }

    /*
     * CREATE SCREEN
     */
    private fun createGameScreen(
        gameCode: String,
        maxPlayers: Int,
        wordSelection: String,
        selectedCategory: String,
        nextWordMaster: String
    ) {

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

        title.setPadding(
            0,
            0,
            0,
            8
        )

        layout.addView(title)

        /*
         * GAME CODE
         */
        val codeText =
            TextView(this)

        codeText.text =
            "Game Code: $gameCode"

        codeText.textSize =
            18f

        codeText.gravity =
            Gravity.CENTER

        codeText.setTypeface(
            null,
            Typeface.BOLD
        )

        codeText.setTextColor(
            Color.rgb(0, 70, 140)
        )

        codeText.setPadding(
            0,
            4,
            0,
            8
        )

        layout.addView(codeText)

        /*
         * CATEGORY
         *
         * Made intentionally prominent.
         */
        val categoryBox =
            TextView(this)

        categoryBox.text =
            "CATEGORY: ${
                getCategoryDisplay(
                    selectedCategory,
                    wordSelection
                )
            }"

        categoryBox.textSize =
            21f

        categoryBox.gravity =
            Gravity.CENTER

        categoryBox.setTypeface(
            null,
            Typeface.BOLD
        )

        categoryBox.setTextColor(
            Color.rgb(0, 70, 140)
        )

        categoryBox.setPadding(
            12,
            12,
            12,
            12
        )

        layout.addView(
            categoryBox,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        /*
         * GAME SETTINGS
         */
        val info =
            TextView(this)

        info.text =
            """
            Players: $playerCount of $maxPlayers
            Turn Time: ${getTurnTimeDisplay()}
            Next Word Master: $nextWordMaster
            """.trimIndent()

        info.textSize =
            15f

        info.gravity =
            Gravity.CENTER

        info.setPadding(
            0,
            8,
            0,
            8
        )

        layout.addView(info)

        /*
         * PLAYERS TITLE
         */
        val playersTitle =
            TextView(this)

        playersTitle.text =
            "PLAYERS"

        playersTitle.textSize =
            21f

        playersTitle.setTypeface(
            null,
            Typeface.BOLD
        )

        playersTitle.gravity =
            Gravity.CENTER

        playersTitle.setPadding(
            0,
            8,
            0,
            4
        )

        layout.addView(
            playersTitle
        )

        /*
         * PLAYER LIST
         */
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
            4,
            0,
            8
        )

        layout.addView(
            playersText
        )

        /*
         * WORD MASTER
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

        wordMasterText.setTextColor(
            Color.DKGRAY
        )

        wordMasterText.setPadding(
            0,
            4,
            0,
            8
        )

        layout.addView(
            wordMasterText
        )

        /*
         * WORD
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
            12,
            0,
            12
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
            "PLAYER $currentPlayer'S TURN"

        turnText.textSize =
            23f

        turnText.gravity =
            Gravity.CENTER

        turnText.setTypeface(
            null,
            Typeface.BOLD
        )

        turnText.setTextColor(
            Color.rgb(0, 100, 0)
        )

        turnText.setPadding(
            0,
            8,
            0,
            4
        )

        layout.addView(
            turnText
        )

        /*
         * TIMER
         */
        timerText =
            TextView(this)

        timerText.text =
            getTimerDisplay()

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
            4,
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
                 * CORRECT
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
                        wordSelection
                    )

                    Toast.makeText(
                        this,
                        "Correct! You earned $occurrences point(s).",
                        Toast.LENGTH_SHORT
                    ).show()

                    if (
                        isWordComplete()
                    ) {

                        finishRound(
                            "Player $currentPlayer completed the word and wins!"
                        )

                    } else {

                        /*
                         * Correct letter:
                         * same player continues.
                         * Fresh timer.
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
                     * WRONG:
                     * Turn immediately ends.
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
         * WHOLE WORD
         */
        wholeWordButton =
            Button(this)

        wholeWordButton.text =
            "Guess Whole Word"

        wholeWordButton.textSize =
            18f

        wholeWordButton.setTypeface(
            null,
            Typeface.BOLD
        )

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
         * LEAVE
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
    }

    /*
     * IS TIMER UNLIMITED?
     */
    private fun isUnlimited(): Boolean {

        return secondsPerTurn <= 0
    }

    /*
     * TIMER DISPLAY
     */
    private fun getTimerDisplay(): String {

        return if (
            isUnlimited()
        ) {

            "Time: Unlimited"

        } else {

            "Time: $remainingTurnSeconds"
        }
    }

    /*
     * TURN TIME DISPLAY
     */
    private fun getTurnTimeDisplay(): String {

        return if (
            isUnlimited()
        ) {

            "Unlimited"

        } else {

            "$secondsPerTurn seconds"
        }
    }

    /*
     * WORD SELECTION
     */
    private fun chooseWord(
        wordSelection: String,
        selectedCategory: String,
        manualWord: String?
    ) {

        /*
         * MANUAL WORD
         */
        if (
            wordSelection !=
            "Random Word"
        ) {

            if (
                !manualWord.isNullOrEmpty()
            ) {

                secretWord =
                    manualWord

            } else {

                secretWord =
                    "APPLE"
            }

            actualCategory =
                if (
                    selectedCategory == "Random"
                ) {
                    "Random"
                } else {
                    selectedCategory
                }

            guessedLetters.clear()
            revealedLetters.clear()

            return
        }

        /*
         * RANDOM WORD
         */
        val availableWords =
            mutableListOf<String>()

        if (
            selectedCategory == "Random"
        ) {

            val categoryNames =
                WordBank.categories.keys.toList()

            actualCategory =
                categoryNames[
                    Random.nextInt(
                        categoryNames.size
                    )
                ]

            availableWords.addAll(
                WordBank.categories[
                    actualCategory
                ] ?: emptyList()
            )

        } else {

            actualCategory =
                selectedCategory

            availableWords.addAll(
                WordBank.categories[
                    selectedCategory
                ] ?: WordBank.categories[
                    "Things"
                ]!!
            )
        }

        /*
         * Avoid immediate repeat.
         */
        if (
            availableWords.size > 1 &&
            previousWord.isNotEmpty()
        ) {

            availableWords.remove(
                previousWord
            )
        }

        if (
            availableWords.isEmpty()
        ) {

            availableWords.add(
                "APPLE"
            )
        }

        secretWord =
            availableWords[
                Random.nextInt(
                    availableWords.size
                )
            ]

        previousWord =
            secretWord

        guessedLetters.clear()
        revealedLetters.clear()
    }

    /*
     * CATEGORY DISPLAY
     */
    private fun getCategoryDisplay(
        selectedCategory: String,
        wordSelection: String
    ): String {

        return if (
            selectedCategory == "Random"
        ) {

            if (
                wordSelection ==
                "Random Word"
            ) {

                "Random: $actualCategory"

            } else {

                "Random"
            }

        } else {

            actualCategory
        }
    }

    /*
     * WORD DISPLAY
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
     * COMPLETE WORD?
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

        /*
         * Make the whole player list bold.
         * The status wording itself identifies
         * CURRENT TURN / WORD MASTER / ELIMINATED.
         */
        playersText.setTypeface(
            null,
            Typeface.BOLD
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

        /*
         * UNLIMITED
         */
        if (
            seconds <= 0
        ) {

            remainingTurnSeconds =
                0

            timerText.text =
                "Time: Unlimited"

            timerText.setTextColor(
                Color.rgb(
                    0,
                    100,
                    0
                )
            )

            return
        }

        /*
         * TIMED TURN
         */
        remainingTurnSeconds =
            seconds

        timerText.text =
            "Time: $remainingTurnSeconds"

        timerText.setTextColor(
            Color.BLACK
        )

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

                        handleMissedTurn(
                            wordSelection
                        )
                    }
                }
                .start()
    }

    /*
     * MISSED TURN
     */
    private fun handleMissedTurn(
        wordSelection: String
    ) {

        if (
            playerCount >= 3
        ) {

            val misses =
                (missedTurns[currentPlayer] ?: 0) + 1

            missedTurns[currentPlayer] =
                misses

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
     * NEXT PLAYER
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
             * Player 1 is Word Master
             * when using manual word.
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
            eliminatedPlayers.contains(
                currentPlayer
            ) &&
            attempts <=
            playerCount + 1
        )

        wholeWordAttemptUsed =
            false

        turnText.text =
            "PLAYER $currentPlayer'S TURN"

        updatePlayerList(
            wordSelection
        )

        startTurnTimer(
            secondsPerTurn,
            wordSelection
        )
    }

    /*
     * ACTIVE PLAYERS
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
     * WHOLE WORD DIALOG
     *
     * Still 10 seconds.
     */
    private fun showWholeWordDialog(
        wordSelection: String
    ) {

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
         * Pause normal timer.
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
         * Separate 10-second whole-word timer.
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

                        moveToNextPlayer(
                            wordSelection
                        )
                    }
                }
                .start()

        /*
         * Dialog buttons.
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

                if (
                    guess ==
                    secretWord.uppercase()
                ) {

                    finishRound(
                        "Player $currentPlayer guessed the whole word and wins!"
                    )

                } else {

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
         * Cancel / back.
         *
         * The whole-word attempt is consumed,
         * but the normal timer resumes from
         * where it was.
         */
        dialog.setOnCancelListener {

            wholeWordTimer?.cancel()

            if (
                !roundFinished
            ) {

                if (
                    isUnlimited()
                ) {

                    startTurnTimer(
                        0,
                        wordSelection
                    )

                } else if (
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

        val title =
            TextView(this)

        title.text =
            "🏆 ROUND OVER"

        title.textSize =
            26f

        title.gravity =
            Gravity.CENTER

        title.setTypeface(
            null,
            Typeface.BOLD
        )

        title.setTextColor(
            Color.rgb(
                0,
                100,
                0
            )
        )

        title.setPadding(
            0,
            10,
            0,
            15
        )

        val dialog =
            AlertDialog.Builder(this)
                .setCustomTitle(
                    title
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
                .create()

        dialog.setOnShowListener {

            dialog.getButton(
                AlertDialog.BUTTON_POSITIVE
            ).setTypeface(
                null,
                Typeface.BOLD
            )
        }

        dialog.show()
    }

    override fun onDestroy() {

        timer?.cancel()
        wholeWordTimer?.cancel()

        super.onDestroy()
    }

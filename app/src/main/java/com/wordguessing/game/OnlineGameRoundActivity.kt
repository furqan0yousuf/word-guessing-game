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
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import kotlin.random.Random

class OnlineGameRoundActivity : Activity() {

    private lateinit var timerText: TextView
    private lateinit var turnText: TextView
    private lateinit var playersText: TextView
    private lateinit var wordText: TextView
    private lateinit var statusText: TextView
    private lateinit var wholeWordButton: Button

    private var timer: CountDownTimer? = null
    private var wholeWordTimer: CountDownTimer? = null

    private var secretWord = ""

    private val letters =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

    private var currentPlayer = 1
    private var playerCount = 2

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

    private fun createGameScreen(
        gameCode: String,
        maxPlayers: Int,
        wordSelection: String,
        selectedCategory: String,
        nextWordMaster: String
    ) {

        /*
         * ScrollView allows the complete game screen
         * to be reached on smaller phones.
         */
        val scrollView =
            ScrollView(this)

        val layout =
            LinearLayout(this)

        layout.orientation =
            LinearLayout.VERTICAL

        layout.setPadding(
            16,
            10,
            16,
            20
        )

        scrollView.addView(layout)

        /*
         * TITLE
         */
        val title =
            TextView(this)

        title.text =
            "Online Game"

        title.textSize =
            25f

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
            5
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
            17f

        codeText.gravity =
            Gravity.CENTER

        codeText.setTypeface(
            null,
            Typeface.BOLD
        )

        codeText.setTextColor(
            Color.rgb(
                0,
                70,
                140
            )
        )

        codeText.setPadding(
            0,
            2,
            0,
            5
        )

        layout.addView(codeText)

        /*
         * CATEGORY
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
            20f

        categoryBox.gravity =
            Gravity.CENTER

        categoryBox.setTypeface(
            null,
            Typeface.BOLD
        )

        categoryBox.setTextColor(
            Color.rgb(
                0,
                70,
                140
            )
        )

        categoryBox.setPadding(
            8,
            8,
            8,
            8
        )

        layout.addView(
            categoryBox
        )

        /*
         * SETTINGS
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
            14f

        info.gravity =
            Gravity.CENTER

        info.setPadding(
            0,
            5,
            0,
            5
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
            19f

        playersTitle.setTypeface(
            null,
            Typeface.BOLD
        )

        playersTitle.gravity =
            Gravity.CENTER

        playersTitle.setPadding(
            0,
            5,
            0,
            2
        )

        layout.addView(playersTitle)

        /*
         * PLAYER LIST
         */
        playersText =
            TextView(this)

        playersText.textSize =
            16f

        playersText.setPadding(
            0,
            2,
            0,
            5
        )

        updatePlayerList(
            wordSelection
        )

        layout.addView(playersText)

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
            15f

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
            3,
            0,
            5
        )

        layout.addView(wordMasterText)

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
            8,
            0,
            8
        )

        layout.addView(wordText)

        /*
         * CURRENT TURN
         */
        turnText =
            TextView(this)

        turnText.text =
            "PLAYER $currentPlayer'S TURN"

        turnText.textSize =
            21f

        turnText.gravity =
            Gravity.CENTER

        turnText.setTypeface(
            null,
            Typeface.BOLD
        )

        turnText.setTextColor(
            Color.rgb(
                0,
                100,
                0
            )
        )

        turnText.setPadding(
            0,
            4,
            0,
            2
        )

        layout.addView(turnText)

        /*
         * TIMER
         */
        timerText =
            TextView(this)

        timerText.text =
            getTimerDisplay()

        timerText.textSize =
            23f

        timerText.gravity =
            Gravity.CENTER

        timerText.setTypeface(
            null,
            Typeface.BOLD
        )

        timerText.setPadding(
            0,
            2,
            0,
            3
        )

        layout.addView(timerText)

        /*
         * WHOLE WORD BUTTON
         *
         * MOVED UP so it is easy to see.
         */
        wholeWordButton =
            Button(this)

        wholeWordButton.text =
            "GUESS WHOLE WORD"

        wholeWordButton.textSize =
            17f

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
         * STATUS
         */
        statusText =
            TextView(this)

        statusText.text =
            "Choose a letter"

        statusText.textSize =
            15f

        statusText.gravity =
            Gravity.CENTER

        statusText.setTypeface(
            null,
            Typeface.BOLD
        )

        statusText.setTextColor(
            Color.DKGRAY
        )

        statusText.setPadding(
            4,
            3,
            4,
            5
        )

        layout.addView(statusText)

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
                13f

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

                    showStatus(
                        "Already guessed.",
                        Color.RED
                    )

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

                    showStatus(
                        "Correct! You earned $occurrences point(s).",
                        Color.rgb(
                            0,
                            100,
                            0
                        )
                    )

                    if (
                        isWordComplete()
                    ) {

                        finishRound(
                            "Player $currentPlayer completed the word and wins!"
                        )

                    } else {

                        wholeWordAttemptUsed =
                            false

                        startTurnTimer(
                            secondsPerTurn,
                            wordSelection
                        )
                    }

                } else {

                    showStatus(
                        "Wrong letter.",
                        Color.RED
                    )

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
         * LEAVE GAME
         */
        val leaveButton =
            Button(this)

        leaveButton.text =
            "Leave Game"

        leaveButton.textSize =
            16f

        leaveButton.setOnClickListener {

            timer?.cancel()
            wholeWordTimer?.cancel()

            finish()
        }

        layout.addView(
            leaveButton
        )

        setContentView(scrollView)
    }

    private fun showStatus(
        message: String,
        color: Int
    ) {

        statusText.text =
            message

        statusText.setTextColor(
            color
        )

        statusText.setTypeface(
            null,
            Typeface.BOLD
        )
    }

    private fun isUnlimited(): Boolean {

        return secondsPerTurn <= 0
    }

    private fun getTimerDisplay(): String {

        return if (
            isUnlimited()
        ) {

            "Time: Unlimited"

        } else {

            "Time: $remainingTurnSeconds"
        }
    }

    private fun getTurnTimeDisplay(): String {

        return if (
            isUnlimited()
        ) {

            "Unlimited"

        } else {

            "$secondsPerTurn seconds"
        }
    }

    private fun chooseWord(
        wordSelection: String,
        selectedCategory: String,
        manualWord: String?
    ) {

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

    private fun updatePlayerList(
        wordSelection: String
    ) {

        playersText.text =
            buildPlayerList(
                wordSelection
            )

        playersText.setTypeface(
            null,
            Typeface.BOLD
        )
    }

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

                showStatus(
                    "Player $currentPlayer is eliminated after 3 missed turns.",
                    Color.RED
                )

            } else {

                showStatus(
                    "Player $currentPlayer missed a turn.",
                    Color.RED
                )
            }

        } else {

            showStatus(
                "Time is up.",
                Color.RED
            )
        }

        updatePlayerList(
            wordSelection
        )

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

        turnText.setTextColor(
            Color.rgb(
                0,
                100,
                0
            )
        )

        updatePlayerList(
            wordSelection
        )

        startTurnTimer(
            secondsPerTurn,
            wordSelection
        )
    }

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

    private fun showWholeWordDialog(
        wordSelection: String
    ) {

        if (
            wholeWordAttemptUsed
        ) {

            showStatus(
                "You already used your whole-word guess this turn.",
                Color.RED
            )

            return
        }

        wholeWordAttemptUsed =
            true

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

                        showStatus(
                            "Whole-word time is up.",
                            Color.RED
                        )

                        moveToNextPlayer(
                            wordSelection
                        )
                    }
                }
                .start()

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

                    showStatus(
                        "Wrong whole-word guess!",
                        Color.RED
                    )

                    moveToNextPlayer(
                        wordSelection
                    )
                }
            }
        }

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
}

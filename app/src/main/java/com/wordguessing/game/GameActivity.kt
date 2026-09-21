package com.wordguessing.game

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.graphics.Color
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.app.AlertDialog
import kotlin.random.Random

class GameActivity : Activity() {

    // =========================
    // WORD BANK
    // =========================

    private val wordBanks = mapOf(

        "Animals" to listOf(
            "ELEPHANT",
            "GIRAFFE",
            "KANGAROO",
            "DOLPHIN",
            "TIGER",
            "PENGUIN",
            "CROCODILE",
            "BUTTERFLY",
            "CHEETAH",
            "GORILLA"
        ),

        "Food" to listOf(
            "PIZZA",
            "HAMBURGER",
            "CHOCOLATE",
            "ICE CREAM",
            "PANCAKES",
            "SPAGHETTI",
            "WATERMELON",
            "POPCORN",
            "SANDWICH",
            "STRAWBERRY"
        ),

        "Places" to listOf(
            "NEW YORK",
            "CHICAGO",
            "LOS ANGELES",
            "LONDON",
            "PARIS",
            "DUBAI",
            "NEW DELHI",
            "GRAND CANYON",
            "LAS VEGAS",
            "DISNEY WORLD"
        ),

        "Sports" to listOf(
            "BASKETBALL",
            "FOOTBALL",
            "BASEBALL",
            "SOCCER",
            "TENNIS",
            "VOLLEYBALL",
            "SWIMMING",
            "BOXING",
            "GOLF",
            "ICE HOCKEY"
        ),

        "Movies" to listOf(
            "THE LION KING",
            "TOY STORY",
            "HOME ALONE",
            "JURASSIC PARK",
            "STAR WARS",
            "THE MATRIX",
            "AVATAR",
            "FROZEN",
            "SPIDER MAN",
            "SUPERMAN"
        ),

        "Things" to listOf(
            "TELEVISION",
            "COMPUTER",
            "TELEPHONE",
            "BICYCLE",
            "UMBRELLA",
            "BACKPACK",
            "TOOTHBRUSH",
            "REFRIGERATOR",
            "KEYBOARD",
            "AIRPLANE"
        )
    )

    private var testWord = ""

    private val selectedLetters = mutableSetOf<Char>()

    private lateinit var wordText: TextView
    private lateinit var turnText: TextView
    private lateinit var scoresText: TextView
    private lateinit var timerText: TextView
    private lateinit var lettersLayout: LinearLayout
    private lateinit var fullWordButton: Button

    private var currentPlayer = 0

    private val scores = mutableListOf<Int>()

    private val missedTurns = mutableListOf<Int>()

    private val eliminatedPlayers = mutableSetOf<Int>()

    private var playerNames = arrayListOf<String>()

    private var secondsPerTurn = 10

    private var timeLeft = 10

    private var roundFinished = false

    private var selectedCategory = "Random"

    private var previousWord = ""

    private val handler =
        Handler(Looper.getMainLooper())

    // =========================
    // TIMER
    // =========================

    private val timerRunnable =
        object : Runnable {

            override fun run() {

                if (roundFinished) {
                    return
                }

                timeLeft--

                timerText.text =
                    "Time: $timeLeft"

                if (timeLeft <= 0) {

                    handleTimeExpired()

                } else {

                    handler.postDelayed(
                        this,
                        1000
                    )
                }
            }
        }

    // =========================
    // ON CREATE
    // =========================

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        selectedCategory =
            intent.getStringExtra(
                "category"
            ) ?: "Random"

        secondsPerTurn =
            intent.getIntExtra(
                "secondsPerTurn",
                10
            )

        playerNames =
            intent.getStringArrayListExtra(
                "playerNames"
            ) ?: arrayListOf(
                "Player 1",
                "Player 2"
            )

        if (playerNames.isEmpty()) {

            playerNames.add("Player 1")
            playerNames.add("Player 2")
        }

        for (i in playerNames.indices) {

            scores.add(0)

            missedTurns.add(0)
        }

        chooseNewWord()

        createGameScreen()

        startTimer()
    }

    // =========================
    // CHOOSE NEW WORD
    // =========================

    private fun chooseNewWord() {

        val availableWords =
            mutableListOf<String>()

        if (selectedCategory == "Random") {

            for (words in wordBanks.values) {

                availableWords.addAll(words)
            }

        } else {

            availableWords.addAll(
                wordBanks[selectedCategory]
                    ?: wordBanks["Things"]!!
            )
        }

        // Prevent the same word from appearing
        // twice in a row when possible.

        if (availableWords.size > 1) {

            availableWords.remove(
                previousWord
            )
        }

        if (availableWords.isEmpty()) {

            availableWords.add("APPLE")
        }

        testWord =
            availableWords[
                Random.nextInt(
                    availableWords.size
                )
            ]

        previousWord = testWord

        selectedLetters.clear()
    }

    // =========================
    // CREATE GAME SCREEN
    // =========================

    private fun createGameScreen() {

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
            "Word Guessing Game"

        title.textSize =
            26f

        title.gravity =
            Gravity.CENTER

        title.setTextColor(
            Color.BLACK
        )

        layout.addView(title)

        val categoryText =
            TextView(this)

        categoryText.text =
            "Category: $selectedCategory"

        categoryText.textSize =
            20f

        layout.addView(
            categoryText
        )

        turnText =
            TextView(this)

        turnText.textSize =
            20f

        turnText.setPadding(
            0,
            15,
            0,
            10
        )

        layout.addView(
            turnText
        )

        scoresText =
            TextView(this)

        scoresText.textSize =
            18f

        scoresText.setPadding(
            0,
            10,
            0,
            15
        )

        layout.addView(
            scoresText
        )

        timerText =
            TextView(this)

        timerText.textSize =
            22f

        timerText.gravity =
            Gravity.CENTER

        timerText.setPadding(
            0,
            5,
            0,
            10
        )

        layout.addView(
            timerText
        )

        wordText =
            TextView(this)

        wordText.textSize =
            32f

        wordText.gravity =
            Gravity.CENTER

        wordText.setPadding(
            0,
            20,
            0,
            20
        )

        layout.addView(
            wordText
        )

        val lettersTitle =
            TextView(this)

        lettersTitle.text =
            "Choose a letter"

        lettersTitle.textSize =
            20f

        layout.addView(
            lettersTitle
        )

        lettersLayout =
            LinearLayout(this)

        lettersLayout.orientation =
            LinearLayout.VERTICAL

        layout.addView(
            lettersLayout
        )

        fullWordButton =
            Button(this)

        fullWordButton.text =
            "Guess Whole Word"

        fullWordButton.textSize =
            18f

        layout.addView(
            fullWordButton
        )

        fullWordButton.setOnClickListener {

            showWholeWordDialog()
        }

        setContentView(layout)

        updateTurnAndScores()

        updateWordDisplay()

        createLetterButtons()
    }

    // =========================
    // LETTER BUTTONS
    // =========================

    private fun createLetterButtons() {

        lettersLayout.removeAllViews()

        val alphabet =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

        for (
            rowStart in
            0 until alphabet.length
            step 6
        ) {

            val row =
                LinearLayout(this)

            row.orientation =
                LinearLayout.HORIZONTAL

            val rowEnd =
                minOf(
                    rowStart + 6,
                    alphabet.length
                )

            for (
                i in rowStart until rowEnd
            ) {

                val letter =
                    alphabet[i]

                val letterButton =
                    Button(this)

                letterButton.text =
                    letter.toString()

                letterButton.textSize =
                    16f

                if (
                    selectedLetters.contains(
                        letter
                    )
                ) {

                    letterButton.setTextColor(
                        Color.RED
                    )

                } else {

                    letterButton.setTextColor(
                        Color.GREEN
                    )
                }

                row.addView(
                    letterButton,
                    LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                    )
                )

                letterButton.setOnClickListener {

                    if (roundFinished) {
                        return@setOnClickListener
                    }

                    if (
                        selectedLetters.contains(
                            letter
                        )
                    ) {

                        Toast.makeText(
                            this,
                            "Already guessed",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@setOnClickListener
                    }

                    selectedLetters.add(
                        letter
                    )

                    letterButton.setTextColor(
                        Color.RED
                    )

                    if (
                        testWord.contains(
                            letter
                        )
                    ) {

                        var pointsEarned = 0

                        for (
                            wordLetter in testWord
                        ) {

                            if (
                                wordLetter ==
                                letter
                            ) {

                                pointsEarned++
                            }
                        }

                        scores[currentPlayer] +=
                            pointsEarned

                        Toast.makeText(
                            this,
                            "${playerNames[currentPlayer]} gets $pointsEarned point(s)!",
                            Toast.LENGTH_SHORT
                        ).show()

                        updateWordDisplay()

                        updateTurnAndScores()

                        if (isWordSolved()) {

                            finishRound(
                                playerNames[
                                    currentPlayer
                                ]
                            )

                            return@setOnClickListener
                        }

                        restartTimer()

                    } else {

                        Toast.makeText(
                            this,
                            "Wrong letter!",
                            Toast.LENGTH_SHORT
                        ).show()

                        moveToNextPlayer()
                    }
                }
            }

            lettersLayout.addView(row)
        }
    }

    // =========================
    // WHOLE WORD GUESS
    // =========================

    private fun showWholeWordDialog() {

        if (roundFinished) {
            return
        }

        val input =
            EditText(this)

        input.hint =
            "Enter the whole word"

        input.textSize =
            20f

        val padding = 40

        input.setPadding(
            padding,
            20,
            padding,
            20
        )

        val dialog =
            AlertDialog.Builder(this)
                .setTitle(
                    "${playerNames[currentPlayer]}'s Guess"
                )
                .setMessage(
                    "Enter your whole-word guess:"
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

                if (guess.isEmpty()) {

                    Toast.makeText(
                        this,
                        "Please enter a word",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setOnClickListener
                }

                if (guess == testWord) {

                    dialog.dismiss()

                    selectedLetters.addAll(
                        testWord.toSet()
                    )

                    updateWordDisplay()

                    finishRound(
                        playerNames[currentPlayer]
                    )

                } else {

                    dialog.dismiss()

                    Toast.makeText(
                        this,
                        "Wrong whole-word guess!",
                        Toast.LENGTH_SHORT
                    ).show()

                    moveToNextPlayer()
                }
            }
        }

        dialog.show()
    }

    // =========================
    // CHECK SOLVED
    // =========================

private fun isWordSolved(): Boolean {

    for (letter in testWord) {

        if (letter == ' ') {
            continue
        }

        if (!selectedLetters.contains(letter)) {
            return false
        }
    }

    return true
}

    // =========================
    // TIME EXPIRED
    // =========================

    private fun handleTimeExpired() {

        if (roundFinished) {
            return
        }

        timeLeft = 0

        timerText.text =
            "Time: 0"

        missedTurns[currentPlayer]++

        val player =
            playerNames[currentPlayer]

        val misses =
            missedTurns[currentPlayer]

        if (
            playerNames.size >= 3 &&
            misses >= 3
        ) {

            eliminatedPlayers.add(
                currentPlayer
            )

            Toast.makeText(
                this,
                "$player is eliminated after 3 missed turns!",
                Toast.LENGTH_LONG
            ).show()

            if (
                getActivePlayerCount() <= 1
            ) {

                val winner =
                    getLastActivePlayer()

                if (winner != -1) {

                    finishRound(
                        playerNames[winner]
                    )
                }

                return
            }

        } else {

            Toast.makeText(
                this,
                "$player missed the turn!",
                Toast.LENGTH_SHORT
            ).show()
        }

        moveToNextPlayer()
    }

    // =========================
    // NEXT PLAYER
    // =========================

    private fun moveToNextPlayer() {

        handler.removeCallbacks(
            timerRunnable
        )

        if (roundFinished) {
            return
        }

        var attempts = 0

        do {

            currentPlayer++

            if (
                currentPlayer >=
                playerNames.size
            ) {

                currentPlayer = 0
            }

            attempts++

        } while (
            eliminatedPlayers.contains(
                currentPlayer
            ) &&
            attempts <= playerNames.size
        )

        updateTurnAndScores()

        restartTimer()
    }

    // =========================
    // TIMER
    // =========================

    private fun startTimer() {

        timeLeft =
            secondsPerTurn

        timerText.text =
            "Time: $timeLeft"

        handler.removeCallbacks(
            timerRunnable
        )

        handler.postDelayed(
            timerRunnable,
            1000
        )
    }

    private fun restartTimer() {

        startTimer()
    }

    // =========================
    // FINISH ROUND
    // =========================

    private fun finishRound(
        winner: String
    ) {

        if (roundFinished) {
            return
        }

        roundFinished = true

        handler.removeCallbacks(
            timerRunnable
        )

        wordText.text =
            testWord

        lettersLayout.removeAllViews()

        fullWordButton.isEnabled =
            false

        turnText.text =
            "🎉 $winner wins!"

        timerText.text =
            "Round complete"

        val parent =
            wordText.parent as LinearLayout

        val winnerText =
            TextView(this)

        winnerText.text =
            "$winner solved the word!\n\nThe word was: $testWord"

        winnerText.textSize =
            22f

        winnerText.gravity =
            Gravity.CENTER

        winnerText.setPadding(
            0,
            20,
            0,
            20
        )

        parent.addView(
            winnerText
        )

        val nextWordButton =
            Button(this)

        nextWordButton.text =
            "Next Word"

        nextWordButton.textSize =
            20f

        parent.addView(
            nextWordButton
        )

        nextWordButton.setOnClickListener {

            startNextRound(
                parent,
                winnerText,
                nextWordButton
            )
        }
    }

    // =========================
    // NEXT ROUND
    // =========================

    private fun startNextRound(
        parent: LinearLayout,
        winnerText: TextView,
        nextWordButton: Button
    ) {

        parent.removeView(
            winnerText
        )

        parent.removeView(
            nextWordButton
        )

        selectedLetters.clear()

        eliminatedPlayers.clear()

        for (
            i in missedTurns.indices
        ) {

            missedTurns[i] = 0
        }

        chooseNewWord()

        currentPlayer++

        if (
            currentPlayer >=
            playerNames.size
        ) {

            currentPlayer = 0
        }

        roundFinished = false

        fullWordButton.isEnabled =
            true

        updateWordDisplay()

        updateTurnAndScores()

        createLetterButtons()

        startTimer()
    }

    // =========================
    // ACTIVE PLAYERS
    // =========================

    private fun getActivePlayerCount(): Int {

        var count = 0

        for (
            i in playerNames.indices
        ) {

            if (
                !eliminatedPlayers.contains(i)
            ) {

                count++
            }
        }

        return count
    }

    // =========================
    // LAST ACTIVE PLAYER
    // =========================

    private fun getLastActivePlayer(): Int {

        for (
            i in playerNames.indices
        ) {

            if (
                !eliminatedPlayers.contains(i)
            ) {

                return i
            }
        }

        return -1
    }

    // =========================
    // WORD DISPLAY
    // =========================

    private fun updateWordDisplay() {

        val display =
            StringBuilder()

        for (letter in testWord) {

            if (letter == ' ') {

                display.append("   ")

            } else if (
                selectedLetters.contains(
                    letter
                )
            ) {

                display.append(letter)
                display.append(" ")

            } else {

                display.append("_ ")
            }
        }

        wordText.text =
            display.toString().trim()
    }

    // =========================
    // SCORES
    // =========================

    private fun updateTurnAndScores() {

        if (
            eliminatedPlayers.contains(
                currentPlayer
            )
        ) {

            return
        }

        turnText.text =
            "Turn: ${playerNames[currentPlayer]}"

        val scoreDisplay =
            StringBuilder()

        for (
            i in playerNames.indices
        ) {

            scoreDisplay.append(
                "${playerNames[i]}: ${scores[i]} points"
            )

            if (
                eliminatedPlayers.contains(i)
            ) {

                scoreDisplay.append(
                    " (Eliminated)"
                )
            }

            scoreDisplay.append(
                " | Missed: ${missedTurns[i]}"
            )

            if (
                i < playerNames.size - 1
            ) {

                scoreDisplay.append(
                    "\n"
                )
            }
        }

        scoresText.text =
            scoreDisplay.toString()
    }

    // =========================
    // CLEAN UP
    // =========================

    override fun onDestroy() {

        handler.removeCallbacks(
            timerRunnable
        )

        super.onDestroy()
    }
}

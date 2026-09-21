package com.wordguessing.game

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.app.AlertDialog
import kotlin.random.Random

class GameActivity : Activity() {

    private var testWord = ""

    private val selectedLetters = mutableSetOf<Char>()

    private lateinit var wordText: TextView
    private lateinit var categoryText: TextView
    private lateinit var turnText: TextView
    private lateinit var scoresText: TextView
    private lateinit var timerText: TextView
    private lateinit var lettersLayout: LinearLayout
    private lateinit var fullWordButton: Button
    private lateinit var wholeWordTimerText: TextView

    private var currentPlayer = 0

    private val scores = mutableListOf<Int>()

    private val missedTurns = mutableListOf<Int>()

    private val eliminatedPlayers = mutableSetOf<Int>()

    private var playerNames = arrayListOf<String>()

    private var secondsPerTurn = 10

    private var timeLeft = 10

    private var roundFinished = false

    private var selectedCategory = "Random"

    private var actualCategory = ""

    private var previousWord = ""

    // Only one whole-word attempt is allowed per turn.
    private var wholeWordAttemptUsed = false

    // True while the whole-word dialog/timer is active.
    private var wholeWordGuessInProgress = false

    private var wholeWordTimeLeft = 10

    private val handler =
        Handler(Looper.getMainLooper())

    // =========================
    // NORMAL TURN TIMER
    // =========================

    private val timerRunnable =
        object : Runnable {

            override fun run() {

                if (roundFinished) {
                    return
                }

                if (wholeWordGuessInProgress) {
                    return
                }

                timeLeft--

                updateTimerDisplay()

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
    // WHOLE WORD TIMER
    // =========================

    private val wholeWordTimerRunnable =
        object : Runnable {

            override fun run() {

                if (
                    roundFinished ||
                    !wholeWordGuessInProgress
                ) {
                    return
                }

                wholeWordTimeLeft--

                wholeWordTimerText.text =
                    "Whole-word time: $wholeWordTimeLeft"

                if (wholeWordTimeLeft <= 3) {

                    wholeWordTimerText.setTypeface(
                        null,
                        Typeface.BOLD
                    )

                    wholeWordTimerText.setTextColor(
                        Color.RED
                    )

                } else {

                    wholeWordTimerText.setTypeface(
                        null,
                        Typeface.BOLD
                    )

                    wholeWordTimerText.setTextColor(
                        Color.BLACK
                    )
                }

                if (wholeWordTimeLeft <= 0) {

                    wholeWordGuessInProgress =
                        false

                    Toast.makeText(
                        this@GameActivity,
                        "Whole-word time expired!",
                        Toast.LENGTH_SHORT
                    ).show()

                    moveToNextPlayer()

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
                WordBank.categories[selectedCategory]
                    ?: WordBank.categories["Things"]!!
            )
        }

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
    // CATEGORY DISPLAY
    // =========================

    private fun updateCategoryDisplay() {

        categoryText.text =
            if (selectedCategory == "Random") {
                "Category: Random: $actualCategory"
            } else {
                "Category: $actualCategory"
            }
    }

    // =========================
    // NORMAL TIMER DISPLAY
    // =========================

    private fun updateTimerDisplay() {

        timerText.text =
            "Time: $timeLeft"

        if (timeLeft <= 3) {

            timerText.setTypeface(
                null,
                Typeface.BOLD
            )

            timerText.setTextColor(
                Color.RED
            )

        } else {

            timerText.setTypeface(
                null,
                Typeface.BOLD
            )

            timerText.setTextColor(
                Color.BLACK
            )
        }
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
            20,
            20,
            20,
            20
        )

        // -------------------------
        // TITLE
        // -------------------------

        val title =
            TextView(this)

        title.text =
            "Word Guessing Game"

        title.textSize =
            26f

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
            15
        )

        layout.addView(title)

        // -------------------------
        // CATEGORY + TIMER ROW
        // -------------------------

        val topRow =
            LinearLayout(this)

        topRow.orientation =
            LinearLayout.HORIZONTAL

        topRow.gravity =
            Gravity.CENTER_VERTICAL

        categoryText =
            TextView(this)

        categoryText.textSize =
            18f

        categoryText.setTypeface(
            null,
            Typeface.BOLD
        )

        categoryText.setPadding(
            0,
            5,
            5,
            5
        )

        topRow.addView(
            categoryText,
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        timerText =
            TextView(this)

        timerText.textSize =
            20f

        timerText.gravity =
            Gravity.CENTER

        timerText.setTypeface(
            null,
            Typeface.BOLD
        )

        topRow.addView(
            timerText,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        layout.addView(topRow)

        updateCategoryDisplay()

        // -------------------------
        // TURN
        // -------------------------

        turnText =
            TextView(this)

        turnText.textSize =
            22f

        turnText.gravity =
            Gravity.CENTER

        turnText.setTypeface(
            null,
            Typeface.BOLD
        )

        turnText.setPadding(
            0,
            15,
            0,
            12
        )

        layout.addView(
            turnText
        )

        // -------------------------
        // SCORES
        // -------------------------

        scoresText =
            TextView(this)

        scoresText.textSize =
            17f

        scoresText.setPadding(
            0,
            5,
            0,
            12
        )

        layout.addView(
            scoresText
        )

        // -------------------------
        // WORD
        // -------------------------

        wordText =
            TextView(this)

        wordText.textSize =
            32f

        wordText.gravity =
            Gravity.CENTER

        wordText.setTypeface(
            null,
            Typeface.BOLD
        )

        wordText.setPadding(
            0,
            20,
            0,
            20
        )

        layout.addView(
            wordText,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        // -------------------------
        // LETTER TITLE
        // -------------------------

        val lettersTitle =
            TextView(this)

        lettersTitle.text =
            "Choose a letter"

        lettersTitle.textSize =
            19f

        lettersTitle.gravity =
            Gravity.CENTER

        lettersTitle.setTypeface(
            null,
            Typeface.BOLD
        )

        lettersTitle.setPadding(
            0,
            5,
            0,
            8
        )

        layout.addView(
            lettersTitle
        )

        // -------------------------
        // LETTERS
        // -------------------------

        lettersLayout =
            LinearLayout(this)

        lettersLayout.orientation =
            LinearLayout.VERTICAL

        layout.addView(
            lettersLayout
        )

        // -------------------------
        // WHOLE WORD BUTTON
        // -------------------------

        fullWordButton =
            Button(this)

        fullWordButton.text =
            "Guess Whole Word"

        fullWordButton.textSize =
            18f

        fullWordButton.setTypeface(
            null,
            Typeface.BOLD
        )

        layout.addView(
            fullWordButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        fullWordButton.setOnClickListener {

            if (!roundFinished) {

                showWholeWordDialog()
            }
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

            row.gravity =
                Gravity.CENTER

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
                    17f

                letterButton.setTypeface(
                    null,
                    Typeface.BOLD
                )

                letterButton.setPadding(
                    0,
                    5,
                    0,
                    5
                )

                if (
                    selectedLetters.contains(
                        letter
                    )
                ) {

                    // Selected letters = RED
                    letterButton.setTextColor(
                        Color.RED
                    )

                } else {

                    // Unselected letters = DARK GREEN
                    letterButton.setTextColor(
                        Color.rgb(
                            0,
                            100,
                            0
                        )
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

                    if (wholeWordGuessInProgress) {
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

        // Only one attempt per turn.
        if (wholeWordAttemptUsed) {

            Toast.makeText(
                this,
                "You already used your whole-word guess this turn.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // Mark the attempt as used immediately.
        wholeWordAttemptUsed = true

        // Pause the normal timer.
        handler.removeCallbacks(
            timerRunnable
        )

        wholeWordGuessInProgress = true

        wholeWordTimeLeft = 10

        // Disable the button so it cannot be used again
        // during this turn.
        fullWordButton.isEnabled = false

        wholeWordTimerText =
            TextView(this)

        wholeWordTimerText.text =
            "Whole-word time: 10"

        wholeWordTimerText.textSize =
            22f

        wholeWordTimerText.gravity =
            Gravity.CENTER

        wholeWordTimerText.setTypeface(
            null,
            Typeface.BOLD
        )

        wholeWordTimerText.setTextColor(
            Color.BLACK
        )

        wholeWordTimerText.setPadding(
            0,
            10,
            0,
            15
        )

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

        val dialogLayout =
            LinearLayout(this)

        dialogLayout.orientation =
            LinearLayout.VERTICAL

        dialogLayout.addView(
            wholeWordTimerText
        )

        dialogLayout.addView(
            input
        )

        val dialog =
            AlertDialog.Builder(this)
                .setTitle(
                    "${playerNames[currentPlayer]}'s Guess"
                )
                .setMessage(
                    "You have 10 seconds to guess the whole word:"
                )
                .setView(dialogLayout)
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

            // -------------------------
            // START WHOLE WORD TIMER
            // -------------------------

            handler.removeCallbacks(
                wholeWordTimerRunnable
            )

            handler.postDelayed(
                wholeWordTimerRunnable,
                1000
            )

            // -------------------------
            // CANCEL
            // -------------------------

            dialog.getButton(
                AlertDialog.BUTTON_NEGATIVE
            ).setOnClickListener {

                handler.removeCallbacks(
                    wholeWordTimerRunnable
                )

                wholeWordGuessInProgress =
                    false

                dialog.dismiss()

                Toast.makeText(
                    this,
                    "Whole-word guess canceled. You cannot use it again this turn.",
                    Toast.LENGTH_SHORT
                ).show()

                // Resume the normal timer
                // from the remaining time.
                resumeNormalTimer()
            }

            // -------------------------
            // GUESS
            // -------------------------

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

                handler.removeCallbacks(
                    wholeWordTimerRunnable
                )

                wholeWordGuessInProgress =
                    false

                dialog.dismiss()

                if (guess == testWord) {

                    selectedLetters.addAll(
                        testWord.toSet()
                    )

                    updateWordDisplay()

                    finishRound(
                        playerNames[currentPlayer]
                    )

                } else {

                    Toast.makeText(
                        this,
                        "Wrong whole-word guess!",
                        Toast.LENGTH_SHORT
                    ).show()

                    moveToNextPlayer()
                }
            }
        }

        dialog.setOnCancelListener {

            handler.removeCallbacks(
                wholeWordTimerRunnable
            )

            wholeWordGuessInProgress =
                false

            // If the dialog is dismissed by the
            // Android back button, resume the timer.
            if (!roundFinished) {

                resumeNormalTimer()
            }
        }

        dialog.show()
    }

    // =========================
    // RESUME NORMAL TIMER
    // =========================

    private fun resumeNormalTimer() {

        if (roundFinished) {
            return
        }

        if (timeLeft <= 0) {

            handleTimeExpired()

            return
        }

        updateTimerDisplay()

        handler.removeCallbacks(
            timerRunnable
        )

        handler.postDelayed(
            timerRunnable,
            1000
        )
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

        updateTimerDisplay()

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

        handler.removeCallbacks(
            wholeWordTimerRunnable
        )

        wholeWordGuessInProgress =
            false

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

        // New turn = new whole-word opportunity.
        wholeWordAttemptUsed = false

        fullWordButton.isEnabled =
            true

        updateTurnAndScores()

        restartTimer()
    }

    // =========================
    // TIMER
    // =========================

    private fun startTimer() {

        timeLeft =
            secondsPerTurn

        updateTimerDisplay()

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

        handler.removeCallbacks(
            wholeWordTimerRunnable
        )

        wholeWordGuessInProgress =
            false

        wordText.text =
            testWord

        wordText.textSize =
            30f

        lettersLayout.removeAllViews()

        fullWordButton.isEnabled =
            false

        turnText.text =
            "🎉 $winner wins!"

        timerText.text =
            "Round complete"

        timerText.setTextColor(
            Color.BLACK
        )

        val parent =
            wordText.parent as LinearLayout

        val winnerText =
            TextView(this)

        winnerText.text =
            "$winner solved the word!\n\nThe word was:\n$testWord"

        winnerText.textSize =
            22f

        winnerText.gravity =
            Gravity.CENTER

        winnerText.setTypeface(
            null,
            Typeface.BOLD
        )

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

        nextWordButton.setTypeface(
            null,
            Typeface.BOLD
        )

        parent.addView(
            nextWordButton
        )

        nextWordButton.setOnClickListener {

            if (!roundFinished) {
                return@setOnClickListener
            }

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

        wholeWordAttemptUsed = false

        wholeWordGuessInProgress = false

        fullWordButton.isEnabled =
            true

        updateCategoryDisplay()

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

        handler.removeCallbacks(
            wholeWordTimerRunnable
        )

        super.onDestroy()
    }
}

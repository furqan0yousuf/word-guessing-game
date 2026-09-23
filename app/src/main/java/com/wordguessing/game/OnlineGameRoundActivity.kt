package com.wordguessing.game

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
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class OnlineGameRoundActivity : AppCompatActivity() {

    private lateinit var gameCodeText: TextView
    private lateinit var categoryText: TextView
    private lateinit var infoText: TextView
    private lateinit var playersText: TextView
    private lateinit var wordMasterText: TextView
    private lateinit var wordText: TextView
    private lateinit var turnText: TextView
    private lateinit var timerText: TextView
    private lateinit var statusText: TextView
    private lateinit var wholeWordButton: Button
    private lateinit var letterContainer: LinearLayout

    private var gameCode = ""
    private var playerCount = 2
    private var maxPlayers = 2
    private var wordSelection = "Random Word"
    private var category = "Random"
    private var secondsPerTurn = 20
    private var totalTurns = 0
    private var nextWordMaster = "Winner becomes Word Master"
    private var manualWord = ""

    private var secretWord = ""
    private var currentPlayer = 1

    private var timer: CountDownTimer? = null
    private var wholeWordTimer: CountDownTimer? = null

    private var normalTimerRunning = false
    private var wholeWordAttemptUsed = false

    private var completedTurns = 0
    private var finalChallengeActive = false
    private var finalChallengeIndex = 0

    private val guessedLetters = mutableSetOf<Char>()
    private val revealedPositions = mutableSetOf<Int>()

    private val scores = mutableMapOf<Int, Int>()
    private val misses = mutableMapOf<Int, Int>()
    private val eliminatedPlayers = mutableSetOf<Int>()

    private val finalChallengePlayers = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gameCode = intent.getStringExtra("gameCode") ?: "------"
        playerCount = intent.getIntExtra("playerCount", 2)
        maxPlayers = intent.getIntExtra("maxPlayers", playerCount)
        wordSelection = intent.getStringExtra("wordSelection") ?: "Random Word"
        category = intent.getStringExtra("category") ?: "Random"
        secondsPerTurn = intent.getIntExtra("secondsPerTurn", 20)
        totalTurns = intent.getIntExtra("totalTurns", 0)
        nextWordMaster =
            intent.getStringExtra("nextWordMaster") ?: "Winner becomes Word Master"
        manualWord = intent.getStringExtra("manualWord") ?: ""

        buildScreen()
        chooseWord()
        initializePlayers()
        updateScreen()
        startTurnTimer()
    }

    private fun buildScreen() {

        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setPadding(20, 20, 20, 20)

        val scrollLayout = LinearLayout(this)
        scrollLayout.orientation = LinearLayout.VERTICAL

        val title = TextView(this)
        title.text = "ONLINE GAME"
        title.textSize = 28f
        title.setTypeface(null, Typeface.BOLD)
        title.gravity = Gravity.CENTER
        title.setPadding(0, 0, 0, 12)
        scrollLayout.addView(title)

        gameCodeText = TextView(this)
        gameCodeText.textSize = 18f
        gameCodeText.gravity = Gravity.CENTER
        gameCodeText.setTypeface(null, Typeface.BOLD)
        scrollLayout.addView(gameCodeText)

        categoryText = TextView(this)
        categoryText.textSize = 24f
        categoryText.gravity = Gravity.CENTER
        categoryText.setTypeface(null, Typeface.BOLD)
        categoryText.setTextColor(Color.rgb(0, 110, 200))
        categoryText.setPadding(0, 15, 0, 15)
        scrollLayout.addView(categoryText)

        infoText = TextView(this)
        infoText.textSize = 15f
        infoText.gravity = Gravity.CENTER
        scrollLayout.addView(infoText)

        playersText = TextView(this)
        playersText.textSize = 17f
        playersText.setTypeface(null, Typeface.BOLD)
        playersText.gravity = Gravity.CENTER
        playersText.setPadding(0, 10, 0, 5)
        scrollLayout.addView(playersText)

        wordMasterText = TextView(this)
        wordMasterText.textSize = 16f
        wordMasterText.gravity = Gravity.CENTER
        scrollLayout.addView(wordMasterText)

        wordText = TextView(this)
        wordText.textSize = 32f
        wordText.setTypeface(null, Typeface.BOLD)
        wordText.gravity = Gravity.CENTER
        wordText.setPadding(0, 20, 0, 15)
        scrollLayout.addView(wordText)

        turnText = TextView(this)
        turnText.textSize = 20f
        turnText.setTypeface(null, Typeface.BOLD)
        turnText.gravity = Gravity.CENTER
        scrollLayout.addView(turnText)

        timerText = TextView(this)
        timerText.textSize = 26f
        timerText.setTypeface(null, Typeface.BOLD)
        timerText.gravity = Gravity.CENTER
        timerText.setPadding(0, 8, 0, 8)
        scrollLayout.addView(timerText)

        wholeWordButton = Button(this)
        wholeWordButton.text = "GUESS WHOLE WORD"
        wholeWordButton.textSize = 17f
        wholeWordButton.setOnClickListener {
            showWholeWordDialog()
        }
        scrollLayout.addView(
            wholeWordButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        statusText = TextView(this)
        statusText.textSize = 17f
        statusText.gravity = Gravity.CENTER
        statusText.setPadding(0, 10, 0, 10)
        scrollLayout.addView(statusText)

        val lettersTitle = TextView(this)
        lettersTitle.text = "LETTERS"
        lettersTitle.textSize = 18f
        lettersTitle.setTypeface(null, Typeface.BOLD)
        lettersTitle.gravity = Gravity.CENTER
        scrollLayout.addView(lettersTitle)

        letterContainer = LinearLayout(this)
        letterContainer.orientation = LinearLayout.VERTICAL
        scrollLayout.addView(letterContainer)

        val leaveButton = Button(this)
        leaveButton.text = "LEAVE GAME"
        leaveButton.setOnClickListener {
            showLeaveConfirmation()
        }

        root.addView(
            scrollLayout,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        root.addView(
            leaveButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        setContentView(root)
    }

    private fun chooseWord() {

        if (wordSelection.equals("Manual / Host Chooses Word", ignoreCase = true)) {

            if (manualWord.isNotBlank()) {
                secretWord = manualWord.trim().uppercase(Locale.US)
            } else {
                secretWord = "APPLE"
            }

        } else {

            val selectedCategory =
                if (category.equals("Random", ignoreCase = true)) {
                    WordBank.categories.random()
                } else {
                    category
                }

            val words = WordBank.wordsByCategory[selectedCategory]
                ?: WordBank.wordsByCategory["Things"]
                ?: listOf("APPLE")

            secretWord = words.random().uppercase(Locale.US)

            category = selectedCategory
        }
    }

    private fun initializePlayers() {

        scores.clear()
        misses.clear()
        eliminatedPlayers.clear()

        for (player in 1..playerCount) {
            scores[player] = 0
            misses[player] = 0
        }

        if (
            wordSelection.equals(
                "Manual / Host Chooses Word",
                ignoreCase = true
            )
        ) {
            currentPlayer = if (playerCount >= 2) 2 else 1
        } else {
            currentPlayer = 1
        }
    }

    private fun updateScreen() {

        gameCodeText.text = "Game Code: $gameCode"

        categoryText.text = "CATEGORY: $category"

        val timeText =
            if (secondsPerTurn <= 0) {
                "Unlimited"
            } else {
                "$secondsPerTurn seconds"
            }

        val turnsText =
            if (totalTurns <= 0) {
                "Unlimited"
            } else {
                "$totalTurns Turns"
            }

        infoText.text =
            "Players: $playerCount / $maxPlayers\n" +
                    "Turn Time: $timeText\n" +
                    "Total Turns: $turnsText\n" +
                    "Next Word Master: $nextWordMaster"

        playersText.text = buildPlayersText()

        if (
            wordSelection.equals(
                "Manual / Host Chooses Word",
                ignoreCase = true
            )
        ) {
            wordMasterText.text = "Word Master: Player 1"
        } else {
            wordMasterText.text = "Word Master: None — Everyone Plays"
        }

        wordText.text = buildDisplayedWord()

        if (finalChallengeActive) {
            turnText.text = "FINAL WHOLE-WORD CHALLENGE"
        } else {
            turnText.text = "PLAYER $currentPlayer'S TURN"
        }

        updateLetterBoard()
    }

    private fun buildPlayersText(): String {

        val builder = StringBuilder()
        builder.append("PLAYERS\n")

        for (player in 1..playerCount) {

            val score = scores[player] ?: 0
            val miss = misses[player] ?: 0

            builder.append("Player $player: $score points")

            if (playerCount >= 3) {
                builder.append(" | Misses: $miss")
            }

            if (eliminatedPlayers.contains(player)) {
                builder.append(" — ELIMINATED")
            }

            builder.append("\n")
        }

        return builder.toString().trim()
    }

    private fun buildDisplayedWord(): String {

        val builder = StringBuilder()

        for (i in secretWord.indices) {

            val character = secretWord[i]

            if (character == ' ') {
                builder.append("  ")
            } else if (revealedPositions.contains(i)) {
                builder.append(character)
                builder.append(" ")
            } else {
                builder.append("_ ")
            }
        }

        return builder.toString().trim()
    }

    private fun updateLetterBoard() {

        letterContainer.removeAllViews()

        val alphabet = ('A'..'Z').toList()

        for (rowStart in alphabet.indices step 7) {

            val row = LinearLayout(this)
            row.orientation = LinearLayout.HORIZONTAL
            row.gravity = Gravity.CENTER

            val rowEnd = minOf(rowStart + 7, alphabet.size)

            for (index in rowStart until rowEnd) {

                val letter = alphabet[index]

                val button = Button(this)
                button.text = letter.toString()
                button.textSize = 15f

                val alreadyGuessed = guessedLetters.contains(letter)

                if (alreadyGuessed) {
                    button.setBackgroundColor(Color.RED)
                    button.setTextColor(Color.WHITE)
                    button.isEnabled = false
                } else {
                    button.setBackgroundColor(Color.GREEN)
                    button.setTextColor(Color.WHITE)
                    button.isEnabled = !finalChallengeActive &&
                            !eliminatedPlayers.contains(currentPlayer)

                    button.setOnClickListener {
                        handleLetterGuess(letter)
                    }
                }

                row.addView(
                    button,
                    LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                    )
                )
            }

            letterContainer.addView(row)
        }
    }

    private fun handleLetterGuess(letter: Char) {

        if (finalChallengeActive) return

        if (guessedLetters.contains(letter)) {
            showStatus("Already guessed.", Color.RED)
            return
        }

        guessedLetters.add(letter)

        val positions = mutableListOf<Int>()

        for (i in secretWord.indices) {
            if (secretWord[i] == letter) {
                positions.add(i)
            }
        }

        if (positions.isNotEmpty()) {

            for (position in positions) {
                revealedPositions.add(position)
            }

            val points = positions.size
            scores[currentPlayer] =
                (scores[currentPlayer] ?: 0) + points

            wholeWordAttemptUsed = false
            wholeWordButton.isEnabled = true

            showStatus(
                "Correct! +$points point${if (points == 1) "" else "s"}.",
                Color.rgb(0, 140, 0)
            )

            updateScreen()

            if (isWordSolved()) {
                finishRound(
                    "Player $currentPlayer solved the word and wins!"
                )
                return
            }

            startTurnTimer()

        } else {

            showStatus(
                "Wrong letter.",
                Color.RED
            )

            updateScreen()
            handleMissedTurn()
        }
    }

    private fun isWordSolved(): Boolean {

        for (i in secretWord.indices) {

            if (secretWord[i] == ' ') {
                continue
            }

            if (!revealedPositions.contains(i)) {
                return false
            }
        }

        return true
    }

    private fun handleMissedTurn() {

        timer?.cancel()
        normalTimerRunning = false

        if (playerCount >= 3) {

            val currentMisses = (misses[currentPlayer] ?: 0) + 1
            misses[currentPlayer] = currentMisses

            if (currentMisses >= 3) {
                eliminatedPlayers.add(currentPlayer)

                showStatus(
                    "Player $currentPlayer has been eliminated.",
                    Color.RED
                )

                if (getActivePlayers().size <= 1) {
                    finishRound(
                        "Only one player remains.\n\nThe word was:\n$secretWord"
                    )
                    return
                }
            }
        }

        moveToNextPlayer()
    }

    private fun moveToNextPlayer() {

        timer?.cancel()
        normalTimerRunning = false

        completedTurns++

        if (
            totalTurns > 0 &&
            completedTurns >= totalTurns
        ) {
            startFinalWholeWordChallenge()
            return
        }

        var nextPlayer = currentPlayer

        repeat(playerCount) {

            nextPlayer++

            if (nextPlayer > playerCount) {
                nextPlayer = 1
            }

            val isWordMaster =
                wordSelection.equals(
                    "Manual / Host Chooses Word",
                    ignoreCase = true
                ) && nextPlayer == 1

            if (
                !eliminatedPlayers.contains(nextPlayer) &&
                !isWordMaster
            ) {
                currentPlayer = nextPlayer
                return@repeat
            }
        }

        wholeWordAttemptUsed = false
        wholeWordButton.isEnabled = true

        updateScreen()
        startTurnTimer()
    }

    private fun getActivePlayers(): List<Int> {

        val players = mutableListOf<Int>()

        for (player in 1..playerCount) {

            val isWordMaster =
                wordSelection.equals(
                    "Manual / Host Chooses Word",
                    ignoreCase = true
                ) && player == 1

            if (
                !eliminatedPlayers.contains(player) &&
                !isWordMaster
            ) {
                players.add(player)
            }
        }

        return players
    }

    private fun startTurnTimer() {

        timer?.cancel()

        if (finalChallengeActive) {
            return
        }

        if (secondsPerTurn <= 0) {
            timerText.text = "Time: Unlimited"
            normalTimerRunning = false
            return
        }

        normalTimerRunning = true

        timer = object : CountDownTimer(
            secondsPerTurn * 1000L,
            250L
        ) {

            override fun onTick(millisUntilFinished: Long) {

                val seconds =
                    ((millisUntilFinished + 999L) / 1000L).toInt()

                timerText.text = "Time: $seconds"

                if (seconds <= 3) {
                    timerText.setTextColor(Color.RED)
                } else {
                    timerText.setTextColor(Color.BLACK)
                }
            }

            override fun onFinish() {

                normalTimerRunning = false
                timerText.text = "Time: 0"
                timerText.setTextColor(Color.RED)

                handleMissedTurn()
            }

        }.start()
    }

    private fun showWholeWordDialog() {

        if (finalChallengeActive) return

        if (wholeWordAttemptUsed) {
            showStatus(
                "You already used your whole-word attempt this turn.",
                Color.RED
            )
            return
        }

        wholeWordAttemptUsed = true

        timer?.cancel()
        normalTimerRunning = false

        wholeWordButton.isEnabled = false

        val input = EditText(this)
        input.hint = "Type the entire word"
        input.setSingleLine(true)
        input.textSize = 18f
        input.setPadding(10, 10, 10, 10)

        val timerView = TextView(this)
        timerView.text = "Time: 20"
        timerView.textSize = 22f
        timerView.gravity = Gravity.CENTER
        timerView.setTypeface(null, Typeface.BOLD)
        timerView.setPadding(0, 15, 0, 5)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(30, 10, 30, 5)
        layout.addView(input)
        layout.addView(timerView)

        val dialog = AlertDialog.Builder(this)
            .setTitle("PLAYER $currentPlayer — WHOLE-WORD GUESS")
            .setMessage("Type the entire word and press Guess.")
            .setView(layout)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Guess", null)
            .setCancelable(false)
            .create()

        dialog.setOnShowListener {

            val guessButton =
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)

            val cancelButton =
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)

            guessButton.setOnClickListener {

                val guess =
                    input.text.toString()
                        .trim()
                        .uppercase(Locale.US)

                if (guess.isEmpty()) {

                    input.error = "Please enter the entire word."
                    input.requestFocus()
                    return@setOnClickListener
                }

                wholeWordTimer?.cancel()
                dialog.dismiss()

                if (
                    guess ==
                    secretWord.trim().uppercase(Locale.US)
                ) {

                    finishRound(
                        "Player $currentPlayer guessed the whole word and wins!"
                    )

                } else {

                    showStatus(
                        "Wrong whole-word guess.",
                        Color.RED
                    )

                    moveToNextPlayer()
                }
            }

            cancelButton.setOnClickListener {

                wholeWordTimer?.cancel()
                dialog.dismiss()

                showStatus(
                    "Whole-word guess cancelled.",
                    Color.BLACK
                )

                wholeWordButton.isEnabled = false

                if (secondsPerTurn > 0) {
                    startTurnTimer()
                } else {
                    timerText.text = "Time: Unlimited"
                }
            }

            input.requestFocus()

            wholeWordTimer =
                object : CountDownTimer(20000L, 250L) {

                    override fun onTick(
                        millisUntilFinished: Long
                    ) {

                        val seconds =
                            ((millisUntilFinished + 999L) / 1000L)
                                .toInt()

                        timerView.text = "Time: $seconds"

                        if (seconds <= 3) {
                            timerView.setTextColor(Color.RED)
                        } else {
                            timerView.setTextColor(Color.BLACK)
                        }
                    }

                    override fun onFinish() {

                        timerView.text = "Time: 0"
                        timerView.setTextColor(Color.RED)

                        dialog.dismiss()

                        showStatus(
                            "Player $currentPlayer ran out of time.",
                            Color.RED
                        )

                        moveToNextPlayer()
                    }
                }.start()
        }

        dialog.show()
    }

    private fun startFinalWholeWordChallenge() {

        timer?.cancel()
        wholeWordTimer?.cancel()

        finalChallengeActive = true

        finalChallengePlayers.clear()
        finalChallengePlayers.addAll(getActivePlayers())

        finalChallengeIndex = 0

        if (finalChallengePlayers.isEmpty()) {

            finishRound(
                "Nobody is left to make the final guess.\n\n" +
                        "The word was:\n$secretWord"
            )

            return
        }

        showStatus(
            "Total turns reached. Final whole-word challenge!",
            Color.rgb(0, 100, 200)
        )

        updateScreen()

        showNextFinalChallengePlayer()
    }

    private fun showNextFinalChallengePlayer() {

        if (
            finalChallengeIndex >=
            finalChallengePlayers.size
        ) {

            finishRound(
                "Nobody guessed the whole word.\n\n" +
                        "The word was:\n$secretWord"
            )

            return
        }

        val player =
            finalChallengePlayers[finalChallengeIndex]

        currentPlayer = player

        updateScreen()

        showFinalWholeWordDialog(player)
    }

    private fun moveToNextFinalChallengePlayer() {

        wholeWordTimer?.cancel()

        finalChallengeIndex++

        if (
            finalChallengeIndex >=
            finalChallengePlayers.size
        ) {

            finishRound(
                "Nobody guessed the whole word.\n\n" +
                        "The word was:\n$secretWord"
            )

            return
        }

        showNextFinalChallengePlayer()
    }

    private fun showFinalWholeWordDialog(
        player: Int
    ) {

        val input = EditText(this)
        input.hint = "Type the entire word"
        input.setSingleLine(true)
        input.textSize = 18f
        input.setPadding(10, 10, 10, 10)

        val dialogLayout = LinearLayout(this)
        dialogLayout.orientation = LinearLayout.VERTICAL
        dialogLayout.setPadding(30, 10, 30, 5)

        dialogLayout.addView(
            input,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        val finalTimerText = TextView(this)
        finalTimerText.text = "Time: 20"
        finalTimerText.textSize = 22f
        finalTimerText.gravity = Gravity.CENTER
        finalTimerText.setTypeface(null, Typeface.BOLD)
        finalTimerText.setPadding(0, 15, 0, 5)

        dialogLayout.addView(finalTimerText)

        val dialog = AlertDialog.Builder(this)
            .setTitle(
                "PLAYER $player — FINAL WHOLE-WORD GUESS"
            )
            .setMessage(
                "Type the entire word and press Guess."
            )
            .setView(dialogLayout)
            .setNegativeButton("Skip", null)
            .setPositiveButton("Guess", null)
            .setCancelable(false)
            .create()

        dialog.setOnShowListener {

            dialog.window?.apply {
                setGravity(Gravity.BOTTOM)

                attributes = attributes.apply {
                    y = 40
                }
            }

            val guessButton =
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)

            val skipButton =
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)

            guessButton.setOnClickListener {

                val guess =
                    input.text.toString()
                        .trim()
                        .uppercase(Locale.US)

                if (guess.isEmpty()) {

                    input.error =
                        "Please enter the entire word."

                    input.requestFocus()

                    return@setOnClickListener
                }

                wholeWordTimer?.cancel()
                dialog.dismiss()

                if (
                    guess ==
                    secretWord.trim().uppercase(Locale.US)
                ) {

                    finalChallengeActive = false

                    finishRound(
                        "Player $player guessed the whole word and wins!"
                    )

                } else {

                    showStatus(
                        "Player $player's whole-word guess was wrong.",
                        Color.RED
                    )

                    moveToNextFinalChallengePlayer()
                }
            }

            skipButton.setOnClickListener {

                wholeWordTimer?.cancel()
                dialog.dismiss()

                showStatus(
                    "Player $player skipped the final guess.",
                    Color.RED
                )

                moveToNextFinalChallengePlayer()
            }

            input.requestFocus()

            wholeWordTimer =
                object : CountDownTimer(
                    20000L,
                    250L
                ) {

                    override fun onTick(
                        millisUntilFinished: Long
                    ) {

                        val seconds =
                            ((millisUntilFinished + 999L) / 1000L)
                                .toInt()

                        finalTimerText.text =
                            "Time: $seconds"

                        if (seconds <= 3) {
                            finalTimerText.setTextColor(Color.RED)
                        } else {
                            finalTimerText.setTextColor(Color.BLACK)
                        }
                    }

                    override fun onFinish() {

                        finalTimerText.text = "Time: 0"
                        finalTimerText.setTextColor(Color.RED)

                        dialog.dismiss()

                        showStatus(
                            "Player $player ran out of time.",
                            Color.RED
                        )

                        moveToNextFinalChallengePlayer()
                    }
                }.start()
        }

        dialog.show()
    }

    private fun showStatus(
        message: String,
        color: Int
    ) {

        statusText.text = message
        statusText.setTextColor(color)
    }

    private fun finishRound(message: String) {

        timer?.cancel()
        wholeWordTimer?.cancel()

        normalTimerRunning = false
        finalChallengeActive = false

        wholeWordButton.isEnabled = false

        wordText.text = buildDisplayedWord()

        turnText.text = "ROUND OVER"

        timerText.text = ""

        statusText.text = message
        statusText.setTextColor(Color.rgb(0, 100, 200))
        statusText.setTypeface(null, Typeface.BOLD)
        statusText.textSize = 19f

        updateLetterBoard()
    }

    private fun showLeaveConfirmation() {

        AlertDialog.Builder(this)
            .setTitle("Leave Game?")
            .setMessage(
                "Are you sure you want to leave this game?"
            )
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Leave") { _, _ ->
                finish()
            }
            .show()
    }

    override fun onDestroy() {

        timer?.cancel()
        wholeWordTimer?.cancel()

        super.onDestroy()
    }
}

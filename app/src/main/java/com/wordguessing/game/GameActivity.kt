package com.wordguessing.game

import android.app.AlertDialog
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity() {

    private lateinit var wordText: TextView
    private lateinit var turnText: TextView
    private lateinit var timerText: TextView
    private lateinit var scoresText: TextView
    private lateinit var lettersLayout: LinearLayout
    private lateinit var wholeWordButton: Button
    private lateinit var nextWordButton: Button

    private val selectedLetters = mutableSetOf<Char>()
    private val missedTurns = mutableMapOf<String, Int>()
    private val eliminatedPlayers = mutableSetOf<String>()

    private val scores = mutableMapOf<String, Int>()

    private var playerNames = ArrayList<String>()
    private var currentPlayerIndex = 0

    private var selectedCategory = "Random"
    private var secondsPerTurn = 10

    private var currentWord = ""
    private var previousWord = ""

    private var timer: CountDownTimer? = null
    private var roundFinished = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_game)

        wordText = findViewById(R.id.wordText)
        turnText = findViewById(R.id.turnText)
        timerText = findViewById(R.id.timerText)
        scoresText = findViewById(R.id.scoresText)
        lettersLayout = findViewById(R.id.lettersLayout)
        wholeWordButton = findViewById(R.id.wholeWordButton)
        nextWordButton = findViewById(R.id.nextWordButton)

        playerNames =
            intent.getStringArrayListExtra("playerNames") ?: arrayListOf("Player 1", "Player 2")

        selectedCategory = intent.getStringExtra("category") ?: "Random"
        secondsPerTurn = intent.getIntExtra("secondsPerTurn", 10)

        for (player in playerNames) {
            scores[player] = 0
            missedTurns[player] = 0
        }

        nextWordButton.visibility = Button.GONE

        createLetterButtons()
        chooseNewWord()

        wholeWordButton.setOnClickListener {
            if (!roundFinished) {
                showWholeWordDialog()
            }
        }

        nextWordButton.setOnClickListener {
            startNextRound()
        }
    }

    private fun chooseNewWord() {
        val availableWords = if (selectedCategory == "Random") {
            WordBank.categories.values.flatten()
        } else {
            WordBank.categories[selectedCategory] ?: emptyList()
        }.filter { it != previousWord }

        if (availableWords.isNotEmpty()) {
            currentWord = availableWords.random().uppercase()
        }

        previousWord = currentWord
        selectedLetters.clear()
        roundFinished = false

        missedTurns.clear()
        eliminatedPlayers.clear()

        for (player in playerNames) {
            missedTurns[player] = 0
        }

        updateWordDisplay()
        updateScores()
        updateTurnDisplay()
        startTimer()

        wholeWordButton.visibility = Button.VISIBLE
        nextWordButton.visibility = Button.GONE
    }

    private fun createLetterButtons() {
        lettersLayout.removeAllViews()

        val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

        for (letter in alphabet) {
            val button = Button(this)

            button.text = letter.toString()
            button.textSize = 14f

            button.setOnClickListener {
                handleLetter(letter)
            }

            lettersLayout.addView(button)
        }
    }

    private fun handleLetter(letter: Char) {
        if (roundFinished) return

        if (selectedLetters.contains(letter)) {
            Toast.makeText(
                this,
                "Already guessed!",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        selectedLetters.add(letter)

        val occurrences = currentWord.count {
            it == letter
        }

        if (occurrences > 0) {

            val currentPlayer = playerNames[currentPlayerIndex]

            scores[currentPlayer] =
                (scores[currentPlayer] ?: 0) + occurrences

            updateWordDisplay()
            updateScores()

            if (isWordSolved()) {
                finishRound(currentPlayer)
            } else {
                Toast.makeText(
                    this,
                    "$currentPlayer got $occurrences point(s)",
                    Toast.LENGTH_SHORT
                ).show()

                startTimer()
            }

        } else {

            Toast.makeText(
                this,
                "Wrong letter!",
                Toast.LENGTH_SHORT
            ).show()

            moveToNextPlayer()
        }
    }

    private fun updateWordDisplay() {
        val display = StringBuilder()

        for (character in currentWord) {

            if (character == ' ') {
                display.append("   ")
            } else if (selectedLetters.contains(character)) {
                display.append(character)
                display.append(" ")
            } else {
                display.append("_ ")
            }
        }

        wordText.text = display.toString()
    }

    private fun updateTurnDisplay() {
        if (roundFinished) return

        turnText.text =
            "${playerNames[currentPlayerIndex]}'s turn"
    }

    private fun updateScores() {
        val display = StringBuilder()

        for (player in playerNames) {
            display.append(player)
            display.append(": ")
            display.append(scores[player] ?: 0)

            if (eliminatedPlayers.contains(player)) {
                display.append(" (Out)")
            }

            display.append("\n")
        }

        scoresText.text = display.toString()
    }

    private fun isWordSolved(): Boolean {

        for (character in currentWord) {

            if (character == ' ') {
                continue
            }

            if (!selectedLetters.contains(character)) {
                return false
            }
        }

        return true
    }

    private fun showWholeWordDialog() {

        val input = android.widget.EditText(this)

        input.hint = "Enter the whole word"

        val dialog = AlertDialog.Builder(this)
            .setTitle("Guess the whole word")
            .setView(input)
            .setPositiveButton("Guess") { _, _ ->

                val guess = input.text
                    .toString()
                    .trim()
                    .uppercase()

                val currentPlayer = playerNames[currentPlayerIndex]

                if (guess == currentWord) {

                    finishRound(currentPlayer)

                } else {

                    Toast.makeText(
                        this,
                        "Wrong whole-word guess!",
                        Toast.LENGTH_SHORT
                    ).show()

                    moveToNextPlayer()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun finishRound(winner: String) {

        roundFinished = true

        timer?.cancel()

        wordText.text = currentWord

        turnText.text = "🎉 $winner wins!"

        wholeWordButton.visibility = Button.GONE
        nextWordButton.visibility = Button.VISIBLE

        updateScores()

        AlertDialog.Builder(this)
            .setTitle("🎉 $winner wins!")
            .setMessage(
                "The word was:\n\n$currentWord\n\n" +
                        "$winner solved the word!"
            )
            .setPositiveButton("OK", null)
            .show()
    }

    private fun startNextRound() {

        currentPlayerIndex++

        if (currentPlayerIndex >= playerNames.size) {
            currentPlayerIndex = 0
        }

        chooseNewWord()
    }

    private fun moveToNextPlayer() {

        timer?.cancel()

        recordMissedTurn()

        if (roundFinished) return

        var attempts = 0

        do {
            currentPlayerIndex++

            if (currentPlayerIndex >= playerNames.size) {
                currentPlayerIndex = 0
            }

            attempts++

        } while (
            eliminatedPlayers.contains(playerNames[currentPlayerIndex]) &&
            attempts <= playerNames.size
        )

        updateTurnDisplay()
        updateScores()
        startTimer()
    }

    private fun recordMissedTurn() {

        val currentPlayer = playerNames[currentPlayerIndex]

        val misses = (missedTurns[currentPlayer] ?: 0) + 1

        missedTurns[currentPlayer] = misses

        if (playerNames.size >= 3 && misses >= 3) {

            eliminatedPlayers.add(currentPlayer)

            Toast.makeText(
                this,
                "$currentPlayer is eliminated!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun startTimer() {

        timer?.cancel()

        if (roundFinished) return

        timerText.text = secondsPerTurn.toString()

        timer = object : CountDownTimer(
            secondsPerTurn * 1000L,
            1000L
        ) {

            override fun onTick(millisUntilFinished: Long) {

                val seconds =
                    (millisUntilFinished / 1000L) + 1

                timerText.text = seconds.toString()
            }

            override fun onFinish() {

                timerText.text = "0"

                Toast.makeText(
                    this@GameActivity,
                    "Time's up!",
                    Toast.LENGTH_SHORT
                ).show()

                moveToNextPlayer()
            }

        }.start()
    }

    override fun onDestroy() {
        timer?.cancel()
        super.onDestroy()
    }
}

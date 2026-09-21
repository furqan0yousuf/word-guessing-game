package com.wordguessing.game

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class GameActivity : Activity() {

    private val testWord = "APPLE"
    private val selectedLetters = mutableSetOf<Char>()

    private lateinit var wordText: TextView
    private lateinit var turnText: TextView
    private lateinit var scoresText: TextView
    private lateinit var timerText: TextView
    private lateinit var lettersLayout: LinearLayout

    private var currentPlayer = 0
    private val scores = mutableListOf<Int>()
    private var playerNames = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val category = intent.getStringExtra("category") ?: "Random"
        val seconds = intent.getIntExtra("secondsPerTurn", 10)

        playerNames =
            intent.getStringArrayListExtra("playerNames")
                ?: arrayListOf("Player 1", "Player 2")

        if (playerNames.isEmpty()) {
            playerNames.add("Player 1")
            playerNames.add("Player 2")
        }

        for (i in playerNames.indices) {
            scores.add(0)
        }

        createGameScreen(category, seconds)
    }

    private fun createGameScreen(category: String, seconds: Int) {

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(24, 24, 24, 24)

        val title = TextView(this)
        title.text = "Word Guessing Game"
        title.textSize = 26f
        title.gravity = Gravity.CENTER
        title.setTextColor(Color.BLACK)
        layout.addView(title)

        val categoryText = TextView(this)
        categoryText.text = "Category: $category"
        categoryText.textSize = 20f
        layout.addView(categoryText)

        turnText = TextView(this)
        turnText.textSize = 20f
        turnText.setPadding(0, 15, 0, 10)
        layout.addView(turnText)

        scoresText = TextView(this)
        scoresText.textSize = 18f
        scoresText.setPadding(0, 10, 0, 15)
        layout.addView(scoresText)

        updateTurnAndScores()

        timerText = TextView(this)
        timerText.text = "Time: $seconds"
        timerText.textSize = 22f
        timerText.gravity = Gravity.CENTER
        layout.addView(timerText)

        wordText = TextView(this)
        wordText.textSize = 32f
        wordText.gravity = Gravity.CENTER
        wordText.setPadding(0, 30, 0, 30)
        layout.addView(wordText)

        updateWordDisplay()

        val lettersTitle = TextView(this)
        lettersTitle.text = "Choose a letter"
        lettersTitle.textSize = 20f
        layout.addView(lettersTitle)

        lettersLayout = LinearLayout(this)
        lettersLayout.orientation = LinearLayout.VERTICAL

        createLetterButtons()

        layout.addView(lettersLayout)

        val fullWordButton = Button(this)
        fullWordButton.text = "Guess Whole Word"
        fullWordButton.textSize = 18f
        layout.addView(fullWordButton)

        fullWordButton.setOnClickListener {

            Toast.makeText(
                this,
                "Whole word guess selected",
                Toast.LENGTH_SHORT
            ).show()
        }

        setContentView(layout)
    }

    private fun createLetterButtons() {

        lettersLayout.removeAllViews()

        val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

        for (rowStart in 0 until alphabet.length step 6) {

            val row = LinearLayout(this)
            row.orientation = LinearLayout.HORIZONTAL

            val rowEnd = minOf(rowStart + 6, alphabet.length)

            for (i in rowStart until rowEnd) {

                val letter = alphabet[i]

                val letterButton = Button(this)
                letterButton.text = letter.toString()
                letterButton.textSize = 16f

                if (selectedLetters.contains(letter)) {
                    letterButton.setTextColor(Color.RED)
                } else {
                    letterButton.setTextColor(Color.GREEN)
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

                    if (selectedLetters.contains(letter)) {

                        Toast.makeText(
                            this,
                            "Already guessed",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@setOnClickListener
                    }

                    selectedLetters.add(letter)

                    letterButton.setTextColor(Color.RED)

                    if (testWord.contains(letter)) {

                        var pointsEarned = 0

                        for (wordLetter in testWord) {
                            if (wordLetter == letter) {
                                pointsEarned++
                            }
                        }

                        scores[currentPlayer] += pointsEarned

                        Toast.makeText(
                            this,
                            "${playerNames[currentPlayer]} gets $pointsEarned point(s)!",
                            Toast.LENGTH_SHORT
                        ).show()

                        updateWordDisplay()
                        updateTurnAndScores()

                        if (isWordSolved()) {
                            showWinnerScreen()
                            return@setOnClickListener
                        }

                    } else {

                        Toast.makeText(
                            this,
                            "Wrong letter!",
                            Toast.LENGTH_SHORT
                        ).show()

                        currentPlayer++

                        if (currentPlayer >= playerNames.size) {
                            currentPlayer = 0
                        }

                        updateTurnAndScores()
                    }
                }
            }

            lettersLayout.addView(row)
        }
    }

    private fun updateWordDisplay() {

        val display = StringBuilder()

        for (letter in testWord) {

            if (selectedLetters.contains(letter)) {
                display.append(letter)
            } else {
                display.append("_")
            }

            display.append(" ")
        }

        wordText.text = display.toString().trim()
    }

    private fun updateTurnAndScores() {

        turnText.text =
            "Turn: ${playerNames[currentPlayer]}"

        val scoreDisplay = StringBuilder()

        for (i in playerNames.indices) {

            scoreDisplay.append(
                "${playerNames[i]}: ${scores[i]} points"
            )

            if (i < playerNames.size - 1) {
                scoreDisplay.append("\n")
            }
        }

        scoresText.text = scoreDisplay.toString()
    }

    private fun isWordSolved(): Boolean {

        for (letter in testWord) {

            if (!selectedLetters.contains(letter)) {
                return false
            }
        }

        return true
    }

    private fun showWinnerScreen() {

        wordText.text = testWord

        lettersLayout.removeAllViews()

        turnText.text =
            "🎉 ${playerNames[currentPlayer]} wins!"

        val winnerText = TextView(this)
        winnerText.text =
            "${playerNames[currentPlayer]} solved the word!\n\nThe word was: $testWord"
        winnerText.textSize = 22f
        winnerText.gravity = Gravity.CENTER
        winnerText.setPadding(0, 30, 0, 30)

        val parent = wordText.parent as LinearLayout

        parent.addView(winnerText)

        val nextWordButton = Button(this)
        nextWordButton.text = "Next Word"
        nextWordButton.textSize = 20f

        parent.addView(nextWordButton)

        nextWordButton.setOnClickListener {

            selectedLetters.clear()

            currentPlayer++

            if (currentPlayer >= playerNames.size) {
                currentPlayer = 0
            }

            parent.removeView(winnerText)
            parent.removeView(nextWordButton)

            createLetterButtons()
            updateWordDisplay()
            updateTurnAndScores()
        }
    }
}

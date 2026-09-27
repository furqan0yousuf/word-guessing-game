package com.wordguessing.game

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class SinglePlayerActivity : Activity() {

    private lateinit var categoryText: TextView
    private lateinit var difficultyText: TextView
    private lateinit var wordText: TextView
    private lateinit var statusText: TextView
    private lateinit var attemptsText: TextView
    private lateinit var lettersLayout: LinearLayout

    private var secretWord = ""
    private var actualCategory = ""

    private var selectedCategories = emptyList<String>()
    private var selectedDifficulties = emptyList<String>()

    private val guessedLetters = mutableSetOf<Char>()

    private var wrongAttempts = 0
    private var maxWrongAttempts = 10

    private var roundFinished = false
    private var finalGuessMode = false

    private var wholeWordTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        selectedCategories =
            intent.getStringArrayListExtra("selectedCategories")?.toList()
                ?: WordBank.categories.keys.toList()

        selectedDifficulties =
            intent.getStringArrayListExtra("selectedDifficulties")?.toList()
                ?: listOf(
                    "Easy",
                    "Intermediate"
                )

        maxWrongAttempts =
            intent.getIntExtra(
                "maxWrongAttempts",
                10
            )

        startNewRound()
    }

    private fun startNewRound() {

        wholeWordTimer?.cancel()

        val result = WordBank.getRandomWord(
            selectedCategories,
            selectedDifficulties
        )

        secretWord = result.first.uppercase()
        actualCategory = result.second

        guessedLetters.clear()
        wrongAttempts = 0
        roundFinished = false
        finalGuessMode = false

        createScreen()
    }

    private fun createScreen() {

        val scrollView = ScrollView(this)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(20, 20, 20, 30)

        val title = TextView(this)
        title.text = "Single Player"
        title.textSize = 28f
        title.gravity = Gravity.CENTER
        title.setTypeface(null, Typeface.BOLD)

        layout.addView(title)

        categoryText = TextView(this)
        categoryText.text = "Category: $actualCategory"
        categoryText.textSize = 21f
        categoryText.gravity = Gravity.CENTER
        categoryText.setTypeface(null, Typeface.BOLD)
        categoryText.setPadding(0, 20, 0, 10)

        layout.addView(categoryText)

        difficultyText = TextView(this)
        difficultyText.text =
            "Difficulty: ${selectedDifficulties.joinToString(" + ")}"
        difficultyText.textSize = 17f
        difficultyText.gravity = Gravity.CENTER

        layout.addView(difficultyText)

        attemptsText = TextView(this)
        attemptsText.text =
            "Wrong letters: $wrongAttempts / $maxWrongAttempts"
        attemptsText.textSize = 19f
        attemptsText.gravity = Gravity.CENTER
        attemptsText.setPadding(0, 18, 0, 10)

        layout.addView(attemptsText)

        wordText = TextView(this)
        wordText.textSize = 28f
        wordText.gravity = Gravity.CENTER
        wordText.setTypeface(null, Typeface.BOLD)
        wordText.setPadding(0, 20, 0, 20)

        layout.addView(wordText)

        statusText = TextView(this)
        statusText.text = "Guess a letter."
        statusText.textSize = 18f
        statusText.gravity = Gravity.CENTER
        statusText.setPadding(0, 5, 0, 15)

        layout.addView(statusText)

        val wholeWordButton = Button(this)
        wholeWordButton.text = "Guess Whole Word"
        wholeWordButton.textSize = 18f

        wholeWordButton.setOnClickListener {

            if (!roundFinished) {
                showWholeWordDialog()
            }
        }

        layout.addView(wholeWordButton)

        lettersLayout = LinearLayout(this)
        lettersLayout.orientation = LinearLayout.VERTICAL
        lettersLayout.setPadding(0, 15, 0, 15)

        layout.addView(lettersLayout)

        val newGameButton = Button(this)
        newGameButton.text = "New Word"
        newGameButton.textSize = 18f

        newGameButton.setOnClickListener {
            startNewRound()
        }

        layout.addView(newGameButton)

        val backButton = Button(this)
        backButton.text = "Back"
        backButton.textSize = 18f

        backButton.setOnClickListener {
            finish()
        }

        layout.addView(backButton)

        scrollView.addView(layout)

        setContentView(scrollView)

        updateWordDisplay()
        createLetterButtons()
    }

    private fun updateWordDisplay() {

        val builder = StringBuilder()

        for (char in secretWord) {

            if (char == ' ') {
                builder.append("   ")
            } else if (guessedLetters.contains(char)) {
                builder.append(char)
                builder.append(' ')
            } else {
                builder.append("_ ")
            }
        }

        wordText.text = builder.toString().trim()

        attemptsText.text =
            "Wrong letters: $wrongAttempts / $maxWrongAttempts"
    }

    private fun createLetterButtons() {

        lettersLayout.removeAllViews()

        val alphabet = ('A'..'Z').toList()

        var row: LinearLayout? = null

        for (index in alphabet.indices) {

            if (index % 6 == 0) {

                row = LinearLayout(this)
                row.orientation = LinearLayout.HORIZONTAL
                row.gravity = Gravity.CENTER

                lettersLayout.addView(row)
            }

            val letter = alphabet[index]

            val button = Button(this)
            button.text = letter.toString()
            button.textSize = 16f

            row?.addView(
                button,
                LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
            )

            button.setOnClickListener {

                if (roundFinished || finalGuessMode) {
                    return@setOnClickListener
                }

                if (guessedLetters.contains(letter)) {

                    Toast.makeText(
                        this,
                        "Already guessed.",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setOnClickListener
                }

                guessedLetters.add(letter)

                button.isEnabled = false
                button.setTextColor(Color.RED)

                if (secretWord.contains(letter)) {

                    statusText.text = "Correct!"

                    updateWordDisplay()

                    if (isWordComplete()) {
                        finishWin()
                    }

                } else {

                    wrongAttempts++

                    statusText.text = "Wrong letter."

                    updateWordDisplay()

                    if (wrongAttempts >= maxWrongAttempts) {

                        finalGuessMode = true

                        statusText.text =
                            "$maxWrongAttempts wrong letters reached. Guess the whole word!"

                        showWholeWordDialog()
                    }
                }
            }
        }
    }

    private fun isWordComplete(): Boolean {

        for (char in secretWord) {

            if (char != ' ' &&
                !guessedLetters.contains(char)
            ) {
                return false
            }
        }

        return true
    }

    private fun showWholeWordDialog() {

        if (roundFinished) {
            return
        }

        val input = android.widget.EditText(this)

        input.hint = "Enter the whole word"
        input.setSingleLine(true)
        input.textSize = 18f

        val dialog = AlertDialog.Builder(this)
            .setTitle("Guess Whole Word")
            .setMessage("You have 20 seconds.")
            .setView(input)
            .setNegativeButton(
                "Cancel",
                null
            )
            .setPositiveButton("Guess", null)
            .create()

        dialog.setOnShowListener {

            val guessButton =
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)

            guessButton.setOnClickListener {

                val guess =
                    input.text
                        .toString()
                        .trim()
                        .uppercase()

                if (guess.isEmpty()) {

                    input.error = "Enter a word."
                    return@setOnClickListener
                }

                wholeWordTimer?.cancel()

                dialog.dismiss()

                if (guess == secretWord) {

                    finishWin()

                } else {

                    if (finalGuessMode) {
                        finishLoss()
                    } else {

                        statusText.text =
                            "Incorrect whole-word guess."

                        finalGuessMode = false
                    }
                }
            }
        }

        dialog.setOnDismissListener {
            wholeWordTimer?.cancel()
        }

        dialog.show()

        wholeWordTimer =
            object : CountDownTimer(
                20_000,
                1_000
            ) {

                override fun onTick(
                    millisUntilFinished: Long
                ) {

                    dialog.setMessage(
                        "Time remaining: " +
                                "${(millisUntilFinished + 999) / 1000} seconds"
                    )
                }

                override fun onFinish() {

                    if (dialog.isShowing) {

                        dialog.dismiss()

                        if (finalGuessMode) {
                            finishLoss()
                        } else {

                            statusText.text =
                                "Whole-word guess timed out."

                            finalGuessMode = false
                        }
                    }
                }

            }.start()
    }

    private fun finishWin() {

        if (roundFinished) {
            return
        }

        roundFinished = true
        finalGuessMode = false

        wholeWordTimer?.cancel()

        wordText.text = secretWord

        AlertDialog.Builder(this)
            .setTitle("🏆 YOU WIN!")
            .setMessage(
                "You guessed $secretWord!"
            )
            .setPositiveButton(
                "Play Again"
            ) { _, _ ->
                startNewRound()
            }
            .setNegativeButton(
                "Exit"
            ) { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun finishLoss() {

        if (roundFinished) {
            return
        }

        roundFinished = true
        finalGuessMode = false

        wholeWordTimer?.cancel()

        wordText.text = secretWord

        AlertDialog.Builder(this)
            .setTitle("Game Over")
            .setMessage(
                "The word was:\n\n$secretWord"
            )
            .setPositiveButton(
                "Play Again"
            ) { _, _ ->
                startNewRound()
            }
            .setNegativeButton(
                "Exit"
            ) { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }
}

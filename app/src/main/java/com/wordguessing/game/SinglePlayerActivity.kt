
package com.wordguessing.game

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ReplacementSpan
import android.util.TypedValue
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
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
                ?: listOf("Easy", "Intermediate")

        maxWrongAttempts =
            intent.getIntExtra("maxWrongAttempts", 10)

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
        wordText.textSize = 30f
        wordText.gravity = Gravity.CENTER
        wordText.setTypeface(null, Typeface.BOLD)
        wordText.setPadding(4, 16, 4, 16)
        wordText.isSingleLine = false
        wordText.maxLines = 10
        wordText.includeFontPadding = true

        layout.addView(
            wordText,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

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
            wholeWordTimer?.cancel()
            finish()
        }
        layout.addView(backButton)

        scrollView.addView(layout)
        setContentView(scrollView)

        updateWordDisplay()
        createLetterButtons()
    }

    private fun updateWordDisplay() {
        val display = secretWord
            .split(' ')
            .joinToString("  ") { word ->
                word.map { letter ->
                    if (guessedLetters.contains(letter)) {
                        letter.toString()
                    } else {
                        "_"
                    }
                }.joinToString("")
            }

        setWordDisplayText(display)

        attemptsText.text =
            "Wrong letters: $wrongAttempts / $maxWrongAttempts"
    }

    private fun setWordDisplayText(displayText: String) {
        val formatted = SpannableStringBuilder()

        val words = displayText
            .trim()
            .split(Regex("\\s+"))
            .filter { it.isNotEmpty() }

        words.forEachIndexed { index, word ->
            if (index > 0) {
                formatted.append(" ")
            }

            val start = formatted.length
            formatted.append(word)
            val end = formatted.length

            val boxColor = if (index % 2 == 0) {
                Color.rgb(234, 241, 252) // Pale blue
            } else {
                Color.rgb(233, 245, 237) // Pale green
            }

            formatted.setSpan(
                WordBoxSpan(boxColor),
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        wordText.text = formatted

        wordText.post {
            if (wordText.text.toString() != formatted.toString()) {
                return@post
            }

            val availableWidth = (
                wordText.width -
                    wordText.paddingLeft -
                    wordText.paddingRight
                ).coerceAtLeast(1).toFloat()

            val density = resources.displayMetrics.density
            val paddingPerWord = 8f * density

            fun measureFullLine(): Float {
                return wordText.paint.measureText(displayText) +
                    words.size * paddingPerWord
            }

            var size = 30f

            wordText.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                size
            )

            if (measureFullLine() > availableWidth) {
                size *= 0.75f
                wordText.setTextSize(
                    TypedValue.COMPLEX_UNIT_SP,
                    size
                )
            }

            if (measureFullLine() > availableWidth) {
                size *= 0.75f
                wordText.setTextSize(
                    TypedValue.COMPLEX_UNIT_SP,
                    size
                )
            }

            while (
                size > 10f &&
                words.any {
                    wordText.paint.measureText(it) +
                        paddingPerWord > availableWidth
                }
            ) {
                size = (size * 0.75f).coerceAtLeast(10f)

                wordText.setTextSize(
                    TypedValue.COMPLEX_UNIT_SP,
                    size
                )
            }

            val fitsOneLine =
                measureFullLine() <= availableWidth

            wordText.isSingleLine = fitsOneLine
            wordText.maxLines =
                if (fitsOneLine) 1 else maxOf(3, words.size)
        }
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
            button.setTextColor(Color.WHITE)
            button.setBackgroundColor(Color.rgb(46, 125, 50))

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
                button.setTextColor(Color.WHITE)
                button.setBackgroundColor(Color.RED)

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
            if (char != ' ' && !guessedLetters.contains(char)) {
                return false
            }
        }

        return true
    }

    private fun showWholeWordDialog() {
        if (roundFinished) {
            return
        }

        val input = EditText(this)
        input.hint = "Enter the whole word"
        input.setSingleLine(true)
        input.textSize = 18f

        val dialog = AlertDialog.Builder(this)
            .setTitle("Guess Whole Word")
            .setMessage("You have 20 seconds.")
            .setView(input)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Guess", null)
            .create()

        dialog.setOnShowListener {
            val guessButton =
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)

            guessButton.setOnClickListener {
                val guess = input.text
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

        wholeWordTimer = object : CountDownTimer(20_000, 1_000) {
            override fun onTick(millisUntilFinished: Long) {
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

        setWordDisplayText(secretWord)

        AlertDialog.Builder(this)
            .setTitle("🏆 YOU WIN!")
            .setMessage("You guessed $secretWord!")
            .setPositiveButton("Play Again") { _, _ ->
                startNewRound()
            }
            .setNegativeButton("Exit") { _, _ ->
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

        setWordDisplayText(secretWord)

        AlertDialog.Builder(this)
            .setTitle("Game Over")
            .setMessage("The word was:\n\n$secretWord")
            .setPositiveButton("Play Again") { _, _ ->
                startNewRound()
            }
            .setNegativeButton("Exit") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private inner class WordBoxSpan(
        private val boxColor: Int
    ) : ReplacementSpan() {

        private val density =
            resources.displayMetrics.density

        private val horizontalPadding = 4f * density
        private val verticalPadding = 3f * density
        private val cornerRadius = 6f * density

        private val backgroundPaint =
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = boxColor
                style = Paint.Style.FILL
            }

        override fun getSize(
            paint: Paint,
            text: CharSequence,
            start: Int,
            end: Int,
            fm: Paint.FontMetricsInt?
        ): Int {
            val word = text.subSequence(start, end).toString()
            val textWidth = paint.measureText(word)

            if (fm != null) {
                val metrics = paint.fontMetricsInt
                fm.ascent = metrics.ascent
                fm.descent = metrics.descent
                fm.top = metrics.top
                fm.bottom = metrics.bottom
            }

            return (
                textWidth + horizontalPadding * 2
            ).toInt()
        }

        override fun draw(
            canvas: Canvas,
            text: CharSequence,
            start: Int,
            end: Int,
            x: Float,
            top: Int,
            y: Int,
            bottom: Int,
            paint: Paint
        ) {
            val word = text.subSequence(start, end).toString()
            val textWidth = paint.measureText(word)
            val metrics = paint.fontMetrics

            val box = RectF(
                x,
                y + metrics.ascent - verticalPadding,
                x + textWidth + horizontalPadding * 2,
                y + metrics.descent + verticalPadding
            )

            // Draw the rounded background first.
            canvas.drawRoundRect(
                box,
                cornerRadius,
                cornerRadius,
                backgroundPaint
            )

            // Draw the word once, centered within its box.
            val originalColor = paint.color
            val originalStyle = paint.style

            paint.color = Color.rgb(30, 41, 59)
            paint.style = Paint.Style.FILL

canvas.drawText(
    word,
    0,
    word.length,
    x + horizontalPadding,
    y,
    paint
)

            paint.color = originalColor
            paint.style = originalStyle
        }
    }
}


package com.wordguessing.game

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.GradientDrawable
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ReplacementSpan
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
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
        window.statusBarColor = backgroundColor
        window.navigationBarColor = backgroundColor
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR

        val scrollView = ScrollView(this).apply {
            setBackgroundColor(backgroundColor)
            isFillViewport = true
            clipToPadding = false
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(22), dp(22), dp(22), dp(30))
        }

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val backButton = TextView(this).apply {
            text = "‹"
            textSize = 30f
            gravity = Gravity.CENTER
            setTextColor(forestColor)
            background = roundedBackground(sageColor, dp(14))
            isClickable = true
            isFocusable = true
            setOnClickListener {
                wholeWordTimer?.cancel()
                finish()
            }
        }
        header.addView(backButton, LinearLayout.LayoutParams(dp(46), dp(46)))

        val brand = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), 0, 0, 0)
        }
        brand.addView(textView("WORDPLAY", 11f, forestColor, Typeface.BOLD).apply {
            letterSpacing = 0.16f
        })
        brand.addView(textView("SINGLE PLAYER", 9f, bodyColor, Typeface.NORMAL).apply {
            letterSpacing = 0.10f
            setPadding(0, dp(3), 0, 0)
        })
        header.addView(brand)
        layout.addView(header)

        val intro = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, dp(28), 0, dp(20))
        }
        intro.addView(textView("Find the hidden word.", 27f, forestColor, Typeface.BOLD))
        intro.addView(textView(
            "Pick your letters and see how many you can uncover.",
            14f,
            bodyColor,
            Typeface.NORMAL
        ).apply { setPadding(0, dp(7), 0, 0) })
        layout.addView(intro)

        val infoCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = cardBackground()
            setPadding(dp(15), dp(14), dp(15), dp(14))
        }
        val infoRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val categoryBlock = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        categoryBlock.addView(textView("CATEGORY", 10f, bodyColor, Typeface.BOLD).apply {
            letterSpacing = 0.10f
        })
        categoryText = textView(actualCategory, 16f, forestColor, Typeface.BOLD).apply {
            setPadding(0, dp(4), 0, 0)
        }
        categoryBlock.addView(categoryText)
        infoRow.addView(categoryBlock, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))

        val difficultyBlock = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.END
        }
        difficultyBlock.addView(textView("DIFFICULTY", 10f, bodyColor, Typeface.BOLD).apply {
            letterSpacing = 0.10f
            gravity = Gravity.END
        })
        difficultyText = textView(selectedDifficulties.joinToString(" + "), 13f, forestColor, Typeface.BOLD).apply {
            gravity = Gravity.END
            setPadding(0, dp(5), 0, 0)
        }
        difficultyBlock.addView(difficultyText)
        infoRow.addView(difficultyBlock, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ))
        infoCard.addView(infoRow)

        val infoDivider = View(this).apply { setBackgroundColor(borderColor) }
        infoCard.addView(infoDivider, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dp(1)
        ).apply { topMargin = dp(13); bottomMargin = dp(11) })

        attemptsText = textView("", 13f, bodyColor, Typeface.BOLD)
        infoCard.addView(attemptsText)
        layout.addView(infoCard)

        val wordCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            background = cardBackground()
            setPadding(dp(12), dp(22), dp(12), dp(22))
        }
        wordCard.addView(textView("YOUR WORD", 10f, bodyColor, Typeface.BOLD).apply {
            letterSpacing = 0.14f
            gravity = Gravity.CENTER
        })
        wordText = TextView(this).apply {
            textSize = 30f
            gravity = Gravity.CENTER
            setTypeface(null, Typeface.BOLD)
            setTextColor(forestColor)
            setPadding(dp(4), dp(15), dp(4), dp(5))
            isSingleLine = false
            maxLines = 10
            includeFontPadding = true
        }
        wordCard.addView(wordText, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ))
        layout.addView(wordCard, spacedParams(top = 16))

        statusText = textView("Guess a letter.", 14f, forestColor, Typeface.BOLD).apply {
            gravity = Gravity.CENTER
            background = roundedBackground(sageColor, dp(12))
            setPadding(dp(12), dp(12), dp(12), dp(12))
        }
        layout.addView(statusText, spacedParams(top = 12))

        val wholeWordButton = TextView(this).apply {
            text = "Guess the Whole Word"
            textSize = 14f
            typeface = Typeface.create("sans-serif", Typeface.BOLD)
            setTextColor(forestColor)
            gravity = Gravity.CENTER
            background = roundedBackground(Color.WHITE, dp(14)).also {
                it.setStroke(dp(1), borderColor)
            }
            isClickable = true
            isFocusable = true
            setPadding(dp(14), dp(14), dp(14), dp(14))
            setOnClickListener {
                if (!roundFinished) {
                    showWholeWordDialog()
                }
            }
        }
        layout.addView(wholeWordButton, spacedParams(top = 12))

        layout.addView(textView("TAP A LETTER", 10f, bodyColor, Typeface.BOLD).apply {
            letterSpacing = 0.13f
            setPadding(dp(2), dp(24), 0, dp(5))
        })

        lettersLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        layout.addView(lettersLayout)

        val newGameButton = TextView(this).apply {
            text = "New Word   ↻"
            textSize = 15f
            typeface = Typeface.create("sans-serif", Typeface.BOLD)
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            background = roundedBackground(forestColor, dp(15))
            isClickable = true
            isFocusable = true
            setPadding(dp(16), dp(16), dp(16), dp(16))
            setOnClickListener { startNewRound() }
        }
        layout.addView(newGameButton, spacedParams(top = 22))

        val footerBackButton = TextView(this).apply {
            text = "Back to setup"
            textSize = 14f
            typeface = Typeface.create("sans-serif", Typeface.BOLD)
            setTextColor(forestColor)
            gravity = Gravity.CENTER
            background = roundedBackground(Color.WHITE, dp(14)).also {
                it.setStroke(dp(1), borderColor)
            }
            isClickable = true
            isFocusable = true
            setPadding(dp(16), dp(14), dp(16), dp(14))
            setOnClickListener {
                wholeWordTimer?.cancel()
                finish()
            }
        }
        layout.addView(footerBackButton, spacedParams(top = 10))

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
                        "-"
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
                Color.rgb(226, 235, 224)
            } else {
                Color.rgb(239, 243, 236)
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
                row = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER
                }
                lettersLayout.addView(row, spacedParams(top = 5))
            }

            val letter = alphabet[index]
            val button = Button(this).apply {
                text = letter.toString()
                textSize = 14f
                typeface = Typeface.create("sans-serif", Typeface.BOLD)
                setTextColor(forestColor)
                background = roundedBackground(Color.WHITE, dp(11)).also {
                    it.setStroke(dp(1), borderColor)
                }
                isAllCaps = false
                minHeight = dp(46)
                minimumHeight = dp(46)
                setPadding(0, dp(4), 0, dp(4))
                stateListAnimator = null
            }

            row?.addView(
                button,
                LinearLayout.LayoutParams(
                    0,
                    dp(48),
                    1f
                ).apply {
                    marginStart = dp(3)
                    marginEnd = dp(3)
                    topMargin = dp(2)
                    bottomMargin = dp(2)
                }
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

                if (secretWord.contains(letter)) {
                    button.setTextColor(forestColor)
                    button.background = roundedBackground(sageColor, dp(11))
                    statusText.text = "Correct!"
                    updateWordDisplay()

                    if (isWordComplete()) {
                        finishWin()
                    }
                } else {
                    button.setTextColor(bodyColor)
                    button.background = roundedBackground(Color.rgb(239, 232, 226), dp(11))
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

    private fun textView(
        value: String,
        size: Float,
        color: Int,
        style: Int
    ): TextView = TextView(this).apply {
        text = value
        textSize = size
        setTextColor(color)
        typeface = Typeface.create("sans-serif", style)
        includeFontPadding = true
    }

    private fun roundedBackground(color: Int, radius: Int): GradientDrawable =
        GradientDrawable().apply {
            setColor(color)
            cornerRadius = radius.toFloat()
        }

    private fun cardBackground(): GradientDrawable =
        GradientDrawable().apply {
            setColor(Color.WHITE)
            cornerRadius = dp(18).toFloat()
            setStroke(dp(1), Color.rgb(229, 235, 227))
        }

    private fun spacedParams(top: Int = 0): LinearLayout.LayoutParams =
        LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin = dp(top)
        }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    private val backgroundColor = Color.rgb(247, 249, 245)
    private val forestColor = Color.rgb(53, 75, 63)
    private val bodyColor = Color.rgb(105, 119, 108)
    private val sageColor = Color.rgb(226, 235, 224)
    private val borderColor = Color.rgb(229, 235, 227)

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

            paint.color = Color.rgb(53, 75, 63)
            paint.style = Paint.Style.FILL

canvas.drawText(
    word.toString(),
    x + horizontalPadding,
    y.toFloat(),
    paint
)

            paint.color = originalColor
            paint.style = originalStyle
        }
    }
}

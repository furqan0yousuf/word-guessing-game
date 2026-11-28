package com.wordguessing.game

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.graphics.Color
import android.graphics.Typeface
import android.widget.ScrollView
import android.view.ViewGroup
import android.view.View
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.app.AlertDialog
import androidx.core.widget.TextViewCompat

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ReplacementSpan

class GameActivity : Activity() {

    private var testWord = ""

    private val selectedLetters =
        mutableSetOf<Char>()

    private val wrongLetters =
        mutableSetOf<Char>()

    private lateinit var wordText: TextView
    private lateinit var categoryText: TextView
    private lateinit var turnText: TextView
    private lateinit var scoresText: TextView
    private lateinit var timerText: TextView
    private lateinit var lettersLayout: LinearLayout
    private lateinit var fullWordButton: Button
    private lateinit var wholeWordTimerText: TextView

    private var currentPlayer = 0

    private val scores =
        mutableListOf<Int>()

    private val missedTurns =
        mutableListOf<Int>()

    private val eliminatedPlayers =
        mutableSetOf<Int>()

    private var playerNames =
        arrayListOf<String>()

    private var selectedCategories =
        emptyList<String>()

    private var selectedDifficulties =
        emptyList<String>()

    private var secondsPerTurn = 20

    private var timeLeft = 20

    private var roundFinished = false

    private var selectedCategory = "Random"

    private var actualCategory = ""

    private var previousWord = ""

    private var wordSelection = "Random Word"

    private var hostWord = ""

    private var nextWordChoice = "Winner"

    private var totalTurnsSetting = "Unlimited"

    private var missedTurnLimitSetting = "3"

    private var turnsUsed = 0

    private var wholeWordAttemptUsed = false

    private var wholeWordGuessInProgress = false

    private var wholeWordTimeLeft = 20

    private val handler =
        Handler(Looper.getMainLooper())

    // NORMAL TURN TIMER
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

    // WHOLE WORD TIMER
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

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )

        selectedCategory =
            intent.getStringExtra(
                "category"
            )
                ?: "Random"

        secondsPerTurn =
            intent.getIntExtra(
                "secondsPerTurn",
                20
            )

        timeLeft =
            secondsPerTurn

        playerNames =
            intent.getStringArrayListExtra(
                "playerNames"
            )
                ?: arrayListOf(
                    "Player 1",
                    "Player 2"
                )

        selectedCategories =
            intent.getStringArrayListExtra(
                "selectedCategories"
            )?.toList()
                ?: if (selectedCategory == "Random") {
                    WordBank.categories.keys.toList()
                } else {
                    listOf(selectedCategory)
                }

        selectedDifficulties =
            intent.getStringArrayListExtra(
                "selectedDifficulties"
            )?.toList()
                ?: listOf(
                    "Easy",
                    "Intermediate"
                )

        wordSelection =
            intent.getStringExtra(
                "wordSelection"
            )
                ?: "Random Word"

        hostWord =
            intent.getStringExtra(
                "hostWord"
            )
                ?: ""

        nextWordChoice =
            intent.getStringExtra(
                "nextWordChoice"
            )
                ?: "Winner"

        totalTurnsSetting =
            intent.getStringExtra(
                "totalTurns"
            )
                ?: "Unlimited"

        missedTurnLimitSetting =
            intent.getStringExtra(
                "missedTurnLimit"
            )
                ?: "3"

        if (
            playerNames.isEmpty()
        ) {

            playerNames.add(
                "Player 1"
            )

            playerNames.add(
                "Player 2"
            )
        }

        for (
            i in playerNames.indices
        ) {

            scores.add(0)

            missedTurns.add(0)
        }

        chooseNewWord()

        /*
         * If the host chose the word,
         * Player 1 is the Word Master.
         *
         * Start guessing with Player 2.
         */
        if (
            wordSelection ==
            "Host Chooses Word" &&
            playerNames.size >= 2
        ) {

            currentPlayer = 1
        }

        createGameScreen()

        startTimer()
    }

    private fun chooseNewWord() {

        if (
            wordSelection ==
            "Host Chooses Word"
        ) {

            testWord =
                hostWord

            actualCategory =
                selectedCategory

            selectedLetters.clear()

            wrongLetters.clear()

            return
        }

        val result =
            WordBank.getRandomWord(
                selectedCategories,
                selectedDifficulties
            )

        testWord =
            result.first

        actualCategory =
            result.second

        if (
            testWord == previousWord &&
            selectedCategories.size > 0
        ) {

            val secondResult =
                WordBank.getRandomWord(
                    selectedCategories,
                    selectedDifficulties
                )

            if (
                secondResult.first != previousWord
            ) {

                testWord =
                    secondResult.first

                actualCategory =
                    secondResult.second
            }
        }

        previousWord =
            testWord

        selectedLetters.clear()

        wrongLetters.clear()
    }

    private fun updateCategoryDisplay() {

        categoryText.text =
            if (
                selectedCategory ==
                "Random"
            ) {

                if (
                    wordSelection ==
                    "Random Word"
                ) {

                    "Category: Random: $actualCategory"

                } else {

                    "Category: Random"
                }

            } else {

                "Category: $actualCategory"
            }
    }

    private fun updateTimerDisplay() {

        timerText.text =
            "Time: $timeLeft"

        if (
            timeLeft <= 3
        ) {

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

    private fun createGameScreen() {
        val pageColor = Color.rgb(247, 249, 245)
        val forestColor = Color.rgb(53, 75, 63)
        val bodyColor = Color.rgb(105, 119, 108)
        val sageColor = Color.rgb(226, 235, 224)
        val borderColor = Color.rgb(229, 235, 227)

        window.statusBarColor = pageColor
        window.navigationBarColor = pageColor
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility =
            android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR

        val scrollView = android.widget.ScrollView(this).apply {
            setBackgroundColor(pageColor)
            isFillViewport = true
            clipToPadding = false
        }
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(20), dp(20), dp(28))
        }

        fun label(value: String, size: Float, color: Int, bold: Boolean = false): TextView =
            TextView(this).apply {
                text = value
                textSize = size
                setTextColor(color)
                typeface = Typeface.create("sans-serif", if (bold) Typeface.BOLD else Typeface.NORMAL)
                includeFontPadding = true
            }

        fun card(): LinearLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = GradientDrawable().apply {
                setColor(Color.WHITE)
                cornerRadius = dp(18).toFloat()
                setStroke(dp(1), borderColor)
            }
            setPadding(dp(15), dp(13), dp(15), dp(13))
        }

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(0, dp(3), 0, dp(18))
        }
        header.addView(label("WORDPLAY", 11f, forestColor, true).apply {
            letterSpacing = 0.16f
            gravity = Gravity.CENTER
        })
        header.addView(label("LOCAL MULTIPLAYER", 9f, bodyColor).apply {
            letterSpacing = 0.12f
            gravity = Gravity.CENTER
            setPadding(0, dp(3), 0, 0)
        })
        header.addView(label("Let the word battle begin.", 25f, forestColor, true).apply {
            gravity = Gravity.CENTER
            setPadding(0, dp(13), 0, 0)
        })
        layout.addView(header)

        val infoCard = card()
        val topRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        categoryText = label("", 13f, forestColor, true)
        categoryText.setPadding(0, dp(5), dp(5), dp(5))
        topRow.addView(categoryText, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        timerText = label("", 15f, forestColor, true).apply {
            gravity = Gravity.CENTER
            background = GradientDrawable().apply {
                setColor(sageColor)
                cornerRadius = dp(12).toFloat()
            }
            setPadding(dp(12), dp(8), dp(12), dp(8))
        }
        topRow.addView(timerText)
        infoCard.addView(topRow)
        updateCategoryDisplay()

        if (wordSelection == "Host Chooses Word") {
            infoCard.addView(label("Word Master: ${playerNames[0]}", 13f, bodyColor, true).apply {
                setPadding(0, dp(10), 0, 0)
            })
        }
        layout.addView(infoCard)

        turnText = label("", 19f, forestColor, true).apply {
            gravity = Gravity.CENTER
            setPadding(dp(8), dp(16), dp(8), dp(13))
        }
        layout.addView(turnText)

        val scoreCard = card()
        scoreCard.addView(label("SCOREBOARD", 10f, bodyColor, true).apply {
            letterSpacing = 0.12f
            setPadding(0, 0, 0, dp(8))
        })
        scoresText = label("", 13f, forestColor)
        scoresText.setLineSpacing(dp(3).toFloat(), 1f)
        scoreCard.addView(scoresText)
        layout.addView(scoreCard)

        val wordCard = card().apply {
            gravity = Gravity.CENTER
            setPadding(dp(10), dp(18), dp(10), dp(18))
        }
        wordText = label("", 30f, forestColor, true).apply {
            gravity = Gravity.CENTER
            setPadding(dp(4), dp(7), dp(4), dp(7))
            maxLines = 3
            isSingleLine = false
            ellipsize = null
        }
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
            wordText, 12, 30, 1, TypedValue.COMPLEX_UNIT_SP
        )
        wordCard.addView(wordText, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ))
        val wordHint = label("Find the hidden word one letter at a time.", 12f, bodyColor).apply {
            gravity = Gravity.CENTER
            setPadding(0, dp(5), 0, 0)
        }
        wordCard.addView(wordHint)
        layout.addView(wordCard, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { topMargin = dp(14) })

        val lettersHeading = label("Choose a letter", 17f, forestColor, true).apply {
            gravity = Gravity.CENTER
            setPadding(0, dp(20), 0, dp(10))
        }
        layout.addView(lettersHeading)
        lettersLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        layout.addView(lettersLayout)

        fullWordButton = Button(this).apply {
            text = "Guess Whole Word"
            textSize = 15f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            background = GradientDrawable().apply {
                setColor(forestColor)
                cornerRadius = dp(14).toFloat()
            }
            setPadding(dp(12), dp(12), dp(12), dp(12))
            isAllCaps = false
        }
        layout.addView(fullWordButton, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { topMargin = dp(14) })
        fullWordButton.setOnClickListener {
            if (!roundFinished) showWholeWordDialog()
        }

        scrollView.addView(layout)
        setContentView(scrollView)
        updateTurnAndScores()
        updateWordDisplay()
        createLetterButtons()
    }

    private fun createLetterButtons() {

        lettersLayout.removeAllViews()

        val alphabet =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

        for (
            rowStart in
            0 until alphabet.length step 6
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
                i in
                rowStart until rowEnd
            ) {

                val letter =
                    alphabet[i]

                val letterButton = Button(this).apply {
                    text = letter.toString()
                    textSize = 14f
                    typeface = Typeface.DEFAULT_BOLD
                    isAllCaps = false
                    setTextColor(Color.rgb(53, 75, 63))
                    background = GradientDrawable().apply {
                        setColor(if (selectedLetters.contains(letter)) {
                            Color.rgb(226, 235, 224)
                        } else {
                            Color.WHITE
                        })
                        cornerRadius = dp(10).toFloat()
                        setStroke(dp(1), Color.rgb(229, 235, 227))
                    }
                    minHeight = dp(42)
                    setPadding(0, dp(5), 0, dp(5))
                    if (selectedLetters.contains(letter)) isEnabled = false
                }

                row.addView(
                    letterButton,
                    LinearLayout.LayoutParams(
                        0,
                        dp(44),
                        1f
                    ).apply {
                        setMargins(dp(3), dp(3), dp(3), dp(3))
                    }
                )

                letterButton.setOnClickListener {

                    if (
                        roundFinished
                    ) {
                        return@setOnClickListener
                    }

                    if (
                        wholeWordGuessInProgress
                    ) {
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

                    letterButton.background = GradientDrawable().apply {
                        setColor(Color.rgb(226, 235, 224))
                        cornerRadius = dp(10).toFloat()
                        setStroke(dp(1), Color.rgb(190, 207, 190))
                    }

                    letterButton.setTextColor(
                        Color.WHITE
                    )

                    letterButton.isEnabled =
                        false

                    if (
                        testWord.contains(
                            letter
                        )
                    ) {

                        letterButton.text =
                            letter.toString()

                        var pointsEarned =
                            0

                        for (
                            wordLetter
                            in testWord
                        ) {

                            if (
                                wordLetter ==
                                letter
                            ) {

                                pointsEarned++
                            }
                        }

                        scores[
                            currentPlayer
                        ] +=
                            pointsEarned

                        updateWordDisplay()

                        updateTurnAndScores()

                        if (
                            isWordSolved()
                        ) {

                            finishRound(
                                playerNames[
                                    currentPlayer
                                ]
                            )

                            return@setOnClickListener
                        }

                        turnText.text =
                            "${playerNames[currentPlayer]} gets $pointsEarned point(s)!"

                        restartTimer()

                    } else {

                        wrongLetters.add(
                            letter
                        )

                        letterButton.text =
                            letter.toString()

                        Toast.makeText(
                            this,
                            "Wrong letter!",
                            Toast.LENGTH_SHORT
                        ).show()

                        moveToNextPlayer()
                    }
                }
            }

            lettersLayout.addView(
                row
            )
        }
    }

    private fun showWholeWordDialog() {

        if (
            roundFinished
        ) {
            return
        }

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

        handler.removeCallbacks(
            timerRunnable
        )

        wholeWordGuessInProgress =
            true

        wholeWordTimeLeft =
            20

        fullWordButton.isEnabled =
            false

        wholeWordTimerText =
            TextView(this)

        wholeWordTimerText.text =
            "Whole-word time: 20"

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

        val padding =
            40

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
                    "You have 20 seconds to guess the whole word:"
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

        dialog.setOnShowListener {

            handler.removeCallbacks(
                wholeWordTimerRunnable
            )

            handler.postDelayed(
                wholeWordTimerRunnable,
                1000
            )

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

                resumeNormalTimer()
            }

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

                if (
                    guess ==
                    testWord
                ) {

                    selectedLetters.addAll(
                        testWord.toSet()
                    )

                    updateWordDisplay()

                    finishRound(
                        playerNames[
                            currentPlayer
                        ]
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

            if (
                !roundFinished
            ) {

                resumeNormalTimer()
            }
        }

        dialog.show()
    }

    private fun resumeNormalTimer() {

        if (
            roundFinished
        ) {
            return
        }

        if (
            timeLeft <= 0
        ) {

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

    private fun isWordSolved(): Boolean {

        for (
            letter in testWord
        ) {

            if (
                letter == ' '
            ) {
                continue
            }

            if (
                !selectedLetters.contains(
                    letter
                )
            ) {

                return false
            }
        }

        return true
    }

    private fun handleTimeExpired() {

        if (
            roundFinished
        ) {
            return
        }

        timeLeft =
            0

        updateTimerDisplay()

        missedTurns[
            currentPlayer
        ]++

        val player =
            playerNames[
                currentPlayer
            ]

        val misses =
            missedTurns[
                currentPlayer
            ]

        val limit =
            getMissedTurnLimit()

        if (
            limit != null &&
            misses >= limit
        ) {

            eliminatedPlayers.add(
                currentPlayer
            )

            Toast.makeText(
                this,
                "$player is eliminated after $limit missed turns!",
                Toast.LENGTH_LONG
            ).show()

            if (
                getActivePlayerCount() <= 1
            ) {

                val winner =
                    getLastActivePlayer()

                if (
                    winner != -1
                ) {

                    finishRound(
                        playerNames[
                            winner
                        ]
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

    private fun getMissedTurnLimit(): Int? {

        return if (
            missedTurnLimitSetting ==
            "Unlimited"
        ) {
            null
        } else {
            missedTurnLimitSetting.toIntOrNull()
        }
    }

    private fun getTotalTurnLimit(): Int? {

        return if (
            totalTurnsSetting ==
            "Unlimited"
        ) {
            null
        } else {
            totalTurnsSetting.toIntOrNull()
        }
    }

    private fun moveToNextPlayer() {

        handler.removeCallbacks(
            timerRunnable
        )

        handler.removeCallbacks(
            wholeWordTimerRunnable
        )

        wholeWordGuessInProgress =
            false

        if (
            roundFinished
        ) {
            return
        }

        turnsUsed++

        val totalTurnLimit =
            getTotalTurnLimit()

        if (
            totalTurnLimit != null &&
            turnsUsed >= totalTurnLimit
        ) {

            finishRoundWithoutWinner()

            return
        }

        var attempts =
            0

        do {

            currentPlayer++

            if (
                currentPlayer >=
                playerNames.size
            ) {

                currentPlayer =
                    0
            }

            attempts++

        } while (
            (
                eliminatedPlayers.contains(
                    currentPlayer
                ) ||
                (
                    wordSelection ==
                    "Host Chooses Word" &&
                    currentPlayer == 0
                )
            ) &&
            attempts <=
            playerNames.size + 1
        )

        wholeWordAttemptUsed =
            false

        fullWordButton.isEnabled =
            true

        updateTurnAndScores()

        restartTimer()
    }

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

    private fun finishRound(
        winner: String
    ) {

        if (
            roundFinished
        ) {
            return
        }

        roundFinished =
            true

        handler.removeCallbacks(
            timerRunnable
        )

        handler.removeCallbacks(
            wholeWordTimerRunnable
        )

        wholeWordGuessInProgress =
            false

setWordDisplayText(testWord.uppercase())

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
            wordText.parent
                as LinearLayout

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

            if (
                !roundFinished
            ) {
                return@setOnClickListener
            }

            startNextRound(
                parent,
                winnerText,
                nextWordButton
            )
        }
    }

    private fun finishRoundWithoutWinner() {

        if (
            roundFinished
        ) {
            return
        }

        roundFinished =
            true

        handler.removeCallbacks(
            timerRunnable
        )

        handler.removeCallbacks(
            wholeWordTimerRunnable
        )

        wholeWordGuessInProgress =
            false

        setWordDisplayText(testWord.uppercase())

        lettersLayout.removeAllViews()

        fullWordButton.isEnabled =
            false

        turnText.text =
            "Round Over"

        timerText.text =
            "Turn limit reached"

        timerText.setTextColor(
            Color.BLACK
        )

        val parent =
            wordText.parent
                as LinearLayout

        val resultText =
            TextView(this)

        resultText.text =
            "No player solved the word.\n\nThe word was:\n$testWord"

        resultText.textSize =
            22f

        resultText.gravity =
            Gravity.CENTER

        resultText.setTypeface(
            null,
            Typeface.BOLD
        )

        resultText.setPadding(
            0,
            20,
            0,
            20
        )

        parent.addView(
            resultText
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
                resultText,
                nextWordButton
            )
        }
    }

    private fun startNextRound(
        parent: LinearLayout,
        resultText: TextView,
        nextWordButton: Button
    ) {

        parent.removeView(
            resultText
        )

        parent.removeView(
            nextWordButton
        )

        selectedLetters.clear()

        wrongLetters.clear()

        eliminatedPlayers.clear()

        for (
            i in missedTurns.indices
        ) {

            missedTurns[i] =
                0
        }

        turnsUsed =
            0

        chooseNewWord()

        currentPlayer++

        if (
            currentPlayer >=
            playerNames.size
        ) {

            currentPlayer =
                0
        }

        /*
         * If the host is the Word Master,
         * skip the host and start with Player 2.
         */
        if (
            wordSelection ==
            "Host Chooses Word" &&
            currentPlayer == 0 &&
            playerNames.size >= 2
        ) {

            currentPlayer =
                1
        }

        roundFinished =
            false

        wholeWordAttemptUsed =
            false

        wholeWordGuessInProgress =
            false

        fullWordButton.isEnabled =
            true

        updateCategoryDisplay()

        updateWordDisplay()

        updateTurnAndScores()

        createLetterButtons()

        startTimer()
    }

    private fun getActivePlayerCount(): Int {

        var count =
            0

        for (
            i in playerNames.indices
        ) {

            if (
                !eliminatedPlayers.contains(
                    i
                ) &&
                !(
                    wordSelection ==
                    "Host Chooses Word" &&
                    i == 0
                )
            ) {

                count++
            }
        }

        return count
    }

    private fun getLastActivePlayer(): Int {

        for (
            i in playerNames.indices
        ) {

            if (
                !eliminatedPlayers.contains(
                    i
                ) &&
                !(
                    wordSelection ==
                    "Host Chooses Word" &&
                    i == 0
                )
            ) {

                return i
            }
        }

        return -1
    }


private fun updateWordDisplay() {
    val display = testWord
        .uppercase()
        .split(' ')
        .joinToString(" ") { word ->
            word.map { letter ->
                if (selectedLetters.contains(letter)) {
                    letter.toString()
                } else {
                    "-"
                }
            }.joinToString("")
        }

    setWordDisplayText(display)
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
            Color.rgb(234, 241, 252)
        } else {
            Color.rgb(233, 245, 237)
        }

        formatted.setSpan(
            WordBoxSpan(boxColor),
            start,
            end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    wordText.text = formatted
}

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
            android.text.SpannableStringBuilder()

        for (
            i in playerNames.indices
        ) {

            val lineStart =
                scoreDisplay.length

            scoreDisplay.append(
                "${playerNames[i]}: ${scores[i]} points"
            )

            if (
                wordSelection ==
                "Host Chooses Word" &&
                i == 0
            ) {

                scoreDisplay.append(
                    " (Word Master)"
                )

            } else if (
                eliminatedPlayers.contains(
                    i
                )
            ) {

                scoreDisplay.append(
                    " (Eliminated)"
                )
            }

            scoreDisplay.append(
                " | Missed: ${missedTurns[i]}"
            )

            val lineEnd =
                scoreDisplay.length

            if (
                eliminatedPlayers.contains(
                    i
                )
            ) {

                scoreDisplay.setSpan(
                    android.text.style.ForegroundColorSpan(
                        Color.RED
                    ),
                    lineStart,
                    lineEnd,
                    android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                scoreDisplay.setSpan(
                    android.text.style.StyleSpan(
                        Typeface.BOLD
                    ),
                    lineStart,
                    lineEnd,
                    android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            if (
                i <
                playerNames.size - 1
            ) {

                scoreDisplay.append(
                    "\n"
                )
            }
        }

        scoresText.text =
            scoreDisplay
    }


private inner class WordBoxSpan(
    private val boxColor: Int
) : ReplacementSpan() {

    private val density = resources.displayMetrics.density
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

        if (fm != null) {
            val metrics = paint.fontMetricsInt
            fm.ascent = metrics.ascent
            fm.descent = metrics.descent
            fm.top = metrics.top
            fm.bottom = metrics.bottom
        }

        return (
            paint.measureText(word) + horizontalPadding * 2
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

        canvas.drawRoundRect(
            box,
            cornerRadius,
            cornerRadius,
            backgroundPaint
        )

        val originalColor = paint.color
        val originalStyle = paint.style

        paint.color = Color.rgb(30, 41, 59)
        paint.style = Paint.Style.FILL

        canvas.drawText(
            word,
            x + horizontalPadding,
            y.toFloat(),
            paint
        )

        paint.color = originalColor
        paint.style = originalStyle
    }
}
    
    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    override fun onBackPressed() {

        if (
            roundFinished
        ) {

            super.onBackPressed()

            return
        }

        AlertDialog.Builder(this)
            .setTitle(
                "Quit Game?"
            )
            .setMessage(
                "Are you sure you want to quit the game?"
            )
            .setNegativeButton(
                "Cancel",
                null
            )
            .setPositiveButton(
                "Quit"
            ) { _, _ ->
                finish()
            }
            .show()
    }

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

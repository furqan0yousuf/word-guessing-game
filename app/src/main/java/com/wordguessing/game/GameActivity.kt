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

    private var secondsPerTurn = 10

    private var timeLeft = 10

    private var roundFinished = false

    private var selectedCategory = "Random"

    private var actualCategory = ""

    private var previousWord = ""

    private var wordSelection = "Random Word"

    private var hostWord = ""

    private var nextWordChoice = "Winner"

    private var wholeWordAttemptUsed = false

    private var wholeWordGuessInProgress = false

    private var wholeWordTimeLeft = 10

    private val handler =
        Handler(Looper.getMainLooper())

    // NORMAL TURN TIMER
    private val timerRunnable =
        object : Runnable {
            override fun run() {

                if (roundFinished) return

                if (wholeWordGuessInProgress) return

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

                if (
                    wholeWordTimeLeft <= 3
                ) {

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

                if (
                    wholeWordTimeLeft <= 0
                ) {

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
                10
            )

        playerNames =
            intent.getStringArrayListExtra(
                "playerNames"
            )
                ?: arrayListOf(
                    "Player 1",
                    "Player 2"
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

        // If the host chose the word,
        // Player 1 is the Word Master.
        //
        // Start guessing with Player 2.
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

        val availableWords =
            mutableListOf<String>()

        if (
            selectedCategory ==
            "Random"
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
            availableWords.size > 1
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

        testWord =
            availableWords[
                Random.nextInt(
                    availableWords.size
                )
            ]

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

        // WORD MASTER DISPLAY
        if (
            wordSelection ==
            "Host Chooses Word"
        ) {

            val wordMasterText =
                TextView(this)

            wordMasterText.text =
                "Word Master: ${playerNames[0]}"

            wordMasterText.textSize =
                18f

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
                8,
                0,
                5
            )

            layout.addView(
                wordMasterText
            )
        }

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

        layout.addView(turnText)

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

        layout.addView(scoresText)

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

            if (
                !roundFinished
            ) {

                showWholeWordDialog()
            }
        }

        setContentView(layout)

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

                val letterButton =
                    Button(this)

                /*
                 * Normal unselected state:
                 * green button.
                 */
                if (
                    !selectedLetters.contains(
                        letter
                    )
                ) {

                    letterButton.text =
                        letter.toString()

                    letterButton.setTextColor(
                        Color.WHITE
                    )

                    letterButton.setBackgroundColor(
                        Color.rgb(
                            0,
                            100,
                            0
                        )
                    )

                } else {

                    /*
                     * Selected buttons are red.
                     *
                     * Wrong letters also show
                     * a clear ❌.
                     */
                    letterButton.setBackgroundColor(
                        Color.RED
                    )

                    letterButton.setTextColor(
                        Color.WHITE
                    )

                    letterButton.setTypeface(
                        null,
                        Typeface.BOLD
                    )

                    if (
                        wrongLetters.contains(
                            letter
                        )
                    ) {

                        letterButton.text =
                            "$letter ❌"

                    } else {

                        letterButton.text =
                            letter.toString()
                    }

                    letterButton.isEnabled =
                        false
                }

                letterButton.textSize =
                    15f

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

                row.addView(
                    letterButton,
                    LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                    )
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

                    /*
                     * Record the selected letter.
                     */
                    selectedLetters.add(
                        letter
                    )

                    /*
                     * Every selected letter
                     * turns red.
                     */
                    letterButton.setBackgroundColor(
                        Color.RED
                    )

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

                        /*
                         * Correct letter:
                         * red only.
                         */
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

                        Toast.makeText(
                            this,
                            "${playerNames[currentPlayer]} gets $pointsEarned point(s)!",
                            Toast.LENGTH_SHORT
                        ).show()

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

                        restartTimer()

                    } else {

                        /*
                         * Wrong letter:
                         * red + ❌.
                         */
                        wrongLetters.add(
                            letter
                        )

                        letterButton.text =
                            "$letter ❌"

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
            10

        fullWordButton.isEnabled =
            false

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
                    "You have 10 seconds to guess the whole word:"
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

        wrongLetters.clear()

        eliminatedPlayers.clear()

        for (
            i in missedTurns.indices
        ) {

            missedTurns[i] =
                0
        }

        chooseNewWord()

        currentPlayer++

        if (
            currentPlayer >=
            playerNames.size
        ) {

            currentPlayer =
                0
        }

        // If the host is the Word Master,
        // skip the host and start with Player 2.
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

        val display =
            StringBuilder()

        for (
            letter in testWord
        ) {

            if (
                letter == ' '
            ) {

                display.append(
                    "   "
                )

            } else if (
                selectedLetters.contains(
                    letter
                )
            ) {

                display.append(
                    letter
                )

                display.append(
                    " "
                )

            } else {

                display.append(
                    "_ "
                )
            }
        }

        wordText.text =
            display
                .toString()
                .trim()
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
            StringBuilder()

        for (
            i in playerNames.indices
        ) {

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
            scoreDisplay.toString()
    }

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

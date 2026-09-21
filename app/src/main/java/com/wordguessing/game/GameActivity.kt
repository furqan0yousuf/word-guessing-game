package com.wordguessing.game

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.*

class GameActivity : Activity() {

```
private val testWord = "APPLE"

private val selectedLetters = mutableSetOf<Char>()

private lateinit var wordText: TextView

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val category =
        intent.getStringExtra("category") ?: "Random"

    val seconds =
        intent.getIntExtra("secondsPerTurn", 10)

    val names =
        intent.getStringArrayListExtra("playerNames")
            ?: arrayListOf()

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
    categoryText.setPadding(0, 25, 0, 10)
    layout.addView(categoryText)

    val turnText = TextView(this)
    turnText.text =
        "Turn: ${names.firstOrNull() ?: "Player 1"}"
    turnText.textSize = 20f
    layout.addView(turnText)

    val timerText = TextView(this)
    timerText.text = "Time: $seconds"
    timerText.textSize = 22f
    timerText.gravity = Gravity.CENTER
    timerText.setPadding(0, 20, 0, 20)
    layout.addView(timerText)

    wordText = TextView(this)
    wordText.textSize = 32f
    wordText.gravity = Gravity.CENTER
    wordText.setPadding(0, 30, 0, 30)

    updateWordDisplay()

    layout.addView(wordText)

    val lettersTitle = TextView(this)
    lettersTitle.text = "Choose a letter"
    lettersTitle.textSize = 20f
    layout.addView(lettersTitle)

    val lettersLayout = LinearLayout(this)
    lettersLayout.orientation = LinearLayout.VERTICAL

    val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

    for (rowStart in 0 until alphabet.length step 6) {

        val row = LinearLayout(this)
        row.orientation = LinearLayout.HORIZONTAL

        val rowEnd =
            minOf(rowStart + 6, alphabet.length)

        for (i in rowStart until rowEnd) {

            val letter = alphabet[i]

            val letterButton = Button(this)
            letterButton.text = letter.toString()
            letterButton.textSize = 16f

            letterButton.setTextColor(Color.GREEN)

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
                    return@setOnClickListener
                }

                selectedLetters.add(letter)

                letterButton.setTextColor(Color.RED)

                updateWordDisplay()
            }
        }

        lettersLayout.addView(row)
    }

    layout.addView(lettersLayout)

    val fullWordButton = Button(this)
    fullWordButton.text = "Guess Whole Word"
    fullWordButton.textSize = 18f
    fullWordButton.setPadding(0, 20, 0, 20)

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
```

}

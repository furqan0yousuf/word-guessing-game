package com.wordguessing.game

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.*

class GameSetupActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val names =
            intent.getStringArrayListExtra("playerNames")
                ?: arrayListOf()

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(24, 24, 24, 24)

        val title = TextView(this)
        title.text = "Game Setup"
        title.textSize = 28f
        title.gravity = Gravity.CENTER
        title.setTextColor(Color.BLACK)

        layout.addView(title)

        // PLAYERS
        val playersTitle = TextView(this)
        playersTitle.text = "Players"
        playersTitle.textSize = 20f
        playersTitle.setPadding(0, 30, 0, 10)

        layout.addView(playersTitle)

        for (i in names.indices) {
            val player = TextView(this)
            player.text = "Player ${i + 1}: ${names[i]}"
            player.textSize = 18f

            layout.addView(player)
        }

        // WORD SELECTION
        val wordSelectionTitle = TextView(this)
        wordSelectionTitle.text = "Word Selection"
        wordSelectionTitle.textSize = 20f
        wordSelectionTitle.setPadding(0, 30, 0, 10)

        layout.addView(wordSelectionTitle)

        val wordSelectionSpinner = Spinner(this)

        val wordSelectionOptions = arrayOf(
            "Random Word",
            "Host Chooses Word"
        )

        val wordSelectionAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            wordSelectionOptions
        )

        wordSelectionSpinner.adapter = wordSelectionAdapter

        layout.addView(wordSelectionSpinner)

        // HOST WORD INPUT
        val hostWordInput = EditText(this)
        hostWordInput.hint = "Host: enter the word"
        hostWordInput.textSize = 20f
        hostWordInput.setSingleLine(true)
        hostWordInput.visibility = View.GONE

        layout.addView(hostWordInput)

        wordSelectionSpinner.onItemSelectedListener =
            object : android.widget.AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position == 1) {
                        hostWordInput.visibility = View.VISIBLE
                    } else {
                        hostWordInput.visibility = View.GONE
                        hostWordInput.text.clear()
                    }
                }

                override fun onNothingSelected(
                    parent: android.widget.AdapterView<*>?
                ) {
                }
            }

        // NEXT WORD CHOICE
        val nextWordTitle = TextView(this)
        nextWordTitle.text = "Next Word Chosen By"
        nextWordTitle.textSize = 20f
        nextWordTitle.setPadding(0, 30, 0, 10)

        layout.addView(nextWordTitle)

        val nextWordSpinner = Spinner(this)

        val nextWordOptions = arrayOf(
            "Winner",
            "Host"
        )

        val nextWordAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            nextWordOptions
        )

        nextWordSpinner.adapter = nextWordAdapter

        layout.addView(nextWordSpinner)

        // CATEGORY
        val categoryTitle = TextView(this)
        categoryTitle.text = "Category"
        categoryTitle.textSize = 20f
        categoryTitle.setPadding(0, 30, 0, 10)

        layout.addView(categoryTitle)

        val categorySpinner = Spinner(this)

        val categories = arrayOf(
            "Random",
            "Animals",
            "Food",
            "Places",
            "Sports",
            "Movies",
            "Things"
        )

        val categoryAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            categories
        )

        categorySpinner.adapter = categoryAdapter

        layout.addView(categorySpinner)

        // TIME
        val timeTitle = TextView(this)
        timeTitle.text = "Seconds per turn"
        timeTitle.textSize = 20f
        timeTitle.setPadding(0, 30, 0, 10)

        layout.addView(timeTitle)

        val timeSpinner = Spinner(this)

        val times = arrayOf(
            "10",
            "15",
            "20",
            "30"
        )

        val timeAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            times
        )

        timeSpinner.adapter = timeAdapter

        layout.addView(timeSpinner)

        // START GAME
        val startButton = Button(this)
        startButton.text = "Start Game"
        startButton.textSize = 18f

        layout.addView(startButton)

        startButton.setOnClickListener {

            val selectedWordSelection =
                wordSelectionSpinner.selectedItem.toString()

            if (
                selectedWordSelection == "Host Chooses Word" &&
                hostWordInput.text.toString().trim().isEmpty()
            ) {
                Toast.makeText(
                    this,
                    "Please enter a word.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val selectedNextWordChoice =
                nextWordSpinner.selectedItem.toString()

            val selectedCategory =
                categorySpinner.selectedItem.toString()

            val selectedTime =
                timeSpinner.selectedItem.toString().toInt()

            val hostWord =
                hostWordInput.text.toString()
                    .trim()
                    .uppercase()

            val intent =
                android.content.Intent(
                    this,
                    GameActivity::class.java
                )

            intent.putExtra(
                "wordSelection",
                selectedWordSelection
            )

            intent.putExtra(
                "hostWord",
                hostWord
            )

            intent.putExtra(
                "nextWordChoice",
                selectedNextWordChoice
            )

            intent.putExtra(
                "category",
                selectedCategory
            )

            intent.putExtra(
                "secondsPerTurn",
                selectedTime
            )

            intent.putStringArrayListExtra(
                "playerNames",
                names
            )

            startActivity(intent)
        }

        setContentView(layout)
    }
}

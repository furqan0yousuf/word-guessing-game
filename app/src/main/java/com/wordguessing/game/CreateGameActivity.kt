package com.wordguessing.game

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast

class CreateGameActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(24, 24, 24, 24)

        val title = TextView(this)
        title.text = "Create Game"
        title.textSize = 28f
        title.gravity = Gravity.CENTER
        title.setTextColor(Color.BLACK)

        layout.addView(title)

        // NUMBER OF PLAYERS
        val playersTitle = TextView(this)
        playersTitle.text = "Number of Players"
        playersTitle.textSize = 20f
        playersTitle.setPadding(0, 30, 0, 10)

        layout.addView(playersTitle)

        val playersSpinner = Spinner(this)

        val playerOptions = arrayOf(
            "2 Players",
            "3 Players",
            "4 Players"
        )

        playersSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            playerOptions
        )

        layout.addView(playersSpinner)

        // WORD SELECTION
        val wordTitle = TextView(this)
        wordTitle.text = "Word Selection"
        wordTitle.textSize = 20f
        wordTitle.setPadding(0, 30, 0, 10)

        layout.addView(wordTitle)

        val wordSpinner = Spinner(this)

        val wordOptions = arrayOf(
            "Random Word",
            "Manual Word"
        )

        wordSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            wordOptions
        )

        layout.addView(wordSpinner)

        // MANUAL WORD
        val manualWordInput = EditText(this)
        manualWordInput.hint = "Enter word"
        manualWordInput.textSize = 18f
        manualWordInput.setSingleLine(true)
        manualWordInput.visibility =
            android.view.View.GONE

        layout.addView(manualWordInput)

        wordSpinner.onItemSelectedListener =
            object :
                android.widget.AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>?,
                    view: android.view.View?,
                    position: Int,
                    id: Long
                ) {

                    if (position == 1) {

                        manualWordInput.visibility =
                            android.view.View.VISIBLE

                    } else {

                        manualWordInput.visibility =
                            android.view.View.GONE

                        manualWordInput.text.clear()
                    }
                }

                override fun onNothingSelected(
                    parent: android.widget.AdapterView<*>?
                ) {
                }
            }

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

        categorySpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            categories
        )

        layout.addView(categorySpinner)

        // SECONDS PER TURN
        val timeTitle = TextView(this)
        timeTitle.text = "Seconds per Turn"
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

        timeSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            times
        )

        layout.addView(timeSpinner)

        // NEXT WORD MASTER
        val nextMasterTitle = TextView(this)
        nextMasterTitle.text = "Next Word Master"
        nextMasterTitle.textSize = 20f
        nextMasterTitle.setPadding(0, 30, 0, 10)

        layout.addView(nextMasterTitle)

        val nextMasterSpinner = Spinner(this)

        val nextMasterOptions = arrayOf(
            "Same Word Master",
            "Winner becomes Word Master",
            "Host chooses Word Master",
            "Winner chooses Word Master"
        )

        nextMasterSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            nextMasterOptions
        )

        layout.addView(nextMasterSpinner)

        // PASSWORD
        val passwordTitle = TextView(this)
        passwordTitle.text = "Game Password (Optional)"
        passwordTitle.textSize = 20f
        passwordTitle.setPadding(0, 30, 0, 10)

        layout.addView(passwordTitle)

        val passwordInput = EditText(this)
        passwordInput.hint = "Enter password or leave blank"
        passwordInput.textSize = 18f
        passwordInput.setSingleLine(true)

        layout.addView(passwordInput)

        // CREATE BUTTON
        val createButton = Button(this)
        createButton.text = "Create Game"
        createButton.textSize = 18f

        layout.addView(
            createButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        createButton.setOnClickListener {

            val selectedWordSelection =
                wordSpinner.selectedItem.toString()

            val manualWord =
                manualWordInput.text
                    .toString()
                    .trim()
                    .uppercase()

            if (
                selectedWordSelection ==
                "Manual Word" &&
                manualWord.isEmpty()
            ) {

                Toast.makeText(
                    this,
                    "Please enter a word.",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val playerCount =
                playersSpinner.selectedItem
                    .toString()
                    .substringBefore(" ")
                    .toInt()

            val selectedCategory =
                categorySpinner.selectedItem.toString()

            val selectedTime =
                timeSpinner.selectedItem
                    .toString()
                    .toInt()

            val selectedNextMaster =
                nextMasterSpinner
                    .selectedItem
                    .toString()

            // Temporary game code.
            // Firebase will generate real shared codes later.
            val gameCode =
                (100000..999999)
                    .random()
                    .toString()

            val intent =
                Intent(
                    this,
                    GameWaitingActivity::class.java
                )

            intent.putExtra(
                "gameCode",
                gameCode
            )

            intent.putExtra(
                "playerCount",
                playerCount
            )

            intent.putExtra(
                "wordSelection",
                selectedWordSelection
            )

            intent.putExtra(
                "manualWord",
                manualWord
            )

            intent.putExtra(
                "category",
                selectedCategory
            )

            intent.putExtra(
                "secondsPerTurn",
                selectedTime
            )

            intent.putExtra(
                "nextWordMaster",
                selectedNextMaster
            )

            startActivity(intent)
        }

        setContentView(layout)
    }
}

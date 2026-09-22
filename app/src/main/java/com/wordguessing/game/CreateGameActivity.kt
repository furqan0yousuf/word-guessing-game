package com.wordguessing.game

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Button
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

        val nameTitle = TextView(this)
        nameTitle.text = "Your Name"
        nameTitle.textSize = 20f
        nameTitle.setPadding(0, 30, 0, 10)
        layout.addView(nameTitle)

        val nameInput = EditText(this)
        nameInput.hint = "Enter your name"
        nameInput.textSize = 18f
        nameInput.setSingleLine(true)
        layout.addView(nameInput)

        val playersTitle = TextView(this)
        playersTitle.text = "Maximum Players"
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

        /*
         * TURN TIME
         *
         * Minimum = 20 seconds
         * Default = 20 seconds
         * Unlimited = no countdown
         */
        val timeTitle = TextView(this)
        timeTitle.text = "Time per Turn"
        timeTitle.textSize = 20f
        timeTitle.setPadding(0, 30, 0, 10)
        layout.addView(timeTitle)

        val timeSpinner = Spinner(this)

        val times = arrayOf(
            "20 seconds",
            "30 seconds",
            "45 seconds",
            "60 seconds",
            "Unlimited"
        )

        timeSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            times
        )

        /*
         * Default = 20 seconds
         */
        timeSpinner.setSelection(0)

        layout.addView(timeSpinner)

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

        /*
         * Default = Winner becomes Word Master
         */
        nextMasterSpinner.setSelection(1)

        layout.addView(nextMasterSpinner)

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

        val createButton = Button(this)
        createButton.text = "Create Game & Join"
        createButton.textSize = 18f

        layout.addView(
            createButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        createButton.setOnClickListener {

            val hostName =
                nameInput.text
                    .toString()
                    .trim()

            if (hostName.isEmpty()) {

                Toast.makeText(
                    this,
                    "Please enter your name.",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

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

            /*
             * Convert the selected display text
             * into seconds.
             *
             * Unlimited = 0
             */
            val selectedTimeText =
                timeSpinner.selectedItem.toString()

            val selectedTime =
                when {

                    selectedTimeText ==
                        "Unlimited" -> 0

                    else ->
                        selectedTimeText
                            .substringBefore(" ")
                            .toInt()
                }

            val selectedNextMaster =
                nextMasterSpinner
                    .selectedItem
                    .toString()

            val password =
                passwordInput.text
                    .toString()
                    .trim()

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

            intent.putExtra(
                "password",
                password
            )

            intent.putExtra(
                "playerName",
                hostName
            )

            intent.putExtra(
                "isHost",
                true
            )

            startActivity(intent)
        }

        setContentView(layout)
    }
}

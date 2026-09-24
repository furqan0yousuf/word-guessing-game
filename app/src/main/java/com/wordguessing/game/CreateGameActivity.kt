package com.wordguessing.game

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast

class CreateGameActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*
         * Main screen
         */
        val mainLayout = LinearLayout(this)
        mainLayout.orientation = LinearLayout.VERTICAL

        /*
         * Scrollable settings area
         */
        val scrollView = ScrollView(this)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(24, 24, 24, 24)

        /*
         * TITLE
         */
        val title = TextView(this)
        title.text = "Create Game"
        title.textSize = 28f
        title.gravity = Gravity.CENTER
        title.setTextColor(Color.BLACK)

        layout.addView(title)

        /*
         * YOUR NAME
         */
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

        /*
         * MAXIMUM PLAYERS
         */
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

        /*
         * WORD SELECTION
         */
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
        manualWordInput.visibility = View.GONE

        layout.addView(manualWordInput)

        wordSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    if (position == 1) {
                        manualWordInput.visibility = View.VISIBLE
                    } else {
                        manualWordInput.visibility = View.GONE
                        manualWordInput.text.clear()
                    }
                }

                override fun onNothingSelected(
                    parent: AdapterView<*>?
                ) {
                }
            }

        /*
         * CATEGORY
         */
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
         * ADVANCED SETTINGS
         */

        val advancedButton = Button(this)
        advancedButton.text = "Advanced Settings ▼"
        advancedButton.textSize = 18f

        advancedButton.setPadding(
            10,
            12,
            10,
            12
        )

        layout.addView(advancedButton)

        val advancedLayout = LinearLayout(this)
        advancedLayout.orientation = LinearLayout.VERTICAL
        advancedLayout.visibility = View.GONE

        /*
         * TIME PER TURN
         */
        val timeTitle = TextView(this)
        timeTitle.text = "Time per Turn"
        timeTitle.textSize = 20f
        timeTitle.setPadding(0, 20, 0, 10)
        advancedLayout.addView(timeTitle)

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

        timeSpinner.setSelection(0)

        advancedLayout.addView(timeSpinner)

        /*
         * TOTAL TURNS
         */
        val totalTurnsTitle = TextView(this)
        totalTurnsTitle.text = "Total Turns"
        totalTurnsTitle.textSize = 20f
        totalTurnsTitle.setPadding(0, 20, 0, 10)
        advancedLayout.addView(totalTurnsTitle)

        val totalTurnsSpinner = Spinner(this)

        val totalTurnOptions = arrayOf(
            "Unlimited",
            "5 Turns",
            "10 Turns",
            "15 Turns",
            "20 Turns",
            "30 Turns"
        )

        totalTurnsSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            totalTurnOptions
        )

        totalTurnsSpinner.setSelection(0)

        advancedLayout.addView(totalTurnsSpinner)

        /*
         * NEXT WORD MASTER
         */
        val nextMasterTitle = TextView(this)
        nextMasterTitle.text = "Next Word Master"
        nextMasterTitle.textSize = 20f
        nextMasterTitle.setPadding(0, 20, 0, 10)
        advancedLayout.addView(nextMasterTitle)

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

        nextMasterSpinner.setSelection(1)

        advancedLayout.addView(nextMasterSpinner)

        /*
         * PASSWORD
         */
        val passwordTitle = TextView(this)
        passwordTitle.text = "Game Password (Optional)"
        passwordTitle.textSize = 20f
        passwordTitle.setPadding(0, 20, 0, 10)
        advancedLayout.addView(passwordTitle)

        val passwordInput = EditText(this)
        passwordInput.hint = "Enter password or leave blank"
        passwordInput.textSize = 18f
        passwordInput.setSingleLine(true)

        advancedLayout.addView(passwordInput)

        /*
         * USE DEFAULT SETTINGS BUTTON
         */
        val defaultButton = Button(this)
        defaultButton.text = "Use Default Settings"
        defaultButton.textSize = 17f

        defaultButton.setOnClickListener {

            timeSpinner.setSelection(0)
            totalTurnsSpinner.setSelection(0)
            nextMasterSpinner.setSelection(1)
            passwordInput.text.clear()

            Toast.makeText(
                this,
                "Default settings restored.",
                Toast.LENGTH_SHORT
            ).show()
        }

        advancedLayout.addView(defaultButton)

        /*
         * Add advanced settings container
         */
        layout.addView(advancedLayout)

        /*
         * Advanced Settings expand/collapse
         */
        advancedButton.setOnClickListener {

            if (advancedLayout.visibility == View.GONE) {

                advancedLayout.visibility = View.VISIBLE
                advancedButton.text = "Advanced Settings ▲"

            } else {

                advancedLayout.visibility = View.GONE
                advancedButton.text = "Advanced Settings ▼"
            }
        }

        /*
         * Put all settings inside the scroll area.
         */
        scrollView.addView(layout)

        /*
         * CREATE & JOIN BUTTON
         *
         * This stays outside the ScrollView.
         * Therefore it remains visible and easy to tap.
         */
        val createButton = Button(this)

        createButton.text = "CREATE GAME & JOIN"
        createButton.textSize = 19f

        createButton.setPadding(
            10,
            12,
            10,
            12
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

                /*
                 * Scroll to the top so the name field
                 * is visible.
                 */
                scrollView.post {
                    scrollView.fullScroll(
                        ScrollView.FOCUS_UP
                    )
                }

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
                categorySpinner.selectedItem
                    .toString()

            /*
             * TIME PER TURN
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

            /*
             * TOTAL TURNS
             *
             * Unlimited = 0
             */
            val selectedTotalTurnsText =
                totalTurnsSpinner
                    .selectedItem
                    .toString()

            val selectedTotalTurns =
                when {

                    selectedTotalTurnsText ==
                        "Unlimited" -> 0

                    else ->
                        selectedTotalTurnsText
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
                "totalTurns",
                selectedTotalTurns
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

        /*
         * Add button below the ScrollView,
         * keeping it permanently visible.
         */
        mainLayout.addView(
            scrollView,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        mainLayout.addView(
            createButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        setContentView(mainLayout)
    }
}

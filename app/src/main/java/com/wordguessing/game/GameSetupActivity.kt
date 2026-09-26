package com.wordguessing.game

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*

class GameSetupActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val names =
            intent.getStringArrayListExtra("playerNames")
                ?: arrayListOf()

        val scrollView = ScrollView(this)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(24, 24, 24, 30)

        // TITLE
        val title = TextView(this)
        title.text = "Game Setup"
        title.textSize = 28f
        title.gravity = Gravity.CENTER
        title.setTextColor(Color.BLACK)
        title.setTypeface(null, Typeface.BOLD)

        layout.addView(title)

        // PLAYERS
        val playersTitle = TextView(this)
        playersTitle.text = "Players"
        playersTitle.textSize = 21f
        playersTitle.setTypeface(null, Typeface.BOLD)
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
        wordSelectionTitle.textSize = 21f
        wordSelectionTitle.setTypeface(null, Typeface.BOLD)
        wordSelectionTitle.setPadding(0, 30, 0, 10)

        layout.addView(wordSelectionTitle)

        val wordSelectionSpinner = Spinner(this)

        val wordSelectionOptions = arrayOf(
            "Random Word",
            "Host Chooses Word"
        )

        wordSelectionSpinner.adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                wordSelectionOptions
            )

        layout.addView(wordSelectionSpinner)

        // HOST WORD
        val hostWordInput = EditText(this)
        hostWordInput.hint = "Host: enter the word"
        hostWordInput.textSize = 20f
        hostWordInput.setSingleLine(true)
        hostWordInput.visibility = View.GONE

        layout.addView(hostWordInput)

        wordSelectionSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
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
                    parent: AdapterView<*>?
                ) {
                }
            }

        // CATEGORIES
        val categoryTitle = TextView(this)
        categoryTitle.text = "Choose Categories"
        categoryTitle.textSize = 21f
        categoryTitle.setTypeface(null, Typeface.BOLD)
        categoryTitle.setPadding(0, 30, 0, 10)

        layout.addView(categoryTitle)

        val allCategoriesCheckBox = CheckBox(this)
        allCategoriesCheckBox.text = "All Categories"
        allCategoriesCheckBox.textSize = 18f
        allCategoriesCheckBox.isChecked = true

        layout.addView(allCategoriesCheckBox)

        val categoryNames = listOf(
            "Animals",
            "Food",
            "Places",
            "Sports",
            "Movies",
            "Things"
        )

        val categoryCheckBoxes = mutableListOf<CheckBox>()

        for (category in categoryNames) {

            val checkBox = CheckBox(this)
            checkBox.text = category
            checkBox.textSize = 18f
            checkBox.isChecked = false

            layout.addView(checkBox)
            categoryCheckBoxes.add(checkBox)
        }

        allCategoriesCheckBox.setOnCheckedChangeListener { _, checked ->

            if (checked) {

                for (checkBox in categoryCheckBoxes) {
                    checkBox.isChecked = false
                }
            }
        }

        for (checkBox in categoryCheckBoxes) {

            checkBox.setOnCheckedChangeListener { _, checked ->

                if (checked) {
                    allCategoriesCheckBox.isChecked = false
                }

                if (!categoryCheckBoxes.any { it.isChecked }) {
                    allCategoriesCheckBox.isChecked = true
                }
            }
        }

        // DIFFICULTY
        val difficultyTitle = TextView(this)
        difficultyTitle.text = "Choose Difficulty"
        difficultyTitle.textSize = 21f
        difficultyTitle.setTypeface(null, Typeface.BOLD)
        difficultyTitle.setPadding(0, 30, 0, 10)

        layout.addView(difficultyTitle)

        val difficultyNames = listOf(
            "Easy",
            "Intermediate",
            "Advanced",
            "Expert"
        )

        val difficultyCheckBoxes = mutableListOf<CheckBox>()

        for (difficulty in difficultyNames) {

            val checkBox = CheckBox(this)
            checkBox.text = difficulty
            checkBox.textSize = 18f

            checkBox.isChecked =
                difficulty == "Easy" ||
                difficulty == "Intermediate"

            layout.addView(checkBox)
            difficultyCheckBoxes.add(checkBox)
        }

        // NEXT WORD
        val nextWordTitle = TextView(this)
        nextWordTitle.text = "Next Word Chosen By"
        nextWordTitle.textSize = 21f
        nextWordTitle.setTypeface(null, Typeface.BOLD)
        nextWordTitle.setPadding(0, 30, 0, 10)

        layout.addView(nextWordTitle)

        val nextWordSpinner = Spinner(this)

        val nextWordOptions = arrayOf(
            "Winner",
            "Host"
        )

        nextWordSpinner.adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                nextWordOptions
            )

        layout.addView(nextWordSpinner)

        // ADVANCED SETTINGS
        val advancedButton = Button(this)
        advancedButton.text = "Advanced Settings ▼"
        advancedButton.textSize = 18f

        layout.addView(advancedButton)

        val advancedLayout = LinearLayout(this)
        advancedLayout.orientation = LinearLayout.VERTICAL
        advancedLayout.visibility = View.GONE

        layout.addView(advancedLayout)

        // TIME PER TURN
        val timeTitle = TextView(this)
        timeTitle.text = "Seconds per turn"
        timeTitle.textSize = 19f
        timeTitle.setPadding(0, 15, 0, 8)

        advancedLayout.addView(timeTitle)

        val timeSpinner = Spinner(this)

        val times = arrayOf(
            "10",
            "15",
            "20",
            "30",
            "45",
            "60"
        )

        timeSpinner.adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                times
            )

        // Default = 20 seconds
        timeSpinner.setSelection(2)

        advancedLayout.addView(timeSpinner)

        // TOTAL TURNS
        val totalTurnsTitle = TextView(this)
        totalTurnsTitle.text = "Total Turns"
        totalTurnsTitle.textSize = 19f
        totalTurnsTitle.setPadding(0, 20, 0, 8)

        advancedLayout.addView(totalTurnsTitle)

        val totalTurnsSpinner = Spinner(this)

        val totalTurnsOptions = arrayOf(
            "Unlimited",
            "5",
            "10",
            "15",
            "20",
            "30"
        )

        totalTurnsSpinner.adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                totalTurnsOptions
            )

        totalTurnsSpinner.setSelection(0)

        advancedLayout.addView(totalTurnsSpinner)

        // MISSED TURN LIMIT
        val missedTurnsTitle = TextView(this)
        missedTurnsTitle.text = "Missed Turn Limit"
        missedTurnsTitle.textSize = 19f
        missedTurnsTitle.setPadding(0, 20, 0, 8)

        advancedLayout.addView(missedTurnsTitle)

        val missedTurnsSpinner = Spinner(this)

        val missedTurnsOptions = arrayOf(
            "1",
            "2",
            "3",
            "5",
            "Unlimited"
        )

        missedTurnsSpinner.adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                missedTurnsOptions
            )

        // Default = 3
        missedTurnsSpinner.setSelection(2)

        advancedLayout.addView(missedTurnsSpinner)

        advancedButton.setOnClickListener {

            if (advancedLayout.visibility == View.GONE) {

                advancedLayout.visibility = View.VISIBLE
                advancedButton.text = "Advanced Settings ▲"

            } else {

                advancedLayout.visibility = View.GONE
                advancedButton.text = "Advanced Settings ▼"
            }
        }

        // DEFAULT SETTINGS BUTTON
        val defaultButton = Button(this)
        defaultButton.text = "Use Default Settings"
        defaultButton.textSize = 17f

        layout.addView(defaultButton)

        defaultButton.setOnClickListener {

            // All categories
            allCategoriesCheckBox.isChecked = true

            // Easy + Intermediate
            for (checkBox in difficultyCheckBoxes) {
                checkBox.isChecked =
                    checkBox.text.toString() == "Easy" ||
                    checkBox.text.toString() == "Intermediate"
            }

            // Random word
            wordSelectionSpinner.setSelection(0)

            // Winner chooses next word
            nextWordSpinner.setSelection(0)

            // 20 seconds
            timeSpinner.setSelection(2)

            // Unlimited total turns
            totalTurnsSpinner.setSelection(0)

            // 3 missed turns
            missedTurnsSpinner.setSelection(2)
        }

        // START GAME
        val startButton = Button(this)
        startButton.text = "Start Game"
        startButton.textSize = 19f

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

            val selectedCategories =
                if (allCategoriesCheckBox.isChecked) {
                    categoryNames
                } else {
                    categoryCheckBoxes
                        .filter { it.isChecked }
                        .map { it.text.toString() }
                }

            if (selectedCategories.isEmpty()) {

                Toast.makeText(
                    this,
                    "Please choose at least one category.",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val selectedDifficulties =
                difficultyCheckBoxes
                    .filter { it.isChecked }
                    .map { it.text.toString() }

            if (selectedDifficulties.isEmpty()) {

                Toast.makeText(
                    this,
                    "Please choose at least one difficulty.",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val selectedNextWordChoice =
                nextWordSpinner.selectedItem.toString()

            val selectedTime =
                timeSpinner.selectedItem.toString().toInt()

            val selectedTotalTurns =
                totalTurnsSpinner.selectedItem.toString()

            val selectedMissedTurns =
                missedTurnsSpinner.selectedItem.toString()

            val hostWord =
                hostWordInput.text.toString()
                    .trim()
                    .uppercase()

            val selectedCategory =
                if (selectedCategories.size == 1) {
                    selectedCategories[0]
                } else {
                    "Random"
                }

            val intent =
                Intent(
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

            intent.putExtra(
                "totalTurns",
                selectedTotalTurns
            )

            intent.putExtra(
                "missedTurnLimit",
                selectedMissedTurns
            )

            intent.putStringArrayListExtra(
                "selectedCategories",
                ArrayList(selectedCategories)
            )

            intent.putStringArrayListExtra(
                "selectedDifficulties",
                ArrayList(selectedDifficulties)
            )

            intent.putStringArrayListExtra(
                "playerNames",
                names
            )

            startActivity(intent)
        }

        // BACK
        val backButton = Button(this)
        backButton.text = "Back"
        backButton.textSize = 18f

        layout.addView(backButton)

        backButton.setOnClickListener {
            finish()
        }

        scrollView.addView(layout)

        setContentView(scrollView)
    }
}

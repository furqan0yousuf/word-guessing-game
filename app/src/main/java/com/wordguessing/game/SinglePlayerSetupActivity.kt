package com.wordguessing.game

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast

class SinglePlayerSetupActivity : Activity() {

    private val categoryNames = listOf(
        "Animals",
        "Food",
        "Places",
        "Sports",
        "Movies",
        "Things"
    )

    private val difficultyNames = listOf(
        "Easy",
        "Intermediate",
        "Advanced",
        "Expert"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scrollView = ScrollView(this)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(24, 24, 24, 30)

        val title = TextView(this)
        title.text = "Single Player Setup"
        title.textSize = 28f
        title.gravity = Gravity.CENTER
        title.setTextColor(Color.BLACK)
        layout.addView(title)

        // CATEGORIES

        val categoryTitle = TextView(this)
        categoryTitle.text = "Choose Categories"
        categoryTitle.textSize = 22f
        categoryTitle.setPadding(0, 30, 0, 10)
        categoryTitle.setTypeface(null, android.graphics.Typeface.BOLD)
        layout.addView(categoryTitle)

        val allCategoriesCheckBox = CheckBox(this)
        allCategoriesCheckBox.text = "All Categories"
        allCategoriesCheckBox.textSize = 18f
        allCategoriesCheckBox.isChecked = true
        layout.addView(allCategoriesCheckBox)

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
        difficultyTitle.textSize = 22f
        difficultyTitle.setPadding(0, 30, 0, 10)
        difficultyTitle.setTypeface(null, android.graphics.Typeface.BOLD)
        layout.addView(difficultyTitle)

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

        // WRONG LETTER LIMIT

        val wrongLettersTitle = TextView(this)
        wrongLettersTitle.text = "Wrong Letter Limit"
        wrongLettersTitle.textSize = 22f
        wrongLettersTitle.setPadding(0, 30, 0, 10)
        wrongLettersTitle.setTypeface(null, android.graphics.Typeface.BOLD)
        layout.addView(wrongLettersTitle)

        val wrongLettersDescription = TextView(this)
        wrongLettersDescription.text =
            "Choose how many wrong letters are allowed before you must guess the whole word."
        wrongLettersDescription.textSize = 16f
        layout.addView(wrongLettersDescription)

        val wrongLettersSpinner = Spinner(this)

        val wrongLetterOptions = arrayOf(
            "5",
            "10",
            "15",
            "20",
            "25"
        )

        val wrongLetterAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            wrongLetterOptions
        )

        wrongLettersSpinner.adapter = wrongLetterAdapter

        // Default = 10
        wrongLettersSpinner.setSelection(1)

        layout.addView(wrongLettersSpinner)

        // WHOLE WORD TIMER

        val wholeWordTitle = TextView(this)
        wholeWordTitle.text = "Whole-Word Guess Timer"
        wholeWordTitle.textSize = 22f
        wholeWordTitle.setPadding(0, 30, 0, 10)
        wholeWordTitle.setTypeface(null, android.graphics.Typeface.BOLD)
        layout.addView(wholeWordTitle)

        val wholeWordTimerText = TextView(this)
        wholeWordTimerText.text =
            "20 seconds"
        wholeWordTimerText.textSize = 18f
        layout.addView(wholeWordTimerText)

        // START GAME

        val startButton = Button(this)
        startButton.text = "Start Game"
        startButton.textSize = 18f
        startButton.setPadding(0, 20, 0, 20)

        layout.addView(
            startButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        // BACK

        val backButton = Button(this)
        backButton.text = "Back"
        backButton.textSize = 18f

        layout.addView(
            backButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        startButton.setOnClickListener {

            val selectedCategories =
                if (allCategoriesCheckBox.isChecked) {

                    categoryNames

                } else {

                    categoryCheckBoxes
                        .filter { it.isChecked }
                        .map { it.text.toString() }
                }

            val selectedDifficulties =
                difficultyCheckBoxes
                    .filter { it.isChecked }
                    .map { it.text.toString() }

            if (selectedCategories.isEmpty()) {

                Toast.makeText(
                    this,
                    "Please choose at least one category.",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (selectedDifficulties.isEmpty()) {

                Toast.makeText(
                    this,
                    "Please choose at least one difficulty.",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val maxWrongAttempts =
                wrongLettersSpinner
                    .selectedItem
                    .toString()
                    .toInt()

            val intent =
                Intent(
                    this,
                    SinglePlayerActivity::class.java
                )

            intent.putStringArrayListExtra(
                "selectedCategories",
                ArrayList(selectedCategories)
            )

            intent.putStringArrayListExtra(
                "selectedDifficulties",
                ArrayList(selectedDifficulties)
            )

            intent.putExtra(
                "maxWrongAttempts",
                maxWrongAttempts
            )

            startActivity(intent)
        }

        backButton.setOnClickListener {
            finish()
        }

        scrollView.addView(layout)

        setContentView(scrollView)
    }
}

package com.wordguessing.game

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast

class SinglePlayerSetupActivity : Activity() {

    private val backgroundColor = Color.rgb(247, 249, 245)
    private val forestColor = Color.rgb(53, 75, 63)
    private val bodyColor = Color.rgb(105, 119, 108)
    private val sageColor = Color.rgb(226, 235, 224)
    private val borderColor = Color.rgb(229, 235, 227)

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

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(22), dp(22), dp(22), dp(30))
        }

        // Brand header and back navigation
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val backIcon = textView("‹", 30f, forestColor, Typeface.NORMAL).apply {
            gravity = Gravity.CENTER
            background = roundedBackground(sageColor, dp(14))
            isClickable = true
            isFocusable = true
            setOnClickListener { finish() }
        }
        header.addView(backIcon, LinearLayout.LayoutParams(dp(46), dp(46)))

        val brand = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), 0, 0, 0)
        }
        brand.addView(textView("WORDPLAY", 11f, forestColor, Typeface.BOLD).apply {
            letterSpacing = 0.16f
        })
        brand.addView(textView("WORD GUESSING GAME", 9f, bodyColor, Typeface.NORMAL).apply {
            letterSpacing = 0.10f
            setPadding(0, dp(3), 0, 0)
        })
        header.addView(brand, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        content.addView(header)

        val intro = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, dp(34), 0, dp(24))
        }
        intro.addView(textView("Make it your game.", 29f, forestColor, Typeface.BOLD))
        intro.addView(textView(
            "Choose your categories and challenge level, then you're ready to play.",
            14f,
            bodyColor,
            Typeface.NORMAL
        ).apply { setPadding(0, dp(9), 0, 0) })
        content.addView(intro)

        content.addView(sectionHeading("01", "CHOOSE CATEGORIES"), spacedParams(top = 2))

        val categoryCard = sectionCard()
        val allCategoriesCheckBox = optionCheckBox("All Categories", 15f).apply {
            isChecked = true
        }
        categoryCard.addView(allCategoriesCheckBox, spacedParams())

        val categoryCheckBoxes = mutableListOf<CheckBox>()
        val categoryRows = listOf(categoryNames.take(2), categoryNames.drop(2).take(2), categoryNames.drop(4))
        for (rowItems in categoryRows) {
            val row = optionRow()
            for (category in rowItems) {
                val checkBox = optionCheckBox(category, 14f).apply {
                    isChecked = false
                }
                categoryCheckBoxes.add(checkBox)
                row.addView(checkBox, weightedOptionParams())
            }
            if (rowItems.size == 1) {
                row.addView(View(this), weightedOptionParams())
            }
            categoryCard.addView(row, spacedParams(top = 8))
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
        content.addView(categoryCard, spacedParams(top = 10))

        content.addView(sectionHeading("02", "CHOOSE DIFFICULTY"), spacedParams(top = 24))

        val difficultyCard = sectionCard()
        val difficultyCheckBoxes = mutableListOf<CheckBox>()
        for (rowItems in listOf(difficultyNames.take(2), difficultyNames.drop(2))) {
            val row = optionRow()
            for (difficulty in rowItems) {
                val checkBox = optionCheckBox(difficulty, 14f).apply {
                    isChecked = difficulty == "Easy" || difficulty == "Intermediate"
                }
                difficultyCheckBoxes.add(checkBox)
                row.addView(checkBox, weightedOptionParams())
            }
            difficultyCard.addView(row, spacedParams())
        }
        content.addView(difficultyCard, spacedParams(top = 10))

        content.addView(sectionHeading("03", "WRONG LETTER LIMIT"), spacedParams(top = 24))

        val wrongLettersCard = sectionCard()
        wrongLettersCard.addView(textView(
            "Choose how many incorrect letters are allowed before you must guess the whole word.",
            13f,
            bodyColor,
            Typeface.NORMAL
        ))
        val wrongLettersSpinner = Spinner(this).apply {
            background = roundedBackground(backgroundColor, dp(12)).also {
                it.setStroke(dp(1), borderColor)
            }
            setPadding(dp(12), dp(5), dp(12), dp(5))
        }
        val wrongLetterOptions = arrayOf("5", "10", "15", "20", "25")
        val wrongLetterAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            wrongLetterOptions
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        wrongLettersSpinner.adapter = wrongLetterAdapter
        // Keep the existing default of 10 wrong letters.
        wrongLettersSpinner.setSelection(1)
        wrongLettersCard.addView(wrongLettersSpinner, spacedParams(top = 12))
        content.addView(wrongLettersCard, spacedParams(top = 10))

        content.addView(sectionHeading("04", "WHOLE-WORD GUESS TIMER"), spacedParams(top = 24))

        val timerCard = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            background = cardBackground()
            setPadding(dp(16), dp(15), dp(16), dp(15))
        }
        val timerIcon = textView("20", 18f, forestColor, Typeface.BOLD).apply {
            gravity = Gravity.CENTER
            background = roundedBackground(sageColor, dp(12))
        }
        timerCard.addView(timerIcon, LinearLayout.LayoutParams(dp(48), dp(48)))
        val timerLabels = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(13), 0, 0, 0)
        }
        timerLabels.addView(textView("Time to guess the whole word", 14f, forestColor, Typeface.BOLD))
        timerLabels.addView(textView("20 seconds", 13f, bodyColor, Typeface.NORMAL).apply {
            setPadding(0, dp(4), 0, 0)
        })
        timerCard.addView(timerLabels, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        content.addView(timerCard, spacedParams(top = 10))

        val startButton = TextView(this).apply {
            text = "Start Game   →"
            textSize = 16f
            typeface = Typeface.create("sans-serif", Typeface.BOLD)
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            background = roundedBackground(forestColor, dp(16))
            isClickable = true
            isFocusable = true
            setPadding(dp(16), dp(17), dp(16), dp(17))
        }
        content.addView(startButton, spacedParams(top = 28))

        val backButton = TextView(this).apply {
            text = "Back"
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
            setOnClickListener { finish() }
        }
        content.addView(backButton, spacedParams(top = 10))

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

            val maxWrongAttempts = wrongLettersSpinner.selectedItem.toString().toInt()

            val intent = Intent(this, SinglePlayerActivity::class.java)
            intent.putStringArrayListExtra(
                "selectedCategories",
                ArrayList(selectedCategories)
            )
            intent.putStringArrayListExtra(
                "selectedDifficulties",
                ArrayList(selectedDifficulties)
            )
            intent.putExtra("maxWrongAttempts", maxWrongAttempts)
            startActivity(intent)
        }

        scrollView.addView(content)
        setContentView(scrollView)
    }

    private fun sectionHeading(number: String, label: String): LinearLayout {
        val heading = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        heading.addView(textView(number, 11f, forestColor, Typeface.BOLD).apply {
            gravity = Gravity.CENTER
            background = roundedBackground(sageColor, dp(8))
        }, LinearLayout.LayoutParams(dp(28), dp(25)))
        heading.addView(textView(label, 11f, bodyColor, Typeface.BOLD).apply {
            letterSpacing = 0.12f
            setPadding(dp(10), 0, 0, 0)
        })
        return heading
    }

    private fun sectionCard(): LinearLayout =
        LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = cardBackground()
            setPadding(dp(14), dp(14), dp(14), dp(14))
        }

    private fun optionRow(): LinearLayout =
        LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

    private fun optionCheckBox(label: String, size: Float): CheckBox =
        CheckBox(this).apply {
            text = label
            textSize = size
            typeface = Typeface.create("sans-serif", Typeface.NORMAL)
            setTextColor(forestColor)
            buttonTintList = ColorStateList.valueOf(forestColor)
            background = roundedBackground(backgroundColor, dp(11)).also {
                it.setStroke(dp(1), borderColor)
            }
            setPadding(dp(9), dp(9), dp(7), dp(9))
            minHeight = dp(48)
        }

    private fun weightedOptionParams(): LinearLayout.LayoutParams =
        LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply {
            marginEnd = dp(7)
        }

    private fun sectionLabel(value: String): TextView =
        textView(value, 11f, bodyColor, Typeface.BOLD).apply {
            letterSpacing = 0.13f
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
            setStroke(dp(1), borderColor)
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
}

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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast

class GameSetupActivity : Activity() {

    private val pageColor = Color.rgb(247, 249, 245)
    private val forestColor = Color.rgb(53, 75, 63)
    private val bodyColor = Color.rgb(105, 119, 108)
    private val sageColor = Color.rgb(226, 235, 224)
    private val borderColor = Color.rgb(229, 235, 227)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createScreen()
    }

    private fun createScreen() {
        val names = intent.getStringArrayListExtra("playerNames") ?: arrayListOf()

        window.statusBarColor = pageColor
        window.navigationBarColor = pageColor
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR

        val scrollView = ScrollView(this).apply {
            setBackgroundColor(pageColor)
            isFillViewport = true
            clipToPadding = false
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(22), dp(22), dp(22), dp(30))
        }

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val backArrow = textView("‹", 30f, forestColor, Typeface.NORMAL).apply {
            gravity = Gravity.CENTER
            background = roundedBackground(sageColor, dp(14))
            isClickable = true
            isFocusable = true
            setOnClickListener { finish() }
        }
        header.addView(backArrow, LinearLayout.LayoutParams(dp(46), dp(46)))
        val brand = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), 0, 0, 0)
        }
        brand.addView(textView("WORDPLAY", 11f, forestColor, Typeface.BOLD).apply {
            letterSpacing = 0.16f
        })
        brand.addView(textView("LOCAL MULTIPLAYER", 9f, bodyColor, Typeface.NORMAL).apply {
            letterSpacing = 0.10f
            setPadding(0, dp(3), 0, 0)
        })
        header.addView(brand)
        layout.addView(header)

        val intro = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, dp(32), 0, dp(22))
        }
        intro.addView(textView("Make it your game.", 28f, forestColor, Typeface.BOLD))
        intro.addView(textView(
            "Choose the rules, pick your words, and let the challenge begin.",
            14f, bodyColor, Typeface.NORMAL
        ).apply { setPadding(0, dp(8), 0, 0) })
        layout.addView(intro)

        layout.addView(sectionHeading("01", "PLAYERS"))
        val playersCard = sectionCard()
        if (names.isEmpty()) {
            playersCard.addView(textView("No player names were provided.", 14f, bodyColor, Typeface.NORMAL))
        } else {
            names.forEachIndexed { index, name ->
                val playerRow = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER_VERTICAL
                    setPadding(0, dp(5), 0, dp(5))
                }
                val number = textView((index + 1).toString(), 14f, forestColor, Typeface.BOLD).apply {
                    gravity = Gravity.CENTER
                    background = roundedBackground(sageColor, dp(10))
                }
                playerRow.addView(number, LinearLayout.LayoutParams(dp(34), dp(34)))
                playerRow.addView(textView(name, 15f, forestColor, Typeface.BOLD).apply {
                    setPadding(dp(11), 0, 0, 0)
                })
                playersCard.addView(playerRow)
            }
        }
        layout.addView(playersCard, spacedParams(top = 10))

        layout.addView(sectionHeading("02", "WORD SELECTION"), spacedParams(top = 22))
        val wordCard = sectionCard()
        wordCard.addView(fieldLabel("HOW SHOULD THE WORD BE CHOSEN?"))
        val wordSelectionSpinner = styledSpinner(arrayOf("Random Word", "Host Chooses Word"))
        wordCard.addView(wordSelectionSpinner, spacedParams(top = 7))

        val hostWordInput = EditText(this).apply {
            hint = "Host: enter the word"
            textSize = 16f
            setSingleLine(true)
            setTextColor(forestColor)
            setHintTextColor(bodyColor)
            background = outlinedBackground()
            setPadding(dp(13), dp(12), dp(13), dp(12))
            visibility = View.GONE
        }
        wordCard.addView(hostWordInput, spacedParams(top = 10))
        wordSelectionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 1) {
                    hostWordInput.visibility = View.VISIBLE
                } else {
                    hostWordInput.visibility = View.GONE
                    hostWordInput.text.clear()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        layout.addView(wordCard, spacedParams(top = 10))

        layout.addView(sectionHeading("03", "CATEGORIES"), spacedParams(top = 22))
        val categoryCard = sectionCard()
        categoryCard.addView(textView("Choose the topics that can appear in the game.", 13f, bodyColor, Typeface.NORMAL))
        val categoryNames = listOf("Animals", "Food", "Places", "Sports", "Movies", "Things")
        val allCategoriesCheckBox = styledCheckBox("All Categories", true)
        categoryCard.addView(allCategoriesCheckBox, spacedParams(top = 10))
        val categoryCheckBoxes = mutableListOf<CheckBox>()
        categoryNames.forEach { category ->
            val checkBox = styledCheckBox(category, false)
            categoryCard.addView(checkBox, spacedParams(top = 3))
            categoryCheckBoxes.add(checkBox)
        }
        allCategoriesCheckBox.setOnCheckedChangeListener { _, checked ->
            if (checked) categoryCheckBoxes.forEach { it.isChecked = false }
        }
        categoryCheckBoxes.forEach { checkBox ->
            checkBox.setOnCheckedChangeListener { _, checked ->
                if (checked) allCategoriesCheckBox.isChecked = false
                if (categoryCheckBoxes.none { it.isChecked }) allCategoriesCheckBox.isChecked = true
            }
        }
        layout.addView(categoryCard, spacedParams(top = 10))

        layout.addView(sectionHeading("04", "DIFFICULTY"), spacedParams(top = 22))
        val difficultyCard = sectionCard()
        difficultyCard.addView(textView("Select at least one difficulty level.", 13f, bodyColor, Typeface.NORMAL))
        val difficultyNames = listOf("Easy", "Intermediate", "Advanced", "Expert")
        val difficultyCheckBoxes = mutableListOf<CheckBox>()
        difficultyNames.forEach { difficulty ->
            val checkBox = styledCheckBox(
                difficulty,
                difficulty == "Easy" || difficulty == "Intermediate"
            )
            difficultyCard.addView(checkBox, spacedParams(top = 5))
            difficultyCheckBoxes.add(checkBox)
        }
        layout.addView(difficultyCard, spacedParams(top = 10))

        layout.addView(sectionHeading("05", "NEXT WORD"), spacedParams(top = 22))
        val nextWordCard = sectionCard()
        nextWordCard.addView(fieldLabel("WHO CHOOSES THE NEXT WORD?"))
        val nextWordSpinner = styledSpinner(arrayOf("Winner", "Host"))
        nextWordCard.addView(nextWordSpinner, spacedParams(top = 7))
        layout.addView(nextWordCard, spacedParams(top = 10))

        val advancedButton = textView("Advanced Settings   ▼", 14f, forestColor, Typeface.BOLD).apply {
            gravity = Gravity.CENTER
            background = roundedBackground(sageColor, dp(13))
            setPadding(dp(16), dp(15), dp(16), dp(15))
            isClickable = true
            isFocusable = true
        }
        layout.addView(advancedButton, spacedParams(top = 22))

        val advancedCard = sectionCard().apply { visibility = View.GONE }
        advancedCard.addView(fieldLabel("SECONDS PER TURN"), spacedParams(top = 2))
        val timeSpinner = styledSpinner(arrayOf("10", "15", "20", "30", "45", "60"))
        timeSpinner.setSelection(2)
        advancedCard.addView(timeSpinner, spacedParams(top = 7))

        advancedCard.addView(fieldLabel("TOTAL TURNS"), spacedParams(top = 18))
        val totalTurnsSpinner = styledSpinner(arrayOf("Unlimited", "5", "10", "15", "20", "30"))
        totalTurnsSpinner.setSelection(0)
        advancedCard.addView(totalTurnsSpinner, spacedParams(top = 7))

        advancedCard.addView(fieldLabel("MISSED TURN LIMIT"), spacedParams(top = 18))
        val missedTurnsSpinner = styledSpinner(arrayOf("1", "2", "3", "5", "Unlimited"))
        missedTurnsSpinner.setSelection(2)
        advancedCard.addView(missedTurnsSpinner, spacedParams(top = 7))
        layout.addView(advancedCard, spacedParams(top = 10))

        advancedButton.setOnClickListener {
            val show = advancedCard.visibility == View.GONE
            advancedCard.visibility = if (show) View.VISIBLE else View.GONE
            advancedButton.text = if (show) "Advanced Settings   ▲" else "Advanced Settings   ▼"
        }

        val defaultButton = textView("Restore Default Settings", 14f, forestColor, Typeface.BOLD).apply {
            gravity = Gravity.CENTER
            background = roundedBackground(Color.WHITE, dp(13)).also { it.setStroke(dp(1), borderColor) }
            setPadding(dp(16), dp(14), dp(16), dp(14))
            isClickable = true
            isFocusable = true
        }
        layout.addView(defaultButton, spacedParams(top = 18))
        defaultButton.setOnClickListener {
            allCategoriesCheckBox.isChecked = true
            difficultyCheckBoxes.forEach {
                it.isChecked = it.text.toString() == "Easy" || it.text.toString() == "Intermediate"
            }
            wordSelectionSpinner.setSelection(0)
            nextWordSpinner.setSelection(0)
            timeSpinner.setSelection(2)
            totalTurnsSpinner.setSelection(0)
            missedTurnsSpinner.setSelection(2)
        }

        val startButton = textView("Start Game   →", 16f, Color.WHITE, Typeface.BOLD).apply {
            gravity = Gravity.CENTER
            background = roundedBackground(forestColor, dp(14))
            setPadding(dp(16), dp(17), dp(16), dp(17))
            isClickable = true
            isFocusable = true
        }
        layout.addView(startButton, spacedParams(top = 12))
        startButton.setOnClickListener {
            val selectedWordSelection = wordSelectionSpinner.selectedItem.toString()
            if (selectedWordSelection == "Host Chooses Word" &&
                hostWordInput.text.toString().trim().isEmpty()
            ) {
                Toast.makeText(this, "Please enter a word.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedCategories = if (allCategoriesCheckBox.isChecked) {
                categoryNames
            } else {
                categoryCheckBoxes.filter { it.isChecked }.map { it.text.toString() }
            }
            if (selectedCategories.isEmpty()) {
                Toast.makeText(this, "Please choose at least one category.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedDifficulties = difficultyCheckBoxes
                .filter { it.isChecked }
                .map { it.text.toString() }
            if (selectedDifficulties.isEmpty()) {
                Toast.makeText(this, "Please choose at least one difficulty.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedNextWordChoice = nextWordSpinner.selectedItem.toString()
            val selectedTime = timeSpinner.selectedItem.toString().toInt()
            val selectedTotalTurns = totalTurnsSpinner.selectedItem.toString()
            val selectedMissedTurns = missedTurnsSpinner.selectedItem.toString()
            val hostWord = hostWordInput.text.toString().trim().uppercase()
            val selectedCategory = if (selectedCategories.size == 1) selectedCategories[0] else "Random"

            val gameIntent = Intent(this, GameActivity::class.java).apply {
                putExtra("wordSelection", selectedWordSelection)
                putExtra("hostWord", hostWord)
                putExtra("nextWordChoice", selectedNextWordChoice)
                putExtra("category", selectedCategory)
                putExtra("secondsPerTurn", selectedTime)
                putExtra("totalTurns", selectedTotalTurns)
                putExtra("missedTurnLimit", selectedMissedTurns)
                putStringArrayListExtra("selectedCategories", ArrayList(selectedCategories))
                putStringArrayListExtra("selectedDifficulties", ArrayList(selectedDifficulties))
                putStringArrayListExtra("playerNames", names)
            }
            startActivity(gameIntent)
        }

        scrollView.addView(layout)
        setContentView(scrollView)
    }

    private fun sectionHeading(number: String, label: String): LinearLayout {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        row.addView(textView(number, 11f, forestColor, Typeface.BOLD).apply {
            gravity = Gravity.CENTER
            background = roundedBackground(sageColor, dp(8))
        }, LinearLayout.LayoutParams(dp(28), dp(25)))
        row.addView(textView(label, 11f, bodyColor, Typeface.BOLD).apply {
            letterSpacing = 0.12f
            setPadding(dp(10), 0, 0, 0)
        })
        return row
    }

    private fun sectionCard(): LinearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        background = cardBackground()
        setPadding(dp(15), dp(15), dp(15), dp(15))
    }

    private fun fieldLabel(value: String): TextView = textView(value, 10f, bodyColor, Typeface.BOLD).apply {
        letterSpacing = 0.10f
    }

    private fun styledCheckBox(label: String, checked: Boolean): CheckBox =
        CheckBox(this).apply {
            text = label
            textSize = 14f
            setTextColor(forestColor)
            buttonTintList = ColorStateList.valueOf(forestColor)
            isChecked = checked
            minHeight = dp(38)
            setPadding(dp(3), 0, 0, 0)
        }

    private fun styledSpinner(options: Array<String>): Spinner {
        val spinner = Spinner(this).apply {
            background = roundedBackground(pageColor, dp(11)).also {
                it.setStroke(dp(1), borderColor)
            }
            setPadding(dp(10), dp(4), dp(10), dp(4))
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        return spinner
    }

    private fun textView(value: String, size: Float, color: Int, style: Int): TextView =
        TextView(this).apply {
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

    private fun outlinedBackground(): GradientDrawable =
        roundedBackground(pageColor, dp(11)).also { it.setStroke(dp(1), borderColor) }

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
        ).apply { topMargin = dp(top) }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()
}

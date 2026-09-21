package com.wordguessing.game

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.widget.*

class GameSetupActivity : Activity() {

    private lateinit var names: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        names =
            intent.getStringArrayListExtra("playerNames")
                ?: arrayListOf()

        // Make sure we always have usable player names.
        for (i in names.indices) {
            if (names[i].trim().isEmpty()) {
                names[i] = "Player ${i + 1}"
            }
        }

        if (names.isEmpty()) {
            names.add("Player 1")
            names.add("Player 2")
        }

        createSetupScreen()
    }

    private fun createSetupScreen() {

        val layout = LinearLayout(this)

        layout.orientation =
            LinearLayout.VERTICAL

        layout.setPadding(
            24,
            24,
            24,
            24
        )

        // =========================
        // TITLE
        // =========================

        val title = TextView(this)

        title.text =
            "Game Setup"

        title.textSize =
            28f

        title.gravity =
            Gravity.CENTER

        title.setTextColor(
            Color.BLACK
        )

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

        // =========================
        // PLAYER COUNT
        // =========================

        val playerCount = TextView(this)

        playerCount.text =
            "${names.size} Player${if (names.size == 1) "" else "s"}"

        playerCount.textSize =
            20f

        playerCount.gravity =
            Gravity.CENTER

        playerCount.setTypeface(
            null,
            Typeface.BOLD
        )

        playerCount.setTextColor(
            Color.DKGRAY
        )

        layout.addView(
            playerCount
        )

        // =========================
        // PLAYERS
        // =========================

        val playersTitle =
            TextView(this)

        playersTitle.text =
            "Players"

        playersTitle.textSize =
            20f

        playersTitle.setTypeface(
            null,
            Typeface.BOLD
        )

        playersTitle.setPadding(
            0,
            25,
            0,
            10
        )

        layout.addView(
            playersTitle
        )

        for (i in names.indices) {

            val player =
                TextView(this)

            player.text =
                "Player ${i + 1}: ${names[i]}"

            player.textSize =
                18f

            player.setPadding(
                10,
                8,
                10,
                8
            )

            layout.addView(
                player
            )
        }

        // =========================
        // CATEGORY
        // =========================

        val categoryTitle =
            TextView(this)

        categoryTitle.text =
            "Category"

        categoryTitle.textSize =
            20f

        categoryTitle.setTypeface(
            null,
            Typeface.BOLD
        )

        categoryTitle.setPadding(
            0,
            25,
            0,
            10
        )

        layout.addView(
            categoryTitle
        )

        val categorySpinner =
            Spinner(this)

        val categories =
            arrayOf(
                "Random",
                "Animals",
                "Food",
                "Places",
                "Sports",
                "Movies",
                "Things"
            )

        val adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                categories
            )

        categorySpinner.adapter =
            adapter

        // Random is the default.
        categorySpinner.setSelection(0)

        layout.addView(
            categorySpinner
        )

        // =========================
        // TIME
        // =========================

        val timeTitle =
            TextView(this)

        timeTitle.text =
            "Seconds per turn"

        timeTitle.textSize =
            20f

        timeTitle.setTypeface(
            null,
            Typeface.BOLD
        )

        timeTitle.setPadding(
            0,
            25,
            0,
            10
        )

        layout.addView(
            timeTitle
        )

        val timeSpinner =
            Spinner(this)

        val times =
            arrayOf(
                "10",
                "15",
                "20",
                "30"
            )

        val timeAdapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                times
            )

        timeSpinner.adapter =
            timeAdapter

        // 10 seconds is the default.
        timeSpinner.setSelection(0)

        layout.addView(
            timeSpinner
        )

        // =========================
        // START BUTTON
        // =========================

        val startButton =
            Button(this)

        startButton.text =
            "Start Game"

        startButton.textSize =
            18f

        startButton.setTypeface(
            null,
            Typeface.BOLD
        )

        startButton.setPadding(
            0,
            10,
            0,
            10
        )

        val startParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        startParams.setMargins(
            0,
            30,
            0,
            10
        )

        layout.addView(
            startButton,
            startParams
        )

        startButton.setOnClickListener {

            val selectedCategory =
                categorySpinner.selectedItem.toString()

            val selectedTime =
                timeSpinner.selectedItem
                    .toString()
                    .toInt()

            val cleanNames =
                ArrayList<String>()

            for (i in names.indices) {

                val cleanName =
                    names[i].trim()

                if (cleanName.isEmpty()) {

                    cleanNames.add(
                        "Player ${i + 1}"
                    )

                } else {

                    cleanNames.add(
                        cleanName
                    )
                }
            }

            val gameIntent =
                android.content.Intent(
                    this,
                    GameActivity::class.java
                )

            gameIntent.putExtra(
                "category",
                selectedCategory
            )

            gameIntent.putExtra(
                "secondsPerTurn",
                selectedTime
            )

            gameIntent.putStringArrayListExtra(
                "playerNames",
                cleanNames
            )

            startActivity(
                gameIntent
            )
        }

        setContentView(
            layout
        )
    }

    // =========================
    // BACK BUTTON
    // =========================

    override fun onBackPressed() {

        AlertDialog.Builder(this)
            .setTitle("Leave Setup?")
            .setMessage(
                "Are you sure you want to leave the game setup?"
            )
            .setNegativeButton(
                "Cancel",
                null
            )
            .setPositiveButton(
                "Leave"
            ) { _, _ ->

                finish()
            }
            .show()
    }
}

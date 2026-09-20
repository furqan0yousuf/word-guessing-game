package com.wordguessing.game

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.*

class GameSetupActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val names = intent.getStringArrayListExtra("playerNames") ?: arrayListOf()

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(24, 24, 24, 24)

        val title = TextView(this)
        title.text = "Game Setup"
        title.textSize = 28f
        title.gravity = Gravity.CENTER
        title.setTextColor(Color.BLACK)
        layout.addView(title)

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

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            categories
        )

        categorySpinner.adapter = adapter
        layout.addView(categorySpinner)

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

        val startButton = Button(this)
        startButton.text = "Start Game"
        startButton.textSize = 18f

        layout.addView(startButton)

        startButton.setOnClickListener {

            val selectedCategory =
                categorySpinner.selectedItem.toString()

            val selectedTime =
                timeSpinner.selectedItem.toString().toInt()

            Toast.makeText(
                this,
                "Starting game: $selectedCategory, $selectedTime seconds",
                Toast.LENGTH_LONG
            ).show()
        }

        setContentView(layout)
    }
}

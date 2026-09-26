package com.wordguessing.game

import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class GameWaitingActivity : Activity() {

    private var gameCode = "------"
    private var playerCount = 2
    private var wordSelection = "Random Word"
    private var manualWord = ""
    private var category = "Random"
    private var secondsPerTurn = 20
    private var totalTurns = 0
    private var nextWordMaster = "Winner becomes Word Master"
    private var playerName = "Player 1"
    private var password = ""
    private var isHost = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gameCode = intent.getStringExtra("gameCode") ?: "------"

        playerCount = intent.getIntExtra("playerCount", 2)

        wordSelection = intent.getStringExtra("wordSelection")
            ?: "Random Word"

        manualWord = intent.getStringExtra("manualWord") ?: ""

        category = intent.getStringExtra("category") ?: "Random"

        secondsPerTurn = intent.getIntExtra("secondsPerTurn", 20)

        totalTurns = intent.getIntExtra("totalTurns", 0)

        nextWordMaster = intent.getStringExtra("nextWordMaster")
            ?: "Winner becomes Word Master"

        playerName = intent.getStringExtra("playerName")
            ?: "Player 1"

        password = intent.getStringExtra("password") ?: ""

        isHost = intent.getBooleanExtra("isHost", true)

        FirebaseAuth.getInstance()
            .signInAnonymously()
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    Toast.makeText(
                        this,
                        "Firebase login works",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    Toast.makeText(
                        this,
                        "Firebase login failed",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        createScreen()
    }

    private fun createScreen() {

        val scrollView = ScrollView(this)

        val layout = LinearLayout(this)

        layout.orientation = LinearLayout.VERTICAL

        layout.setPadding(
            24,
            24,
            24,
            24
        )

        val title = TextView(this)

        title.text = "Game Waiting Room"

        title.textSize = 28f

        title.gravity = Gravity.CENTER

        title.setTypeface(
            null,
            Typeface.BOLD
        )

        layout.addView(title)

        val codeText = TextView(this)

        codeText.text = "Game Code\n$gameCode"

        codeText.textSize = 22f

        codeText.gravity = Gravity.CENTER

        codeText.setPadding(
            0,
            20,
            0,
            8
        )

        codeText.setTypeface(
            null,
            Typeface.BOLD
        )

        layout.addView(codeText)

        val copyButton = Button(this)

        copyButton.text = "Copy Game Code"

        copyButton.setOnClickListener {

            val clipboard =
                getSystemService(
                    CLIPBOARD_SERVICE
                ) as ClipboardManager

            val clip =
                ClipData.newPlainText(
                    "Game Code",
                    gameCode
                )

            clipboard.setPrimaryClip(clip)

            Toast.makeText(
                this,
                "Game code copied.",
                Toast.LENGTH_SHORT
            ).show()
        }

        layout.addView(copyButton)

        val joinedTitle = TextView(this)

        joinedTitle.text =
            "Players Joined: 1 / $playerCount"

        joinedTitle.textSize = 21f

        joinedTitle.setTypeface(
            null,
            Typeface.BOLD
        )

        joinedTitle.setPadding(
            0,
            18,
            0,
            8
        )

        layout.addView(joinedTitle)

        val playersText = TextView(this)

        playersText.text = buildPlayerList()

        playersText.textSize = 18f

        playersText.setPadding(
            0,
            5,
            0,
            8
        )

        layout.addView(playersText)

        val settingsTitle = TextView(this)

        settingsTitle.text = "Game Settings"

        settingsTitle.textSize = 21f

        settingsTitle.setTypeface(
            null,
            Typeface.BOLD
        )

        settingsTitle.setPadding(
            0,
            12,
            0,
            8
        )

        layout.addView(settingsTitle)

        val settingsText = TextView(this)

        settingsText.text =
            """
            Maximum Players: $playerCount
            Word Selection: $wordSelection
            Category: $category
            Time per Turn: ${formatSecondsPerTurn()}
            Total Turns: ${formatTotalTurns()}
            Next Word Master: $nextWordMaster
            """.trimIndent()

        settingsText.textSize = 17f

        layout.addView(settingsText)

        val ruleText = TextView(this)

        ruleText.text =
            if (wordSelection == "Random Word") {
                "\nWord Master: None\nEveryone plays."
            } else {
                "\nWord Master: Not assigned yet\nThe Word Master does not play that round."
            }

        ruleText.textSize = 17f

        ruleText.gravity = Gravity.CENTER

        ruleText.setTypeface(
            null,
            Typeface.BOLD
        )

        layout.addView(ruleText)

        val statusText = TextView(this)

        statusText.text =
            if (isHost) {
                "\nYou are the HOST.\nWaiting for players..."
            } else {
                "\nWaiting for the host to start the game..."
            }

        statusText.textSize = 18f

        statusText.gravity = Gravity.CENTER

        statusText.setPadding(
            0,
            5,
            0,
            5
        )

        layout.addView(statusText)

        if (isHost) {

            val startButton = Button(this)

            startButton.text = "START GAME"

            startButton.textSize = 20f

            startButton.setTypeface(
                null,
                Typeface.BOLD
            )

            startButton.setPadding(
                10,
                12,
                10,
                12
            )

            startButton.setOnClickListener {

                AlertDialog.Builder(this)
                    .setTitle("Start Game?")
                    .setMessage(
                        "Start the game with the current players?"
                    )
                    .setNegativeButton(
                        "Cancel",
                        null
                    )
                    .setPositiveButton(
                        "Start"
                    ) { _, _ ->
                        startGame()
                    }
                    .show()
            }

            layout.addView(startButton)
        }

        val leaveButton = Button(this)

        leaveButton.text = "Leave Game"

        leaveButton.textSize = 18f

        leaveButton.setPadding(
            10,
            10,
            10,
            10
        )

        leaveButton.setOnClickListener {
            finish()
        }

        layout.addView(leaveButton)

        scrollView.addView(layout)

        setContentView(scrollView)
    }

    private fun formatSecondsPerTurn(): String {

        return if (secondsPerTurn <= 0) {
            "Unlimited"
        } else {
            "$secondsPerTurn seconds"
        }
    }

    private fun formatTotalTurns(): String {

        return if (totalTurns <= 0) {
            "Unlimited"
        } else {
            "$totalTurns Turns"
        }
    }

    private fun buildPlayerList(): String {

        val builder = StringBuilder()

        builder.append(
            "1. $playerName — HOST"
        )

        return builder.toString()
    }

    private fun startGame() {

        val gameIntent =
            Intent(
                this,
                OnlineGameRoundActivity::class.java
            )

        gameIntent.putExtra(
            "gameCode",
            gameCode
        )

        gameIntent.putExtra(
            "playerCount",
            playerCount
        )

        gameIntent.putExtra(
            "maxPlayers",
            playerCount
        )

        gameIntent.putExtra(
            "wordSelection",
            wordSelection
        )

        gameIntent.putExtra(
            "manualWord",
            manualWord
        )

        gameIntent.putExtra(
            "category",
            category
        )

        gameIntent.putExtra(
            "secondsPerTurn",
            secondsPerTurn
        )

        gameIntent.putExtra(
            "totalTurns",
            totalTurns
        )

        gameIntent.putExtra(
            "nextWordMaster",
            nextWordMaster
        )

        gameIntent.putExtra(
            "playerName",
            playerName
        )

        gameIntent.putExtra(
            "password",
            password
        )

        gameIntent.putExtra(
            "isHost",
            isHost
        )

        startActivity(gameIntent)
    }
}

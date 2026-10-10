
package com.wordguessing.game

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class GameWaitingActivity : Activity() {

    private val database = FirebaseDatabase.getInstance()

    private var gameCode = "------"

    // Maximum active players.
    private var playerCount = 2

    // Maximum total people in the room.
    private var roomCapacity = 2

    // Whole-word guesses for waiting players.
    private var waitingPlayerGuesses = 2

    private var wordSelection = "Random Word"
    private var manualWord = ""
    private var category = "Random"
    private var secondsPerTurn = 20
    private var totalTurns = 0

    private var nextWordMaster =
        "Winner becomes Word Master"

    private var playerName = "Player 1"
    private var password = ""
    private var isHost = true

    private lateinit var playersText: TextView
    private lateinit var joinedTitle: TextView
    private lateinit var activeTitle: TextView
    private lateinit var waitingTitle: TextView
    private lateinit var statusText: TextView
    private lateinit var startButton: Button

    private var gameListener: ValueEventListener? = null

    // Prevent duplicate navigation to the game screen.
    private var openingOnlineGame = false

    // Prevent repeated start requests while Firebase is checked.
    private var checkingStart = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        readIntentData()
        buildScreen()
        listenToGame()
    }

    private fun readIntentData() {
        gameCode = intent.getStringExtra("gameCode") ?: "------"

        playerCount = intent.getIntExtra("playerCount", 2)

        roomCapacity = intent.getIntExtra(
            "roomCapacity",
            playerCount
        )

        waitingPlayerGuesses = intent.getIntExtra(
            "waitingPlayerGuesses",
            2
        )

        wordSelection =
            intent.getStringExtra("wordSelection") ?: "Random Word"

        manualWord =
            intent.getStringExtra("manualWord") ?: ""

        category =
            intent.getStringExtra("category") ?: "Random"

        secondsPerTurn =
            intent.getIntExtra("secondsPerTurn", 20)

        totalTurns =
            intent.getIntExtra("totalTurns", 0)

        nextWordMaster =
            intent.getStringExtra("nextWordMaster")
                ?: "Winner becomes Word Master"

        playerName =
            intent.getStringExtra("playerName") ?: "Player 1"

        password =
            intent.getStringExtra("password") ?: ""

        isHost =
            intent.getBooleanExtra("isHost", true)
    }

    private fun buildScreen() {
        val scrollView = ScrollView(this)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL

        layout.setPadding(24, 24, 24, 40)
        scrollView.addView(layout)

        // TITLE
        val title = TextView(this)
        title.text = "Game Waiting Room"
        title.textSize = 28f
        title.gravity = Gravity.CENTER
        title.setTextColor(Color.BLACK)

        layout.addView(title)

        // GAME CODE
        val codeTitle = TextView(this)
        codeTitle.text = "Game Code"
        codeTitle.textSize = 20f
        codeTitle.setPadding(0, 30, 0, 5)

        layout.addView(codeTitle)

        val codeText = TextView(this)
        codeText.text = gameCode
        codeText.textSize = 30f
        codeText.gravity = Gravity.CENTER

        codeText.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        layout.addView(codeText)

        // COPY BUTTON
        val copyButton = Button(this)
        copyButton.text = "COPY GAME CODE"
        copyButton.textSize = 16f

        layout.addView(copyButton)

        copyButton.setOnClickListener {
            val clipboard = getSystemService(
                CLIPBOARD_SERVICE
            ) as ClipboardManager

            val clip = ClipData.newPlainText(
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

        // GAME ROOM SUMMARY
        val roomTitle = TextView(this)
        roomTitle.text = "GAME ROOM"
        roomTitle.textSize = 20f

        roomTitle.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        roomTitle.setPadding(0, 30, 0, 10)
        layout.addView(roomTitle)

        joinedTitle = TextView(this)
        joinedTitle.textSize = 19f

        joinedTitle.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        layout.addView(joinedTitle)

        activeTitle = TextView(this)
        activeTitle.textSize = 18f
        activeTitle.setPadding(0, 5, 0, 0)

        layout.addView(activeTitle)

        waitingTitle = TextView(this)
        waitingTitle.textSize = 18f
        waitingTitle.setPadding(0, 5, 0, 10)

        layout.addView(waitingTitle)

        // PLAYERS
        val playersHeading = TextView(this)
        playersHeading.text = "Players"
        playersHeading.textSize = 20f

        playersHeading.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        playersHeading.setPadding(0, 20, 0, 10)
        layout.addView(playersHeading)

        playersText = TextView(this)
        playersText.textSize = 18f

        layout.addView(playersText)

        // GAME SETTINGS
        val settingsTitle = TextView(this)
        settingsTitle.text = "Game Settings"
        settingsTitle.textSize = 20f

        settingsTitle.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        settingsTitle.setPadding(0, 30, 0, 10)
        layout.addView(settingsTitle)

        val settingsText = TextView(this)
        settingsText.textSize = 17f
        settingsText.text = buildSettingsText()

        layout.addView(settingsText)

        // RULE TEXT
        val ruleText = TextView(this)
        ruleText.textSize = 16f
        ruleText.setPadding(0, 25, 0, 15)

        ruleText.text =
            "Players marked Active participate in the current game. " +
            "Players marked Waiting are in the Game Room and will " +
            "be able to participate in future rounds."

        layout.addView(ruleText)

        // STATUS
        statusText = TextView(this)
        statusText.textSize = 18f
        statusText.gravity = Gravity.CENTER
        statusText.setPadding(0, 15, 0, 15)

        layout.addView(statusText)

        // START BUTTON
        startButton = Button(this)
        startButton.text = "START GAME"
        startButton.textSize = 18f

        startButton.visibility =
            if (isHost) View.VISIBLE else View.GONE

        layout.addView(startButton)

        startButton.setOnClickListener {
            startGame()
        }

        // LEAVE BUTTON
        val leaveButton = Button(this)
        leaveButton.text = "LEAVE GAME"
        leaveButton.textSize = 18f

        layout.addView(leaveButton)

        leaveButton.setOnClickListener {
            leaveGame()
        }

        setContentView(scrollView)
    }

    private fun buildSettingsText(): String {
        val timeText =
            if (secondsPerTurn == 0) {
                "Unlimited"
            } else {
                "$secondsPerTurn seconds"
            }

        val turnsText =
            if (totalTurns == 0) {
                "Unlimited"
            } else {
                "$totalTurns turns"
            }

        return """
            Maximum Active Players: $playerCount
            Maximum Room Players: $roomCapacity
            Waiting Player Guesses: $waitingPlayerGuesses
            Word Selection: $wordSelection
            Category: $category
            Time per Turn: $timeText
            Total Turns: $turnsText
            Next Word Master: $nextWordMaster
        """.trimIndent()
    }

    private fun listenToGame() {
        val gameReference = database
            .getReference("games")
            .child(gameCode)

        gameListener = object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                // Ignore callbacks after navigation has started.
                if (
                    openingOnlineGame ||
                    isFinishing ||
                    isDestroyed
                ) {
                    return
                }

                if (!snapshot.exists()) {
                    Toast.makeText(
                        this@GameWaitingActivity,
                        "Game no longer exists.",
                        Toast.LENGTH_LONG
                    ).show()

                    finish()
                    return
                }

                val status = snapshot
                    .child("status")
                    .getValue(String::class.java)
                    ?: "waiting"

                if (status == "playing") {
                    openOnlineGame(snapshot)
                    return
                }

                // Refresh settings from Firebase.
                playerCount = snapshot
                    .child("maxPlayers")
                    .getValue(Int::class.java)
                    ?: playerCount

                roomCapacity = snapshot
                    .child("roomCapacity")
                    .getValue(Int::class.java)
                    ?: playerCount

                waitingPlayerGuesses = snapshot
                    .child("waitingPlayerGuesses")
                    .getValue(Int::class.java)
                    ?: 2

                updatePlayerDisplay(snapshot)
                updateStatus(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                if (!openingOnlineGame && !isFinishing) {
                    statusText.text = "Unable to read game room."
                }
            }
        }

        gameReference.addValueEventListener(gameListener!!)
    }

    private fun updatePlayerDisplay(snapshot: DataSnapshot) {
        val players = snapshot.child("players")

        var totalPlayers = 0
        var activePlayers = 0
        var waitingPlayers = 0

        val text = StringBuilder()

        // Sort players by joinedAt so the display follows join order.
        val playerList = players.children
            .toList()
            .sortedBy {
                it.child("joinedAt")
                    .getValue(Long::class.java) ?: 0L
            }

        for (player in playerList) {
            totalPlayers++

            val name = player
                .child("name")
                .getValue(String::class.java)
                ?: "Player"

            val playerIsHost = player
                .child("isHost")
                .getValue(Boolean::class.java)
                ?: false

            val isActive = player
                .child("isActive")
                .getValue(Boolean::class.java)
                ?: (activePlayers < playerCount)

            if (isActive) {
                activePlayers++

                text.append("🟢 ")
                text.append(name)

                if (playerIsHost) {
                    text.append(" (Host)")
                }

                text.append(" — Active")
            } else {
                waitingPlayers++

                text.append("🟡 ")
                text.append(name)

                if (playerIsHost) {
                    text.append(" (Host)")
                }

                text.append(" — Waiting")
            }

            text.append("\n")
        }

        joinedTitle.text =
            "Total Joined: $totalPlayers / $roomCapacity"

        activeTitle.text =
            "Active Players: $activePlayers / $playerCount"

        waitingTitle.text =
            "Waiting Players: $waitingPlayers / " +
            "${roomCapacity - playerCount}"

        playersText.text =
            if (text.isEmpty()) {
                "No players yet."
            } else {
                text.toString().trim()
            }

        // Refresh displayed settings.
        findSettingsTextView()?.text = buildSettingsText()
    }

    private fun findSettingsTextView(): TextView? {
        val root = window.decorView
            .findViewById<android.view.ViewGroup>(
                android.R.id.content
            )

        return findTextViewContaining(
            root,
            "Maximum Active Players:"
        )
    }

    private fun findTextViewContaining(
        parent: android.view.ViewGroup,
        textStart: String
    ): TextView? {
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)

            if (
                child is TextView &&
                child.text.toString().startsWith(textStart)
            ) {
                return child
            }

            if (child is android.view.ViewGroup) {
                val result = findTextViewContaining(
                    child,
                    textStart
                )

                if (result != null) {
                    return result
                }
            }
        }

        return null
    }

    private fun updateStatus(snapshot: DataSnapshot) {
        val players = snapshot.child("players")
        val count = players.children.count()

        if (isHost) {
            if (count >= 2) {
                startButton.isEnabled =
                    !openingOnlineGame && !checkingStart

                statusText.text = "You can start the game."
            } else {
                startButton.isEnabled = false

                statusText.text =
                    "Waiting for at least 2 players..."
            }
        } else {
            statusText.text =
                "Waiting for the host to start the game..."
        }
    }

    private fun startGame() {
        if (
            !isHost ||
            openingOnlineGame ||
            checkingStart ||
            isFinishing ||
            isDestroyed
        ) {
            return
        }

        checkingStart = true
        startButton.isEnabled = false

        val gameReference = database
            .getReference("games")
            .child(gameCode)

        gameReference.get()
            .addOnSuccessListener { snapshot ->

                if (
                    openingOnlineGame ||
                    isFinishing ||
                    isDestroyed
                ) {
                    return@addOnSuccessListener
                }

                if (!snapshot.exists()) {
                    checkingStart = false
                    startButton.isEnabled = true

                    Toast.makeText(
                        this,
                        "Game no longer exists.",
                        Toast.LENGTH_LONG
                    ).show()

                    return@addOnSuccessListener
                }

                val count = snapshot
                    .child("players")
                    .children
                    .count()

                if (count < 2) {
                    checkingStart = false
                    startButton.isEnabled = true

                    Toast.makeText(
                        this,
                        "At least 2 players are required.",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@addOnSuccessListener
                }

                /*
                 * Do not change status here.
                 * OnlineGameRoundActivity initializes
                 * the first round.
                 */
                openOnlineGame(snapshot)
            }
            .addOnFailureListener {
                if (
                    !isFinishing &&
                    !isDestroyed &&
                    !openingOnlineGame
                ) {
                    checkingStart = false
                    startButton.isEnabled = true

                    Toast.makeText(
                        this,
                        "Could not start the game.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun openOnlineGame(snapshot: DataSnapshot) {
        if (
            openingOnlineGame ||
            isFinishing ||
            isDestroyed
        ) {
            return
        }

        // Set the guard before any navigation work.
        openingOnlineGame = true
        checkingStart = false
        startButton.isEnabled = false

        // Detach Firebase listener before navigating.
        val listener = gameListener

        if (listener != null) {
            database
                .getReference("games")
                .child(gameCode)
                .removeEventListener(listener)

            gameListener = null
        }

        val gameIntent = Intent(
            this,
            OnlineGameRoundActivity::class.java
        )

        gameIntent.putExtra("gameCode", gameCode)
        gameIntent.putExtra("playerCount", playerCount)
        gameIntent.putExtra("roomCapacity", roomCapacity)

        gameIntent.putExtra(
            "waitingPlayerGuesses",
            waitingPlayerGuesses
        )

        gameIntent.putExtra(
            "wordSelection",
            snapshot.child("wordSelection")
                .getValue(String::class.java)
                ?: wordSelection
        )

        gameIntent.putExtra(
            "manualWord",
            snapshot.child("manualWord")
                .getValue(String::class.java)
                ?: manualWord
        )

        gameIntent.putExtra(
            "category",
            snapshot.child("category")
                .getValue(String::class.java)
                ?: category
        )

        gameIntent.putExtra(
            "secondsPerTurn",
            snapshot.child("secondsPerTurn")
                .getValue(Int::class.java)
                ?: secondsPerTurn
        )

        gameIntent.putExtra(
            "totalTurns",
            snapshot.child("totalTurns")
                .getValue(Int::class.java)
                ?: totalTurns
        )

        gameIntent.putExtra(
            "nextWordMaster",
            snapshot.child("nextWordMaster")
                .getValue(String::class.java)
                ?: nextWordMaster
        )

        gameIntent.putExtra("playerName", playerName)
        gameIntent.putExtra("password", password)
        gameIntent.putExtra("isHost", isHost)

        startActivity(gameIntent)

        // Close the waiting room after navigation.
        finish()
    }

    private fun leaveGame() {
        val currentUser =
            com.google.firebase.auth.FirebaseAuth
                .getInstance()
                .currentUser

        if (currentUser == null) {
            finish()
            return
        }

        val uid = currentUser.uid

        val gameReference = database
            .getReference("games")
            .child(gameCode)

        gameReference
            .child("players")
            .child(uid)
            .removeValue()
            .addOnSuccessListener {

                /*
                 * If the host leaves, transfer host status
                 * to the first remaining player.
                 */
                if (isHost) {
                    gameReference.get()
                        .addOnSuccessListener { snapshot ->

                            if (!snapshot.exists()) {
                                finish()
                                return@addOnSuccessListener
                            }

                            val players = snapshot
                                .child("players")
                                .children
                                .toList()
                                .sortedBy {
                                    it.child("joinedAt")
                                        .getValue(Long::class.java)
                                        ?: 0L
                                }

                            if (players.isNotEmpty()) {
                                val newHost = players.first()
                                val newHostUid = newHost.key

                                if (newHostUid != null) {
                                    val newHostName =
                                        newHost.child("name")
                                            .getValue(String::class.java)
                                            ?: "Player"

                                    val updates =
                                        hashMapOf<String, Any>(
                                            "hostUid" to newHostUid,
                                            "hostName" to newHostName,
                                            "players/$newHostUid/isHost" to true
                                        )

                                    gameReference.updateChildren(updates)
                                }
                            } else {
                                // Nobody remains. Remove the empty game.
                                gameReference.removeValue()
                            }

                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                this,
                                "Could not update the game host.",
                                Toast.LENGTH_LONG
                            ).show()

                            finish()
                        }
                } else {
                    finish()
                }
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    "Could not leave the game.",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    override fun onDestroy() {
        val listener = gameListener

        if (listener != null) {
            database
                .getReference("games")
                .child(gameCode)
                .removeEventListener(listener)

            gameListener = null
        }

        super.onDestroy()
    }
}

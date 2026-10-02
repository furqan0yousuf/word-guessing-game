package com.wordguessing.game

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction

class JoinGameActivity : Activity() {

    private val auth =
        FirebaseAuth.getInstance()

    private val database =
        FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout =
            LinearLayout(this)

        layout.orientation =
            LinearLayout.VERTICAL

        layout.setPadding(
            24,
            24,
            24,
            24
        )

        // TITLE
        val title =
            TextView(this)

        title.text =
            "Join Game"

        title.textSize =
            28f

        title.gravity =
            Gravity.CENTER

        title.setTextColor(
            Color.BLACK
        )

        layout.addView(title)

        // GAME CODE
        val codeTitle =
            TextView(this)

        codeTitle.text =
            "Game Code"

        codeTitle.textSize =
            20f

        codeTitle.setPadding(
            0,
            30,
            0,
            10
        )

        layout.addView(codeTitle)

        val codeInput =
            EditText(this)

        codeInput.hint =
            "Enter 6-digit game code"

        codeInput.textSize =
            20f

        codeInput.setSingleLine(true)

        codeInput.inputType =
            android.text.InputType.TYPE_CLASS_NUMBER

        layout.addView(codeInput)

        // PASSWORD
        val passwordTitle =
            TextView(this)

        passwordTitle.text =
            "Password (if required)"

        passwordTitle.textSize =
            20f

        passwordTitle.setPadding(
            0,
            30,
            0,
            10
        )

        layout.addView(passwordTitle)

        val passwordInput =
            EditText(this)

        passwordInput.hint =
            "Enter password"

        passwordInput.textSize =
            20f

        passwordInput.setSingleLine(true)

        layout.addView(passwordInput)

        // PLAYER NAME
        val nameTitle =
            TextView(this)

        nameTitle.text =
            "Your Name"

        nameTitle.textSize =
            20f

        nameTitle.setPadding(
            0,
            30,
            0,
            10
        )

        layout.addView(nameTitle)

        val nameInput =
            EditText(this)

        nameInput.hint =
            "Enter your name"

        nameInput.textSize =
            20f

        nameInput.setSingleLine(true)

        layout.addView(nameInput)

        // JOIN BUTTON
        val joinButton =
            Button(this)

        joinButton.text =
            "Join Game"

        joinButton.textSize =
            18f

        layout.addView(
            joinButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        joinButton.setOnClickListener {

            val gameCode =
                codeInput.text
                    .toString()
                    .trim()

            val enteredPassword =
                passwordInput.text
                    .toString()
                    .trim()

            val playerName =
                nameInput.text
                    .toString()
                    .trim()

            if (gameCode.isEmpty()) {

                Toast.makeText(
                    this,
                    "Please enter the game code.",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (
                gameCode.length != 6 ||
                !gameCode.all { it.isDigit() }
            ) {

                Toast.makeText(
                    this,
                    "Game code must be 6 digits.",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (playerName.isEmpty()) {

                Toast.makeText(
                    this,
                    "Please enter your name.",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            joinButton.isEnabled =
                false

            val currentUser =
                auth.currentUser

            if (currentUser != null) {

                joinFirebaseGame(
                    gameCode =
                        gameCode,
                    playerName =
                        playerName,
                    enteredPassword =
                        enteredPassword,
                    uid =
                        currentUser.uid,
                    joinButton =
                        joinButton
                )

            } else {

                auth.signInAnonymously()
                    .addOnSuccessListener { result ->

                        val uid =
                            result.user?.uid

                        if (uid == null) {

                            joinButton.isEnabled =
                                true

                            Toast.makeText(
                                this,
                                "Firebase login failed.",
                                Toast.LENGTH_LONG
                            ).show()

                            return@addOnSuccessListener
                        }

                        joinFirebaseGame(
                            gameCode =
                                gameCode,
                            playerName =
                                playerName,
                            enteredPassword =
                                enteredPassword,
                            uid =
                                uid,
                            joinButton =
                                joinButton
                        )
                    }
                    .addOnFailureListener {

                        joinButton.isEnabled =
                            true

                        Toast.makeText(
                            this,
                            "Firebase login failed.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
        }

        setContentView(layout)
    }

    private fun joinFirebaseGame(
        gameCode: String,
        playerName: String,
        enteredPassword: String,
        uid: String,
        joinButton: Button
    ) {

        val gameReference =
            database
                .getReference("games")
                .child(gameCode)

        /*
         * First load the game room.
         */
        gameReference.get()
            .addOnSuccessListener { snapshot ->

                if (!snapshot.exists()) {

                    joinButton.isEnabled =
                        true

                    Toast.makeText(
                        this,
                        "Game not found.",
                        Toast.LENGTH_LONG
                    ).show()

                    return@addOnSuccessListener
                }

                val status =
                    snapshot
                        .child("status")
                        .getValue(
                            String::class.java
                        )
                        ?: "waiting"

                if (status != "waiting") {

                    joinButton.isEnabled =
                        true

                    Toast.makeText(
                        this,
                        "This game has already started.",
                        Toast.LENGTH_LONG
                    ).show()

                    return@addOnSuccessListener
                }

                /*
                 * Check password.
                 */
                val gamePassword =
                    snapshot
                        .child("password")
                        .getValue(
                            String::class.java
                        )
                        ?: ""

                if (
                    gamePassword.isNotEmpty() &&
                    enteredPassword != gamePassword
                ) {

                    joinButton.isEnabled =
                        true

                    Toast.makeText(
                        this,
                        "Incorrect game password.",
                        Toast.LENGTH_LONG
                    ).show()

                    return@addOnSuccessListener
                }

                /*
                 * Existing setting:
                 *
                 * Maximum ACTIVE players.
                 */
                val maxPlayers =
                    snapshot
                        .child("maxPlayers")
                        .getValue(
                            Int::class.java
                        )
                        ?: 2

                /*
                 * New setting:
                 *
                 * Maximum TOTAL people allowed
                 * in the Game Room.
                 *
                 * The fallback keeps older games
                 * working exactly as before.
                 */
                val roomCapacity =
                    snapshot
                        .child("roomCapacity")
                        .getValue(
                            Int::class.java
                        )
                        ?: maxPlayers

                /*
                 * New waiting-player setting.
                 *
                 * Default = 2.
                 */
                val waitingPlayerGuesses =
                    snapshot
                        .child("waitingPlayerGuesses")
                        .getValue(
                            Int::class.java
                        )
                        ?: 2

                /*
                 * Use a Firebase transaction so
                 * two phones cannot easily take
                 * the same final room slot.
                 */
                gameReference.runTransaction(
                    object : Transaction.Handler {

                        override fun doTransaction(
                            currentData:
                                MutableData
                        ): Transaction.Result {

                            val playersData =
                                currentData
                                    .child("players")

                            val existingPlayer =
                                playersData
                                    .child(uid)

                            /*
                             * If this device is already
                             * in the game, don't add it
                             * again.
                             */
                            if (
                                existingPlayer.value !=
                                null
                            ) {

                                return Transaction.success(
                                    currentData
                                )
                            }

                            var totalPlayers =
                                0

                            var activePlayers =
                                0

                            /*
                             * Count everyone in the room.
                             *
                             * Also count currently active
                             * players.
                             */
                            for (
                                player in
                                playersData.children
                            ) {

                                totalPlayers++

                                val isActive =
                                    player
                                        .child(
                                            "isActive"
                                        )
                                        .getValue(
                                            Boolean::class.java
                                        )
                                        ?: (
                                            totalPlayers <=
                                                maxPlayers
                                            )

                                if (isActive) {
                                    activePlayers++
                                }
                            }

                            /*
                             * Room capacity controls
                             * whether this person can
                             * enter the room.
                             */
                            if (
                                totalPlayers >=
                                roomCapacity
                            ) {

                                return Transaction.abort()
                            }

                            /*
                             * The first maxPlayers
                             * people are active.
                             *
                             * Everyone after that
                             * becomes a waiting player.
                             */
                            val shouldBeActive =
                                activePlayers <
                                    maxPlayers

                            val playerData =
                                hashMapOf<String, Any>(
                                    "uid" to uid,
                                    "name" to playerName,
                                    "isHost" to false,
                                    "isActive" to
                                        shouldBeActive,
                                    "joinedAt" to
                                        System.currentTimeMillis()
                                )

                            playersData
                                .child(uid)
                                .value =
                                playerData

                            return Transaction.success(
                                currentData
                            )
                        }

                        override fun onComplete(
                            error: DatabaseError?,
                            committed: Boolean,
                            currentData:
                                DataSnapshot?
                        ) {

                            if (error != null) {

                                joinButton.isEnabled =
                                    true

                                Toast.makeText(
                                    this@JoinGameActivity,
                                    "Could not join the game.",
                                    Toast.LENGTH_LONG
                                ).show()

                                return
                            }

                            if (!committed) {

                                joinButton.isEnabled =
                                    true

                                Toast.makeText(
                                    this@JoinGameActivity,
                                    "The game room is full.",
                                    Toast.LENGTH_LONG
                                ).show()

                                return
                            }

                            /*
                             * Successfully joined.
                             *
                             * Use the actual settings
                             * stored in Firebase.
                             */
                            val gameSnapshot =
                                currentData
                                    ?: snapshot

                            openWaitingRoom(
                                gameSnapshot =
                                    gameSnapshot,
                                gameCode =
                                    gameCode,
                                playerName =
                                    playerName,
                                enteredPassword =
                                    enteredPassword
                            )
                        }
                    }
                )
            }
            .addOnFailureListener {

                joinButton.isEnabled =
                    true

                Toast.makeText(
                    this,
                    "Could not connect to Firebase.",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun openWaitingRoom(
        gameSnapshot:
            DataSnapshot,
        gameCode: String,
        playerName: String,
        enteredPassword: String
    ) {

        val playerCount =
            gameSnapshot
                .child("maxPlayers")
                .getValue(
                    Int::class.java
                )
                ?: 2

        val roomCapacity =
            gameSnapshot
                .child("roomCapacity")
                .getValue(
                    Int::class.java
                )
                ?: playerCount

        val waitingPlayerGuesses =
            gameSnapshot
                .child(
                    "waitingPlayerGuesses"
                )
                .getValue(
                    Int::class.java
                )
                ?: 2

        val wordSelection =
            gameSnapshot
                .child("wordSelection")
                .getValue(
                    String::class.java
                )
                ?: "Random Word"

        val manualWord =
            gameSnapshot
                .child("manualWord")
                .getValue(
                    String::class.java
                )
                ?: ""

        val category =
            gameSnapshot
                .child("category")
                .getValue(
                    String::class.java
                )
                ?: "Random"

        val secondsPerTurn =
            gameSnapshot
                .child("secondsPerTurn")
                .getValue(
                    Int::class.java
                )
                ?: 20

        val totalTurns =
            gameSnapshot
                .child("totalTurns")
                .getValue(
                    Int::class.java
                )
                ?: 0

        val nextWordMaster =
            gameSnapshot
                .child("nextWordMaster")
                .getValue(
                    String::class.java
                )
                ?: "Winner becomes Word Master"

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
            "roomCapacity",
            roomCapacity
        )

        intent.putExtra(
            "waitingPlayerGuesses",
            waitingPlayerGuesses
        )

        intent.putExtra(
            "wordSelection",
            wordSelection
        )

        intent.putExtra(
            "manualWord",
            manualWord
        )

        intent.putExtra(
            "category",
            category
        )

        intent.putExtra(
            "secondsPerTurn",
            secondsPerTurn
        )

        intent.putExtra(
            "totalTurns",
            totalTurns
        )

        intent.putExtra(
            "nextWordMaster",
            nextWordMaster
        )

        intent.putExtra(
            "playerName",
            playerName
        )

        intent.putExtra(
            "password",
            enteredPassword
        )

        intent.putExtra(
            "isHost",
            false
        )

        startActivity(intent)
    }
}

package com.wordguessing.game

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AlertDialog

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener

class JoinGameActivity : Activity() {

    private val auth =
        FirebaseAuth.getInstance()

    private val database =
        FirebaseDatabase.getInstance()

    private lateinit var publicGamesContainer:
        LinearLayout

    private lateinit var refreshButton:
        Button

    private var gamesListener:
        ValueEventListener? = null

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(
            savedInstanceState
        )

        buildScreen()
    }

    override fun onResume() {

        super.onResume()

        listenToPublicGames()
    }

    override fun onPause() {

        super.onPause()

        removePublicGamesListener()
    }

    private fun buildScreen() {

        val scrollView =
            ScrollView(this)

        val layout =
            LinearLayout(this)

        layout.orientation =
            LinearLayout.VERTICAL

        layout.setPadding(
            24,
            24,
            24,
            40
        )

        scrollView.addView(
            layout
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

        layout.addView(
            title
        )

        // ==========================================
        // ENTER GAME CODE SECTION
        // ==========================================

        val codeSectionTitle =
            TextView(this)

        codeSectionTitle.text =
            "JOIN WITH GAME CODE"

        codeSectionTitle.textSize =
            20f

        codeSectionTitle.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        codeSectionTitle.setPadding(
            0,
            30,
            0,
            10
        )

        layout.addView(
            codeSectionTitle
        )

        // GAME CODE
        val codeTitle =
            TextView(this)

        codeTitle.text =
            "Game Code"

        codeTitle.textSize =
            20f

        codeTitle.setPadding(
            0,
            15,
            0,
            10
        )

        layout.addView(
            codeTitle
        )

        val codeInput =
            EditText(this)

        codeInput.hint =
            "Enter 6-digit game code"

        codeInput.textSize =
            20f

        codeInput.setSingleLine(
            true
        )

        codeInput.inputType =
            InputType.TYPE_CLASS_NUMBER

        layout.addView(
            codeInput
        )

        // PASSWORD
        val passwordTitle =
            TextView(this)

        passwordTitle.text =
            "Password (if required)"

        passwordTitle.textSize =
            20f

        passwordTitle.setPadding(
            0,
            25,
            0,
            10
        )

        layout.addView(
            passwordTitle
        )

        val passwordInput =
            EditText(this)

        passwordInput.hint =
            "Enter password"

        passwordInput.textSize =
            20f

        passwordInput.setSingleLine(
            true
        )

        passwordInput.inputType =
            InputType.TYPE_CLASS_TEXT or
                InputType.TYPE_TEXT_VARIATION_PASSWORD

        layout.addView(
            passwordInput
        )

        // PLAYER NAME
        val nameTitle =
            TextView(this)

        nameTitle.text =
            "Your Name"

        nameTitle.textSize =
            20f

        nameTitle.setPadding(
            0,
            25,
            0,
            10
        )

        layout.addView(
            nameTitle
        )

        val nameInput =
            EditText(this)

        nameInput.hint =
            "Enter your name"

        nameInput.textSize =
            20f

        nameInput.setSingleLine(
            true
        )

        layout.addView(
            nameInput
        )

        // JOIN BUTTON
        val joinButton =
            Button(this)

        joinButton.text =
            "JOIN GAME"

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

            joinByCode(
                gameCode =
                    gameCode,
                enteredPassword =
                    enteredPassword,
                playerName =
                    playerName,
                joinButton =
                    joinButton
            )
        }

        // ==========================================
        // PUBLIC GAMES SECTION
        // ==========================================

        val publicTitle =
            TextView(this)

        publicTitle.text =
            "PUBLIC GAMES"

        publicTitle.textSize =
            22f

        publicTitle.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        publicTitle.setPadding(
            0,
            40,
            0,
            5
        )

        layout.addView(
            publicTitle
        )

        val publicDescription =
            TextView(this)

        publicDescription.text =
            "Browse games that are currently waiting for players."

        publicDescription.textSize =
            16f

        publicDescription.setPadding(
            0,
            0,
            0,
            10
        )

        layout.addView(
            publicDescription
        )

        refreshButton =
            Button(this)

        refreshButton.text =
            "REFRESH PUBLIC GAMES"

        refreshButton.textSize =
            16f

        layout.addView(
            refreshButton
        )

        refreshButton.setOnClickListener {

            listenToPublicGames()
        }

        publicGamesContainer =
            LinearLayout(this)

        publicGamesContainer.orientation =
            LinearLayout.VERTICAL

        publicGamesContainer.setPadding(
            0,
            10,
            0,
            20
        )

        layout.addView(
            publicGamesContainer
        )

        setContentView(
            scrollView
        )
    }

    private fun joinByCode(
        gameCode: String,
        enteredPassword: String,
        playerName: String,
        joinButton: Button
    ) {

        if (gameCode.isEmpty()) {

            Toast.makeText(
                this,
                "Please enter the game code.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (
            gameCode.length != 6 ||
            !gameCode.all {
                it.isDigit()
            }
        ) {

            Toast.makeText(
                this,
                "Game code must be 6 digits.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (playerName.isEmpty()) {

            Toast.makeText(
                this,
                "Please enter your name.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        joinButton.isEnabled =
            false

        authenticateAndJoin(
            gameCode =
                gameCode,
            playerName =
                playerName,
            enteredPassword =
                enteredPassword,
            joinButton =
                joinButton
        )
    }

    private fun authenticateAndJoin(
        gameCode: String,
        playerName: String,
        enteredPassword: String,
        joinButton: Button
    ) {

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

    // ==========================================
    // PUBLIC GAME BROWSER
    // ==========================================

    private fun listenToPublicGames() {

        removePublicGamesListener()

        publicGamesContainer.removeAllViews()

        val loadingText =
            TextView(this)

        loadingText.text =
            "Loading public games..."

        loadingText.textSize =
            17f

        loadingText.gravity =
            Gravity.CENTER

        loadingText.setPadding(
            0,
            15,
            0,
            15
        )

        publicGamesContainer.addView(
            loadingText
        )

        val gamesReference =
            database
                .getReference("games")

        gamesListener =
            object : ValueEventListener {

                override fun onDataChange(
                    snapshot: DataSnapshot
                ) {

                    publicGamesContainer
                        .removeAllViews()

                    var foundGames =
                        0

                    /*
                     * Sort by creation time when
                     * available.
                     */
                    val gameList =
                        snapshot.children
                            .toList()
                            .sortedByDescending {
                                it.child(
                                    "createdAt"
                                ).getValue(
                                    Long::class.java
                                ) ?: 0L
                            }

                    for (
                        game in gameList
                    ) {

                        val status =
                            game.child(
                                "status"
                            ).getValue(
                                String::class.java
                            ) ?: "waiting"

                        if (
                            status !=
                            "waiting"
                        ) {
                            continue
                        }

                        val isPublic =
                            game.child(
                                "isPublic"
                            ).getValue(
                                Boolean::class.java
                            ) ?: (
                                game.child(
                                    "roomType"
                                ).getValue(
                                    String::class.java
                                ) == "Public"
                            )

                        if (!isPublic) {
                            continue
                        }

                        val gameCode =
                            game.key
                                ?: continue

                        val maxPlayers =
                            game.child(
                                "maxPlayers"
                            ).getValue(
                                Int::class.java
                            ) ?: 2

                        val roomCapacity =
                            game.child(
                                "roomCapacity"
                            ).getValue(
                                Int::class.java
                            ) ?: maxPlayers

                        val players =
                            game.child(
                                "players"
                            )

                        var totalPlayers =
                            0

                        for (
                            ignored in
                            players.children
                        ) {
                            totalPlayers++
                        }

                        /*
                         * Do not display a room that
                         * is already full.
                         */
                        if (
                            totalPlayers >=
                            roomCapacity
                        ) {
                            continue
                        }

                        foundGames++

                        addPublicGameCard(
                            game =
                                game,
                            gameCode =
                                gameCode,
                            totalPlayers =
                                totalPlayers,
                            maxPlayers =
                                maxPlayers,
                            roomCapacity =
                                roomCapacity
                        )
                    }

                    if (
                        foundGames == 0
                    ) {

                        val emptyText =
                            TextView(this@JoinGameActivity)

                        emptyText.text =
                            "No public games are currently available."

                        emptyText.textSize =
                            17f

                        emptyText.gravity =
                            Gravity.CENTER

                        emptyText.setPadding(
                            0,
                            20,
                            0,
                            20
                        )

                        publicGamesContainer
                            .addView(
                                emptyText
                            )
                    }
                }

                override fun onCancelled(
                    error: DatabaseError
                ) {

                    publicGamesContainer
                        .removeAllViews()

                    val errorText =
                        TextView(
                            this@JoinGameActivity
                        )

                    errorText.text =
                        "Unable to load public games."

                    errorText.textSize =
                        17f

                    errorText.gravity =
                        Gravity.CENTER

                    publicGamesContainer
                        .addView(
                            errorText
                        )
                }
            }

        gamesReference.addValueEventListener(
            gamesListener!!
        )
    }

    private fun addPublicGameCard(
        game: DataSnapshot,
        gameCode: String,
        totalPlayers: Int,
        maxPlayers: Int,
        roomCapacity: Int
    ) {

        val card =
            LinearLayout(this)

        card.orientation =
            LinearLayout.VERTICAL

        card.setPadding(
            20,
            20,
            20,
            20
        )

        /*
         * Simple border/background using
         * a GradientDrawable.
         */
        val background =
            android.graphics.drawable.GradientDrawable()

        background.setColor(
            Color.rgb(
                245,
                245,
                245
            )
        )

        background.setStroke(
            2,
            Color.LTGRAY
        )

        background.cornerRadius =
            18f

        card.background =
            background

        val cardParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        cardParams.setMargins(
            0,
            0,
            0,
            18
        )

        publicGamesContainer.addView(
            card,
            cardParams
        )

        val hostName =
            game.child(
                "hostName"
            ).getValue(
                String::class.java
            ) ?: "Unknown Host"

        val category =
            game.child(
                "category"
            ).getValue(
                String::class.java
            ) ?: "Random"

        val wordSelection =
            game.child(
                "wordSelection"
            ).getValue(
                String::class.java
            ) ?: "Random Word"

        val secondsPerTurn =
            game.child(
                "secondsPerTurn"
            ).getValue(
                Int::class.java
            ) ?: 20

        val totalTurns =
            game.child(
                "totalTurns"
            ).getValue(
                Int::class.java
            ) ?: 0

        val password =
            game.child(
                "password"
            ).getValue(
                String::class.java
            ) ?: ""

        val hasPassword =
            password.isNotEmpty()

        // GAME CODE
        val codeText =
            TextView(this)

        codeText.text =
            if (hasPassword) {
                "🎮 $gameCode   🔒"
            } else {
                "🎮 $gameCode"
            }

        codeText.textSize =
            23f

        codeText.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        card.addView(
            codeText
        )

        // HOST
        val hostText =
            TextView(this)

        hostText.text =
            "Host: $hostName"

        hostText.textSize =
            18f

        hostText.setPadding(
            0,
            8,
            0,
            0
        )

        card.addView(
            hostText
        )

        // PLAYERS
        val playersText =
            TextView(this)

        playersText.text =
            "Players: $totalPlayers / $roomCapacity " +
                "(Active limit: $maxPlayers)"

        playersText.textSize =
            17f

        playersText.setPadding(
            0,
            5,
            0,
            0
        )

        card.addView(
            playersText
        )

        // CATEGORY
        val categoryText =
            TextView(this)

        categoryText.text =
            "Category: $category"

        categoryText.textSize =
            17f

        categoryText.setPadding(
            0,
            5,
            0,
            0
        )

        card.addView(
            categoryText
        )

        // WORD SELECTION
        val wordText =
            TextView(this)

        wordText.text =
            "Word Selection: $wordSelection"

        wordText.textSize =
            17f

        wordText.setPadding(
            0,
            5,
            0,
            0
        )

        card.addView(
            wordText
        )

        // TIME
        val timeText =
            TextView(this)

        timeText.text =
            if (secondsPerTurn == 0) {
                "Time per Turn: Unlimited"
            } else {
                "Time per Turn: $secondsPerTurn seconds"
            }

        timeText.textSize =
            17f

        timeText.setPadding(
            0,
            5,
            0,
            0
        )

        card.addView(
            timeText
        )

        // TOTAL TURNS
        val turnsText =
            TextView(this)

        turnsText.text =
            if (totalTurns == 0) {
                "Total Turns: Unlimited"
            } else {
                "Total Turns: $totalTurns"
            }

        turnsText.textSize =
            17f

        turnsText.setPadding(
            0,
            5,
            0,
            10
        )

        card.addView(
            turnsText
        )

        // JOIN BUTTON
        val joinButton =
            Button(this)

        joinButton.text =
            if (hasPassword) {
                "JOIN 🔒"
            } else {
                "JOIN GAME"
            }

        joinButton.textSize =
            17f

        card.addView(
            joinButton
        )

        joinButton.setOnClickListener {

            showPublicGameJoinDialog(
                gameCode =
                    gameCode,
                requiresPassword =
                    hasPassword
            )
        }
    }

    private fun showPublicGameJoinDialog(
        gameCode: String,
        requiresPassword: Boolean
    ) {

        val dialogLayout =
            LinearLayout(this)

        dialogLayout.orientation =
            LinearLayout.VERTICAL

        dialogLayout.setPadding(
            40,
            10,
            40,
            10
        )

        val nameInput =
            EditText(this)

        nameInput.hint =
            "Your name"

        nameInput.setSingleLine(
            true
        )

        dialogLayout.addView(
            nameInput
        )

        val passwordInput =
            EditText(this)

        passwordInput.hint =
            if (requiresPassword) {
                "Game password"
            } else {
                "Password not required"
            }

        passwordInput.setSingleLine(
            true
        )

        passwordInput.inputType =
            InputType.TYPE_CLASS_TEXT or
                InputType.TYPE_TEXT_VARIATION_PASSWORD

        passwordInput.isEnabled =
            requiresPassword

        val passwordParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        passwordParams.setMargins(
            0,
            20,
            0,
            0
        )

        dialogLayout.addView(
            passwordInput,
            passwordParams
        )

        val dialog =
            AlertDialog.Builder(this)
                .setTitle(
                    "Join Public Game"
                )
                .setView(
                    dialogLayout
                )
                .setNegativeButton(
                    "CANCEL",
                    null
                )
                .setPositiveButton(
                    "JOIN",
                    null
                )
                .create()

        dialog.setOnShowListener {

            val positiveButton =
                dialog.getButton(
                    AlertDialog.BUTTON_POSITIVE
                )

            positiveButton.setOnClickListener {

                val playerName =
                    nameInput.text
                        .toString()
                        .trim()

                val enteredPassword =
                    passwordInput.text
                        .toString()
                        .trim()

                if (
                    playerName.isEmpty()
                ) {

                    nameInput.error =
                        "Enter your name."

                    return@setOnClickListener
                }

                if (
                    requiresPassword &&
                    enteredPassword.isEmpty()
                ) {

                    passwordInput.error =
                        "Enter the password."

                    return@setOnClickListener
                }

                positiveButton.isEnabled =
                    false

                authenticateAndJoinPublicGame(
                    gameCode =
                        gameCode,
                    playerName =
                        playerName,
                    enteredPassword =
                        enteredPassword,
                    positiveButton =
                        positiveButton,
                    dialog =
                        dialog
                )
            }
        }

        dialog.show()
    }

    private fun authenticateAndJoinPublicGame(
        gameCode: String,
        playerName: String,
        enteredPassword: String,
        positiveButton: Button,
        dialog: AlertDialog
    ) {

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
                    positiveButton,
                dialogToClose =
                    dialog
            )

        } else {

            auth.signInAnonymously()
                .addOnSuccessListener { result ->

                    val uid =
                        result.user?.uid

                    if (uid == null) {

                        positiveButton.isEnabled =
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
                            positiveButton,
                        dialogToClose =
                            dialog
                    )
                }
                .addOnFailureListener {

                    positiveButton.isEnabled =
                        true

                    Toast.makeText(
                        this,
                        "Firebase login failed.",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }

    // ==========================================
    // FIREBASE JOIN
    // ==========================================

    private fun joinFirebaseGame(
        gameCode: String,
        playerName: String,
        enteredPassword: String,
        uid: String,
        joinButton: Button,
        dialogToClose: AlertDialog? = null
    ) {

        val gameReference =
            database
                .getReference("games")
                .child(gameCode)

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

                if (
                    status != "waiting"
                ) {

                    joinButton.isEnabled =
                        true

                    Toast.makeText(
                        this,
                        "This game has already started.",
                        Toast.LENGTH_LONG
                    ).show()

                    return@addOnSuccessListener
                }

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

                val maxPlayers =
                    snapshot
                        .child("maxPlayers")
                        .getValue(
                            Int::class.java
                        )
                        ?: 2

                val roomCapacity =
                    snapshot
                        .child("roomCapacity")
                        .getValue(
                            Int::class.java
                        )
                        ?: maxPlayers

                val waitingPlayerGuesses =
                    snapshot
                        .child(
                            "waitingPlayerGuesses"
                        )
                        .getValue(
                            Int::class.java
                        )
                        ?: 2

                gameReference.runTransaction(
                    object : Transaction.Handler {

                        override fun doTransaction(
                            currentData:
                                MutableData
                        ): Transaction.Result {

                            val playersData =
                                currentData
                                    .child(
                                        "players"
                                    )

                            val existingPlayer =
                                playersData
                                    .child(uid)

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

                                if (
                                    isActive
                                ) {
                                    activePlayers++
                                }
                            }

                            if (
                                totalPlayers >=
                                roomCapacity
                            ) {

                                return Transaction.abort()
                            }

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

                            if (
                                error != null
                            ) {

                                joinButton.isEnabled =
                                    true

                                Toast.makeText(
                                    this@JoinGameActivity,
                                    "Could not join the game.",
                                    Toast.LENGTH_LONG
                                ).show()

                                return
                            }

                            if (
                                !committed
                            ) {

                                joinButton.isEnabled =
                                    true

                                Toast.makeText(
                                    this@JoinGameActivity,
                                    "The game room is full.",
                                    Toast.LENGTH_LONG
                                ).show()

                                return
                            }

                            dialogToClose?.dismiss()

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

    // ==========================================
    // WAITING ROOM
    // ==========================================

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

        startActivity(
            intent
        )
    }

    // ==========================================
    // LISTENER CLEANUP
    // ==========================================

    private fun removePublicGamesListener() {

        val listener =
            gamesListener

        if (
            listener != null
        ) {

            database
                .getReference("games")
                .removeEventListener(
                    listener
                )

            gamesListener =
                null
        }
    }

    override fun onDestroy() {

        removePublicGamesListener()

        super.onDestroy()
    }
}

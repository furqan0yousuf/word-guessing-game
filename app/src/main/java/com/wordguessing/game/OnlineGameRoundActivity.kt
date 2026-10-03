package com.wordguessing.game

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.util.TypedValue
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import kotlin.random.Random

class OnlineGameRoundActivity : Activity() {

    private lateinit var timerText: TextView
    private lateinit var turnText: TextView
    private lateinit var playersText: TextView
    private lateinit var wordText: TextView
    private lateinit var statusText: TextView
    private lateinit var wholeWordButton: Button
    private lateinit var categoryText: TextView
    private lateinit var letterBoard: LinearLayout
    private lateinit var wordMasterText: TextView

    private var timer: CountDownTimer? = null
    private var wholeWordTimer: CountDownTimer? = null

    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private var gameListener: ValueEventListener? = null

    private var gameCode = "------"
    private var myUid = ""

    private var maxPlayers = 2
    private var roomCapacity = 4
    private var waitingPlayerGuesses = 2

    private var wordSelection = "Random Word"
    private var selectedCategory = "Random"
    private var secondsPerTurn = 20
    private var totalTurns = 0
    private var nextWordMaster = "Winner becomes Word Master"
    private var manualWord: String? = null

    private var status = "waiting"
    private var roundNumber = 0
    private var roundFinished = false
    private var phase = "normal"

    private var secretWord = ""
    private var actualCategory = "Random"
    private var currentPlayerUid = ""
    private var wordMasterUid = ""
    private var turnEndsAt = 0L
    private var completedTurns = 0

    private var roundWinnerUid = ""
    private var roundWinnerName = ""

    private var finalChallengeIndex = 0

    private val playerNames = mutableMapOf<String, String>()
    private val playerOrder = mutableListOf<String>()

    /*
     * This is important.
     *
     * The previous version did not keep the Firebase isActive
     * value locally and instead guessed whether a player was
     * active based on their position in playerOrder.
     *
     * That could cause waiting players to be treated as active.
     */
    private val playerActiveStates =
        mutableMapOf<String, Boolean>()

    private val scores = mutableMapOf<String, Int>()
    private val missedTurns = mutableMapOf<String, Int>()
    private val eliminatedPlayers = mutableSetOf<String>()
    private val guessedLetters = mutableSetOf<Char>()

    private val letters =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

    private var wholeWordAttemptUsed = false

    private var lastShownRoundFinished = 0
    private var lastShownFinalIndex = -1

    private var knownHostUid = ""

    private val lastKnownPlayerIds =
        mutableSetOf<String>()

    private val lastKnownPlayerNames =
        mutableMapOf<String, String>()

    private val notifiedDepartures =
        mutableSetOf<String>()

    private var playerChangeInitialized = false
    private var singlePlayerDialogShown = false
    private var leavingGame = false

    /*
     * Timer synchronization state.
     *
     * The old code cancelled and restarted the CountDownTimer
     * on every Firebase snapshot. Clicking a letter causes a
     * Firebase snapshot, so this made the screen appear to
     * refresh/flicker.
     *
     * We now restart the timer only when one of these values
     * actually changes.
     */
    private var timerStateUid = ""
    private var timerStateEndsAt = -1L
    private var timerStatePhase = ""
    private var timerStateRound = -1

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        gameCode =
            intent.getStringExtra("gameCode")
                ?: "------"

        maxPlayers =
            intent.getIntExtra(
                "maxPlayers",
                intent.getIntExtra(
                    "playerCount",
                    2
                )
            )

        roomCapacity =
            intent.getIntExtra(
                "roomCapacity",
                maxPlayers
            )

        waitingPlayerGuesses =
            intent.getIntExtra(
                "waitingPlayerGuesses",
                2
            )

        wordSelection =
            intent.getStringExtra(
                "wordSelection"
            )
                ?: "Random Word"

        selectedCategory =
            intent.getStringExtra(
                "category"
            )
                ?: "Random"

        secondsPerTurn =
            intent.getIntExtra(
                "secondsPerTurn",
                20
            )

        totalTurns =
            intent.getIntExtra(
                "totalTurns",
                0
            )

        nextWordMaster =
            intent.getStringExtra(
                "nextWordMaster"
            )
                ?: "Winner becomes Word Master"

        manualWord =
            intent.getStringExtra(
                "manualWord"
            )
                ?.trim()
                ?.uppercase()

        myUid =
            auth.currentUser?.uid
                ?: ""

        createInitialScreen()

        if (myUid.isEmpty()) {

            statusText.text =
                "Firebase login is not available."

            statusText.setTextColor(
                Color.RED
            )

            return
        }

        startFirebaseListener()
    }

    private fun gameReference() =
        database.reference
            .child("games")
            .child(gameCode)

    private fun createInitialScreen() {

        val scrollView =
            ScrollView(this)

        val layout =
            LinearLayout(this)

        layout.orientation =
            LinearLayout.VERTICAL

        layout.setPadding(
            16,
            10,
            16,
            20
        )

        scrollView.addView(layout)

        val title =
            TextView(this)

        title.text =
            "Online Game"

        title.textSize =
            25f

        title.gravity =
            Gravity.CENTER

        title.setTypeface(
            null,
            Typeface.BOLD
        )

        layout.addView(title)

        val codeText =
            TextView(this)

        codeText.text =
            "Game Code: $gameCode"

        codeText.textSize =
            17f

        codeText.gravity =
            Gravity.CENTER

        codeText.setTypeface(
            null,
            Typeface.BOLD
        )

        codeText.setTextColor(
            Color.rgb(0, 70, 140)
        )

        layout.addView(codeText)

        categoryText =
            TextView(this)

        categoryText.text =
            "CATEGORY: Waiting..."

        categoryText.textSize =
            20f

        categoryText.gravity =
            Gravity.CENTER

        categoryText.setTypeface(
            null,
            Typeface.BOLD
        )

        categoryText.setTextColor(
            Color.rgb(0, 70, 140)
        )

        categoryText.setPadding(
            8,
            8,
            8,
            8
        )

        layout.addView(categoryText)

        val info =
            TextView(this)

        info.text =
            """
            Active Players: $maxPlayers
            Room Capacity: $roomCapacity
            Waiting Player Guesses: $waitingPlayerGuesses

            Turn Time: ${
                if (secondsPerTurn <= 0)
                    "Unlimited"
                else
                    "$secondsPerTurn seconds"
            }

            Total Turns: ${
                if (totalTurns <= 0)
                    "Unlimited"
                else
                    "$totalTurns turns"
            }

            Next Word Master: $nextWordMaster
            """.trimIndent()

        info.textSize =
            14f

        info.gravity =
            Gravity.CENTER

        layout.addView(info)

        val playersTitle =
            TextView(this)

        playersTitle.text =
            "PLAYERS"

        playersTitle.textSize =
            19f

        playersTitle.gravity =
            Gravity.CENTER

        playersTitle.setTypeface(
            null,
            Typeface.BOLD
        )

        layout.addView(playersTitle)

        playersText =
            TextView(this)

        playersText.textSize =
            16f

        playersText.setTypeface(
            null,
            Typeface.BOLD
        )

        playersText.setPadding(
            0,
            5,
            0,
            8
        )

        layout.addView(playersText)

        wordMasterText =
            TextView(this)

        wordMasterText.text =
            "Word Master information will appear here."

        wordMasterText.textSize =
            15f

        wordMasterText.gravity =
            Gravity.CENTER

        wordMasterText.setTypeface(
            null,
            Typeface.BOLD
        )

        wordMasterText.setTextColor(
            Color.DKGRAY
        )

        layout.addView(wordMasterText)

        wordText =
            TextView(this)

        wordText.setTextSize(
            TypedValue.COMPLEX_UNIT_SP,
            30f
        )

        wordText.setAutoSizeTextTypeUniformWithConfiguration(
            16,
            30,
            1,
            TypedValue.COMPLEX_UNIT_SP
        )

        wordText.maxLines = 1
        wordText.isSingleLine = true
        wordText.ellipsize = null
        wordText.gravity =
            Gravity.CENTER

        wordText.setTypeface(
            null,
            Typeface.BOLD
        )

        wordText.setPadding(
            0,
            10,
            0,
            10
        )

        layout.addView(
            wordText,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        wordText.text =
            "Waiting for game..."

        turnText =
            TextView(this)

        turnText.text =
            "Waiting..."

        turnText.textSize =
            21f

        turnText.gravity =
            Gravity.CENTER

        turnText.setTypeface(
            null,
            Typeface.BOLD
        )

        turnText.setPadding(
            0,
            4,
            0,
            2
        )

        layout.addView(turnText)

        timerText =
            TextView(this)

        timerText.text =
            "Time: --"

        timerText.textSize =
            23f

        timerText.gravity =
            Gravity.CENTER

        timerText.setTypeface(
            null,
            Typeface.BOLD
        )

        layout.addView(timerText)

        wholeWordButton =
            Button(this)

        wholeWordButton.text =
            "GUESS WHOLE WORD"

        wholeWordButton.textSize =
            17f

        wholeWordButton.setTypeface(
            null,
            Typeface.BOLD
        )

        wholeWordButton.setTextColor(
            Color.WHITE
        )

        wholeWordButton.setBackgroundColor(
            Color.rgb(0, 70, 140)
        )

        wholeWordButton.isEnabled =
            false

        wholeWordButton.setOnClickListener {

            if (
                roundFinished ||
                phase != "normal"
            ) {
                return@setOnClickListener
            }

            if (
                !isMyPlayerActive()
            ) {

                showStatus(
                    "You are a waiting player. Active players take the turns.",
                    Color.RED
                )

                return@setOnClickListener
            }

            if (
                currentPlayerUid != myUid
            ) {

                showStatus(
                    "It is not your turn.",
                    Color.RED
                )

                return@setOnClickListener
            }

            if (wholeWordAttemptUsed) {

                showStatus(
                    "You already used your whole-word guess this turn.",
                    Color.RED
                )

                return@setOnClickListener
            }

            showWholeWordDialog()
        }

        layout.addView(
            wholeWordButton
        )

        statusText =
            TextView(this)

        statusText.text =
            "Connecting to Firebase..."

        statusText.textSize =
            15f

        statusText.gravity =
            Gravity.CENTER

        statusText.setTypeface(
            null,
            Typeface.BOLD
        )

        statusText.setPadding(
            4,
            5,
            4,
            5
        )

        layout.addView(statusText)

        letterBoard =
            LinearLayout(this)

        letterBoard.orientation =
            LinearLayout.VERTICAL

        layout.addView(letterBoard)

        createLetterButtons()

        val leaveButton =
            Button(this)

        leaveButton.text =
            "Leave Game"

        leaveButton.textSize =
            16f

        leaveButton.setOnClickListener {
            confirmLeaveGame()
        }

        layout.addView(
            leaveButton
        )

        setContentView(scrollView)
    }

    private fun createLetterButtons() {

        letterBoard.removeAllViews()

        var currentRow =
            LinearLayout(this)

        currentRow.orientation =
            LinearLayout.HORIZONTAL

        currentRow.gravity =
            Gravity.CENTER

        letterBoard.addView(
            currentRow
        )

        for (i in letters.indices) {

            val letter =
                letters[i]

            val button =
                Button(this)

            button.text =
                letter.toString()

            button.textSize =
                13f

            button.setTextColor(
                Color.WHITE
            )

            button.setBackgroundColor(
                Color.rgb(0, 100, 0)
            )

            button.tag =
                letter

            button.setOnClickListener {

                if (
                    roundFinished ||
                    phase != "normal"
                ) {
                    return@setOnClickListener
                }

                if (
                    !isMyPlayerActive()
                ) {

                    showStatus(
                        "You are a waiting player. Active players take the turns.",
                        Color.RED
                    )

                    return@setOnClickListener
                }

                if (
                    currentPlayerUid != myUid
                ) {

                    showStatus(
                        "It is not your turn.",
                        Color.RED
                    )

                    return@setOnClickListener
                }

                if (
                    guessedLetters.contains(
                        letter
                    )
                ) {

                    showStatus(
                        "Already guessed.",
                        Color.RED
                    )

                    return@setOnClickListener
                }

                processLetterGuess(
                    letter
                )
            }

            currentRow.addView(
                button,
                LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
            )

            if (
                (i + 1) % 6 == 0
            ) {

                currentRow =
                    LinearLayout(this)

                currentRow.orientation =
                    LinearLayout.HORIZONTAL

                currentRow.gravity =
                    Gravity.CENTER

                letterBoard.addView(
                    currentRow
                )
            }
        }
    }

    private fun startFirebaseListener() {

        val reference =
            gameReference()

        gameListener =
            object : ValueEventListener {

                override fun onDataChange(
                    snapshot: DataSnapshot
                ) {

                    if (leavingGame) {
                        return
                    }

                    if (!snapshot.exists()) {

                        stopTimers()

                        showStatus(
                            "Game no longer exists.",
                            Color.RED
                        )

                        return
                    }

                    readGameState(
                        snapshot
                    )
                }

                override fun onCancelled(
                    error: DatabaseError
                ) {

                    if (!leavingGame) {

                        showStatus(
                            "Firebase error: ${error.message}",
                            Color.RED
                        )
                    }
                }
            }

        reference.addValueEventListener(
            gameListener!!
        )
    }

    private fun readGameState(
        snapshot: DataSnapshot
    ) {

        val oldStatus =
            status

        val oldCurrentPlayer =
            currentPlayerUid

        val oldTurnEndsAt =
            turnEndsAt

        val oldPhase =
            phase

        val oldRoundNumber =
            roundNumber

        status =
            snapshot.child("status")
                .getValue(
                    String::class.java
                )
                ?: "waiting"

        wordSelection =
            snapshot.child("wordSelection")
                .getValue(
                    String::class.java
                )
                ?: wordSelection

        selectedCategory =
            snapshot.child("category")
                .getValue(
                    String::class.java
                )
                ?: selectedCategory

        secondsPerTurn =
            snapshot.child("secondsPerTurn")
                .getValue(
                    Int::class.java
                )
                ?: secondsPerTurn

        totalTurns =
            snapshot.child("totalTurns")
                .getValue(
                    Int::class.java
                )
                ?: totalTurns

        nextWordMaster =
            snapshot.child("nextWordMaster")
                .getValue(
                    String::class.java
                )
                ?: nextWordMaster

        roomCapacity =
            snapshot.child("roomCapacity")
                .getValue(
                    Int::class.java
                )
                ?: roomCapacity

        waitingPlayerGuesses =
            snapshot.child(
                "waitingPlayerGuesses"
            )
                .getValue(
                    Int::class.java
                )
                ?: waitingPlayerGuesses

        secretWord =
            snapshot.child("secretWord")
                .getValue(
                    String::class.java
                )
                ?: ""

        actualCategory =
            snapshot.child("actualCategory")
                .getValue(
                    String::class.java
                )
                ?: selectedCategory

        currentPlayerUid =
            snapshot.child(
                "currentPlayerUid"
            )
                .getValue(
                    String::class.java
                )
                ?: ""

        wordMasterUid =
            snapshot.child(
                "wordMasterUid"
            )
                .getValue(
                    String::class.java
                )
                ?: ""

        knownHostUid =
            snapshot.child("hostUid")
                .getValue(
                    String::class.java
                )
                ?: ""

        turnEndsAt =
            snapshot.child("turnEndsAt")
                .getValue(
                    Long::class.java
                )
                ?: 0L

        completedTurns =
            snapshot.child(
                "completedTurns"
            )
                .getValue(
                    Int::class.java
                )
                ?: 0

        roundNumber =
            snapshot.child("roundNumber")
                .getValue(
                    Int::class.java
                )
                ?: 0

        roundWinnerUid =
            snapshot.child(
                "roundWinnerUid"
            )
                .getValue(
                    String::class.java
                )
                ?: ""

        roundWinnerName =
            snapshot.child(
                "roundWinnerName"
            )
                .getValue(
                    String::class.java
                )
                ?: ""

        phase =
            snapshot.child("phase")
                .getValue(
                    String::class.java
                )
                ?: "normal"

        finalChallengeIndex =
            snapshot.child(
                "finalChallengeIndex"
            )
                .getValue(
                    Int::class.java
                )
                ?: 0

        readPlayers(
            snapshot
        )

        readGuessedLetters(
            snapshot
        )

        readScores(
            snapshot
        )

        readMissedTurns(
            snapshot
        )

        readEliminatedPlayers(
            snapshot
        )

        roundFinished =
            status == "roundFinished"

        /*
         * If Firebase tells us the turn changed, clear the
         * local whole-word attempt flag for the new turn.
         */
        if (
            oldCurrentPlayer != currentPlayerUid ||
            oldRoundNumber != roundNumber ||
            oldPhase != phase
        ) {

            wholeWordAttemptUsed =
                false
        }

        updateAllUI()

        if (
            status == "waiting" &&
            isHost(snapshot)
        ) {

            initializeFirstRound(
                snapshot
            )

            return
        }

        if (
            status == "roundFinished"
        ) {

            stopTimers()

            setWholeWordButtonEnabled(
                false
            )

            showRoundFinishedDialogIfNeeded()

            return
        }

        if (
            status == "playing"
        ) {

            val activePlayers =
                getActivePlayersFromSnapshot(
                    snapshot
                )

            if (
                activePlayers.size == 1
            ) {

                showSinglePlayerChoiceIfNeeded()
            }

            if (
                phase == "final"
            ) {

                handleFinalChallenge()

            } else {

                /*
                 * Do NOT blindly restart the timer here.
                 *
                 * This is the main fix for the visual refresh
                 * that happened after clicking letters.
                 */
                val stateChanged =
                    oldStatus != status ||
                        oldCurrentPlayer !=
                        currentPlayerUid ||
                        oldTurnEndsAt !=
                        turnEndsAt ||
                        oldPhase != phase ||
                        oldRoundNumber !=
                        roundNumber

                startSynchronizedTimer(
                    forceRestart = stateChanged
                )

                maybeStartNormalTurnUI()
            }
        }
    }

    private fun readPlayers(
        snapshot: DataSnapshot
    ) {

        val previousIds =
            lastKnownPlayerIds.toSet()

        val previousNames =
            lastKnownPlayerNames.toMap()

        playerNames.clear()
        playerOrder.clear()
        playerActiveStates.clear()

        val playersSnapshot =
            snapshot.child("players")

        val temp =
            mutableListOf<
                Triple<String, String, Long>
            >()

        for (
            child in playersSnapshot.children
        ) {

            val uid =
                child.key ?: continue

            val name =
                child.child("name")
                    .getValue(
                        String::class.java
                    )
                    ?: "Player"

            val joinedAt =
                child.child("joinedAt")
                    .getValue(
                        Long::class.java
                    )
                    ?: 0L

            /*
             * Older games may not have isActive.
             * In that case we treat the player as active.
             */
            val isActive =
                child.child("isActive")
                    .getValue(
                        Boolean::class.java
                    )
                    ?: true

            playerNames[uid] =
                name

            playerActiveStates[uid] =
                isActive

            temp.add(
                Triple(
                    uid,
                    name,
                    joinedAt
                )
            )
        }

        temp.sortBy {
            it.third
        }

        for (item in temp) {

            playerOrder.add(
                item.first
            )
        }

        maxPlayers =
            snapshot.child("maxPlayers")
                .getValue(
                    Int::class.java
                )
                ?: maxPlayers

        roomCapacity =
            snapshot.child("roomCapacity")
                .getValue(
                    Int::class.java
                )
                ?: roomCapacity

        waitingPlayerGuesses =
            snapshot.child(
                "waitingPlayerGuesses"
            )
                .getValue(
                    Int::class.java
                )
                ?: waitingPlayerGuesses

        val currentIds =
            playerOrder.toSet()

        if (
            playerChangeInitialized
        ) {

            val departed =
                previousIds.filter {
                    !currentIds.contains(it)
                }

            if (
                departed.isNotEmpty()
            ) {

                for (
                    uid in departed
                ) {

                    if (
                        !notifiedDepartures.contains(
                            uid
                        )
                    ) {

                        notifiedDepartures.add(
                            uid
                        )

                        val departedName =
                            previousNames[uid]
                                ?: "A player"

                        showPlayerLeftNotification(
                            departedName
                        )
                    }
                }
            }
        }

        lastKnownPlayerIds.clear()
        lastKnownPlayerIds.addAll(
            currentIds
        )

        lastKnownPlayerNames.clear()
        lastKnownPlayerNames.putAll(
            playerNames
        )

        playerChangeInitialized =
            true
    }

    private fun isPlayerActive(
        player: DataSnapshot
    ): Boolean {

        return player.child("isActive")
            .getValue(
                Boolean::class.java
            )
            ?: true
    }

    private fun isPlayerActive(
        data: MutableData,
        uid: String
    ): Boolean {

        return data.child("players")
            .child(uid)
            .child("isActive")
            .getValue(
                Boolean::class.java
            )
            ?: true
    }

    private fun isMyPlayerActive(): Boolean {

        if (myUid.isEmpty()) {
            return false
        }

        return playerActiveStates[
            myUid
        ] ?: false
    }

    private fun getActivePlayersFromSnapshot(
        snapshot: DataSnapshot
    ): List<String> {

        val active =
            mutableListOf<String>()

        for (
            child in snapshot.child(
                "players"
            ).children
        ) {

            val uid =
                child.key ?: continue

            if (
                isPlayerActive(child)
            ) {

                active.add(uid)
            }
        }

        active.sortBy { uid ->

            snapshot.child("players")
                .child(uid)
                .child("joinedAt")
                .getValue(
                    Long::class.java
                )
                ?: 0L
        }

        return active
    }

    private fun getActivePlayerIds(
        data: MutableData
    ): List<String> {

        val active =
            mutableListOf<String>()

        for (
            child in data.child(
                "players"
            ).children
        ) {

            val uid =
                child.key ?: continue

            if (
                child.child("isActive")
                    .getValue(
                        Boolean::class.java
                    )
                    ?: true
            ) {

                active.add(uid)
            }
        }

        return active
    }

    private fun showPlayerLeftNotification(
        departedName: String
    ) {

        if (leavingGame) {
            return
        }

        Toast.makeText(
            this,
            "$departedName left the game.",
            Toast.LENGTH_LONG
        ).show()

        val activeCount =
            getCurrentActivePlayerCount()

        if (
            activeCount == 1
        ) {

            showStatus(
                "$departedName left. There is now only one active player.",
                Color.rgb(0, 70, 140)
            )

        } else {

            showStatus(
                "$departedName left the game. $activeCount active players remain.",
                Color.rgb(0, 70, 140)
            )
        }
    }

    private fun getCurrentActivePlayerCount():
        Int {

        return playerActiveStates.values.count {
            it
        }
    }

    private fun showSinglePlayerChoiceIfNeeded() {

        if (
            singlePlayerDialogShown ||
            roundFinished ||
            myUid.isEmpty()
        ) {
            return
        }

        val activeCount =
            getCurrentActivePlayerCount()

        if (
            activeCount != 1
        ) {
            return
        }

        if (
            !isMyPlayerActive()
        ) {
            return
        }

        if (
            currentPlayerUid != myUid
        ) {
            return
        }

        singlePlayerDialogShown =
            true

        AlertDialog.Builder(this)
            .setTitle(
                "You are now the only active player"
            )
            .setMessage(
                "The other active player left the game.\n\n" +
                    "You can continue guessing by yourself, " +
                    "or leave the game."
            )
            .setPositiveButton(
                "Continue Playing"
            ) { dialog, _ ->

                dialog.dismiss()

                showStatus(
                    "You are now playing alone. Continue guessing!",
                    Color.rgb(0, 100, 0)
                )
            }
            .setNegativeButton(
                "Leave Game"
            ) { _, _ ->

                leaveGame()
            }
            .setCancelable(false)
            .show()
    }

    private fun readGuessedLetters(
        snapshot: DataSnapshot
    ) {

        guessedLetters.clear()

        val guessed =
            snapshot.child(
                "guessedLetters"
            )

        for (
            child in guessed.children
        ) {

            val value =
                child.getValue(
                    Boolean::class.java
                ) ?: false

            if (value) {

                val letter =
                    child.key?.firstOrNull()

                if (letter != null) {
                    guessedLetters.add(letter)
                }
            }
        }
    }

    private fun readScores(
        snapshot: DataSnapshot
    ) {

        scores.clear()

        val scoreSnapshot =
            snapshot.child("scores")

        for (
            child in scoreSnapshot.children
        ) {

            val uid =
                child.key ?: continue

            scores[uid] =
                child.getValue(
                    Int::class.java
                ) ?: 0
        }
    }

    private fun readMissedTurns(
        snapshot: DataSnapshot
    ) {

        missedTurns.clear()

        val missSnapshot =
            snapshot.child(
                "missedTurns"
            )

        for (
            child in missSnapshot.children
        ) {

            val uid =
                child.key ?: continue

            missedTurns[uid] =
                child.getValue(
                    Int::class.java
                ) ?: 0
        }
    }

    private fun readEliminatedPlayers(
        snapshot: DataSnapshot
    ) {

        eliminatedPlayers.clear()

        val eliminated =
            snapshot.child(
                "eliminatedPlayers"
            )

        for (
            child in eliminated.children
        ) {

            val value =
                child.getValue(
                    Boolean::class.java
                ) ?: false

            if (value) {

                child.key?.let {
                    eliminatedPlayers.add(it)
                }
            }
        }
    }

    private fun isHost(
        snapshot: DataSnapshot
    ): Boolean {

        val hostUid =
            snapshot.child("hostUid")
                .getValue(
                    String::class.java
                )

        return hostUid == myUid
    }

    private fun initializeFirstRound(
        snapshot: DataSnapshot
    ) {

        val currentStatus =
            snapshot.child("status")
                .getValue(
                    String::class.java
                )

        if (
            currentStatus != "waiting"
        ) {
            return
        }

        val activePlayers =
            getActivePlayersFromSnapshot(
                snapshot
            )

        if (
            activePlayers.size < 2
        ) {

            showStatus(
                "Waiting for at least 2 active players.",
                Color.DKGRAY
            )

            return
        }

        val selected =
            chooseOnlineWord()

        val firstPlayerUid =
            chooseFirstPlayer(
                snapshot,
                activePlayers
            )

        if (
            firstPlayerUid.isEmpty()
        ) {
            return
        }

        val updates =
            hashMapOf<String, Any>(
                "status" to
                    "playing",

                "roundNumber" to
                    1,

                "phase" to
                    "normal",

                "secretWord" to
                    selected.first,

                "actualCategory" to
                    selected.second,

                "currentPlayerUid" to
                    firstPlayerUid,

                "completedTurns" to
                    0,

                "roundWinnerUid" to
                    "",

                "roundWinnerName" to
                    "",

                "wordMasterUid" to
                    getInitialWordMasterUid(
                        snapshot
                    ),

                "turnEndsAt" to
                    getNewTurnEndTime(),

                "guessedLetters" to
                    emptyMap<String, Any>(),

                "scores" to
                    createInitialScores(
                        activePlayers
                    ),

                "missedTurns" to
                    createInitialMisses(
                        activePlayers
                    ),

                "eliminatedPlayers" to
                    emptyMap<String, Any>(),

                "finalChallengePlayers" to
                    emptyList<String>(),

                "finalChallengeIndex" to
                    0
            )

        gameReference()
            .updateChildren(
                updates
            )
    }

    private fun chooseOnlineWord():
        Pair<String, String> {

        if (
            wordSelection != "Random Word"
        ) {

            val word =
                manualWord
                    ?.trim()
                    ?.uppercase()
                    ?: "APPLE"

            val category =
                if (
                    selectedCategory ==
                    "Random"
                ) {
                    "Random"
                } else {
                    selectedCategory
                }

            return Pair(
                word,
                category
            )
        }

        val categoryNames =
            if (
                selectedCategory ==
                "Random"
            ) {

                WordBank.categories.keys
                    .toList()

            } else {

                listOf(
                    selectedCategory
                )
            }

        if (
            categoryNames.isEmpty()
        ) {

            return Pair(
                "APPLE",
                "Random"
            )
        }

        val category =
            categoryNames[
                Random.nextInt(
                    categoryNames.size
                )
            ]

        val words =
            WordBank.categories[
                category
            ]
                ?: listOf(
                    "APPLE"
                )

        val word =
            words[
                Random.nextInt(
                    words.size
                )
            ]

        return Pair(
            word,
            category
        )
    }

    private fun chooseFirstPlayer(
        snapshot: DataSnapshot,
        activePlayers: List<String>
    ): String {

        if (
            wordSelection !=
            "Random Word"
        ) {

            val master =
                activePlayers.firstOrNull {
                    snapshot.child("players")
                        .child(it)
                        .child("isHost")
                        .getValue(
                            Boolean::class.java
                        ) == true
                }

            if (
                master != null
            ) {

                val nonMaster =
                    activePlayers.firstOrNull {
                        it != master
                    }

                if (
                    nonMaster != null
                ) {
                    return nonMaster
                }
            }
        }

        return activePlayers.firstOrNull()
            ?: ""
    }

    private fun getInitialWordMasterUid(
        snapshot: DataSnapshot
    ): String {

        if (
            wordSelection ==
            "Random Word"
        ) {
            return ""
        }

        return snapshot.child("hostUid")
            .getValue(
                String::class.java
            )
            ?: ""
    }

    private fun createInitialScores(
        activePlayers: List<String>
    ): Map<String, Any> {

        val map =
            mutableMapOf<String, Any>()

        for (
            uid in activePlayers
        ) {

            map[uid] = 0
        }

        return map
    }

    private fun createInitialMisses(
        activePlayers: List<String>
    ): Map<String, Any> {

        val map =
            mutableMapOf<String, Any>()

        for (
            uid in activePlayers
        ) {

            map[uid] = 0
        }

        return map
    }

    private fun getNewTurnEndTime(): Long {

        if (
            secondsPerTurn <= 0
        ) {
            return 0L
        }

        return System.currentTimeMillis() +
            secondsPerTurn * 1000L
    }

    private fun updateAllUI() {

        val categoryDisplay =
            if (
                selectedCategory ==
                "Random" &&
                wordSelection ==
                "Random Word"
            ) {

                "Random: $actualCategory"

            } else {

                actualCategory
            }

        categoryText.text =
            "CATEGORY: $categoryDisplay"

        updatePlayerList()
        updateWordMasterDisplay()
        updateWordDisplay()
        updateLetterButtons()
        updateTurnDisplay()

        if (
            phase == "final"
        ) {

            wholeWordButton.isEnabled =
                false
        }
    }

    private fun updateWordMasterDisplay() {

        if (
            wordSelection ==
            "Random Word"
        ) {

            wordMasterText.text =
                "Random Word — no Word Master"

            wordMasterText.setTextColor(
                Color.rgb(0, 70, 140)
            )

            return
        }

        if (
            wordMasterUid.isEmpty()
        ) {

            wordMasterText.text =
                "Word Master: Waiting..."

            wordMasterText.setTextColor(
                Color.DKGRAY
            )

            return
        }

        val name =
            playerNames[
                wordMasterUid
            ] ?: "Player"

        wordMasterText.text =
            "WORD MASTER: $name"

        wordMasterText.setTextColor(
            Color.rgb(0, 70, 140)
        )
    }

    private fun updatePlayerList() {

        val builder =
            StringBuilder()

        val activeCount =
            getLocalActivePlayerCount()

        val waitingCount =
            (
                playerOrder.size -
                    activeCount
            ).coerceAtLeast(0)

        builder.append(
            "Active: $activeCount / $maxPlayers"
        )

        builder.append(
            "\nWaiting: $waitingCount"
        )

        builder.append(
            "\n\n"
        )

        for (
            uid in playerOrder
        ) {

            val name =
                playerNames[uid]
                    ?: "Player"

            val active =
                isPlayerCurrentlyActive(
                    uid
                )

            if (active) {

                builder.append(
                    "🟢 "
                )

            } else {

                builder.append(
                    "🟡 "
                )
            }

            builder.append(
                name
            )

            builder.append(
                " — Score: "
            )

            builder.append(
                scores[uid] ?: 0
            )

            if (!active) {

                builder.append(
                    " — WAITING"
                )

            } else if (
                eliminatedPlayers.contains(
                    uid
                )
            ) {

                builder.append(
                    " — ELIMINATED"
                )

            } else if (
                uid == currentPlayerUid
            ) {

                builder.append(
                    " — CURRENT TURN"
                )
            }

            if (
                uid == wordMasterUid
            ) {

                builder.append(
                    " — WORD MASTER"
                )
            }

            if (
                active &&
                activeCount >= 3
            ) {

                builder.append(
                    " — Misses: "
                )

                builder.append(
                    missedTurns[uid] ?: 0
                )

                builder.append(
                    "/3"
                )
            }

            builder.append(
                "\n"
            )
        }

        playersText.text =
            builder.toString().trim()

        playersText.setTypeface(
            null,
            Typeface.BOLD
        )

        playersText.setTextColor(
            Color.DKGRAY
        )

        if (
            currentPlayerUid ==
            myUid &&
            !roundFinished
        ) {

            turnText.setTextColor(
                Color.rgb(0, 100, 0)
            )
        }
    }

    private fun getLocalActivePlayerCount():
        Int {

        return playerActiveStates.values.count {
            it
        }
    }

    private fun isPlayerCurrentlyActive(
        uid: String
    ): Boolean {

        return playerActiveStates[
            uid
        ] ?: false
    }

    private fun updateWordDisplay() {

        if (
            secretWord.isEmpty()
        ) {

            wordText.text =
                "Waiting for word..."

            return
        }

        val builder =
            StringBuilder()

        for (
            letter in secretWord
        ) {

            if (
                letter == ' '
            ) {

                builder.append(
                    "   "
                )

            } else if (
                guessedLetters.contains(
                    letter
                )
            ) {

                builder.append(
                    letter
                )

                builder.append(
                    " "
                )

            } else {

                builder.append(
                    "_ "
                )
            }
        }

        wordText.text =
            builder.toString().trim()
    }

    private fun updateLetterButtons() {

        for (
            i in 0 until
                letterBoard.childCount
        ) {

            val row =
                letterBoard.getChildAt(i)
                    as? LinearLayout
                    ?: continue

            for (
                j in 0 until
                    row.childCount
            ) {

                val button =
                    row.getChildAt(j)
                        as? Button
                        ?: continue

                val letter =
                    button.tag as? Char
                        ?: continue

                if (
                    guessedLetters.contains(
                        letter
                    )
                ) {

                    button.setBackgroundColor(
                        Color.RED
                    )

                    button.isEnabled =
                        false

                } else {

                    button.setBackgroundColor(
                        Color.rgb(
                            0,
                            100,
                            0
                        )
                    )

                    button.isEnabled =
                        !roundFinished &&
                            phase == "normal" &&
                            currentPlayerUid ==
                            myUid &&
                            isMyPlayerActive()
                }
            }
        }
    }

    private fun updateTurnDisplay() {

        if (
            currentPlayerUid.isEmpty()
        ) {

            turnText.text =
                "Waiting..."

            return
        }

        val currentName =
            playerNames[
                currentPlayerUid
            ] ?: "Player"

        if (
            phase == "final"
        ) {

            turnText.text =
                "FINAL CHALLENGE: $currentName"

            turnText.setTextColor(
                Color.rgb(
                    0,
                    70,
                    140
                )
            )

        } else {

            turnText.text =
                if (
                    currentPlayerUid ==
                    myUid
                ) {

                    "YOUR TURN"

                } else {

                    "$currentName'S TURN"
                }

            turnText.setTextColor(
                Color.rgb(
                    0,
                    100,
                    0
                )
            )
        }
    }

    private fun processLetterGuess(
        letter: Char
    ) {

        /*
         * Disable the clicked letter immediately.
         * Firebase remains authoritative, so this is only
         * a visual protection against double tapping.
         */
        setLetterEnabled(
            letter,
            false
        )

        gameReference().runTransaction(
            object : Transaction.Handler {

                override fun doTransaction(
                    currentData: MutableData
                ): Transaction.Result {

                    val currentUid =
                        currentData
                            .child(
                                "currentPlayerUid"
                            )
                            .getValue(
                                String::class.java
                            )
                            ?: ""

                    if (
                        currentUid != myUid
                    ) {

                        return Transaction.abort()
                    }

                    if (
                        !isPlayerActive(
                            currentData,
                            myUid
                        )
                    ) {

                        return Transaction.abort()
                    }

                    val currentStatus =
                        currentData
                            .child("status")
                            .getValue(
                                String::class.java
                            )
                            ?: "waiting"

                    if (
                        currentStatus !=
                        "playing"
                    ) {

                        return Transaction.abort()
                    }

                    val currentPhase =
                        currentData
                            .child("phase")
                            .getValue(
                                String::class.java
                            )
                            ?: "normal"

                    if (
                        currentPhase !=
                        "normal"
                    ) {

                        return Transaction.abort()
                    }

                    /*
                     * Reject a letter that arrives after the
                     * Firebase turn deadline.
                     */
                    val firebaseTurnEndsAt =
                        currentData
                            .child("turnEndsAt")
                            .getValue(
                                Long::class.java
                            )
                            ?: 0L

                    if (
                        secondsPerTurn > 0 &&
                        firebaseTurnEndsAt > 0 &&
                        firebaseTurnEndsAt <=
                        System.currentTimeMillis()
                    ) {

                        return Transaction.abort()
                    }

                    val guessed =
                        currentData.child(
                            "guessedLetters"
                        )

                    if (
                        guessed
                            .child(
                                letter.toString()
                            )
                            .value != null
                    ) {

                        return Transaction.abort()
                    }

                    guessed
                        .child(
                            letter.toString()
                        )
                        .value = true

                    val word =
                        currentData
                            .child("secretWord")
                            .getValue(
                                String::class.java
                            )
                            ?: ""

                    if (
                        word.contains(letter)
                    ) {

                        val occurrences =
                            word.count {
                                it == letter
                            }

                        val score =
                            currentData
                                .child("scores")
                                .child(myUid)
                                .getValue(
                                    Int::class.java
                                )
                                ?: 0

                        currentData
                            .child("scores")
                            .child(myUid)
                            .value =
                            score + occurrences

                        var complete =
                            true

                        for (
                            c in word
                        ) {

                            if (
                                c != ' ' &&
                                guessed
                                    .child(
                                        c.toString()
                                    )
                                    .value == null
                            ) {

                                complete =
                                    false

                                break
                            }
                        }

                        if (complete) {

                            currentData
                                .child("status")
                                .value =
                                "roundFinished"

                            currentData
                                .child(
                                    "roundWinnerUid"
                                )
                                .value =
                                myUid

                            currentData
                                .child(
                                    "roundWinnerName"
                                )
                                .value =
                                playerNames[
                                    myUid
                                ] ?: "Player"

                            currentData
                                .child(
                                    "turnEndsAt"
                                )
                                .value =
                                0L

                            return Transaction.success(
                                currentData
                            )
                        }

                        currentData
                            .child(
                                "turnEndsAt"
                            )
                            .value =
                            if (
                                secondsPerTurn <= 0
                            ) {

                                0L

                            } else {

                                System.currentTimeMillis() +
                                    secondsPerTurn *
                                    1000L
                            }

                    } else {

                        advanceTurnInsideTransaction(
                            currentData
                        )
                    }

                    return Transaction.success(
                        currentData
                    )
                }

                override fun onComplete(
                    error: DatabaseError?,
                    committed: Boolean,
                    currentData: DataSnapshot?
                ) {

                    if (error != null) {

                        showStatus(
                            "Could not submit letter.",
                            Color.RED
                        )

                        updateLetterButtons()

                        return
                    }

                    if (!committed) {

                        showStatus(
                            "That move is no longer available.",
                            Color.RED
                        )

                        updateLetterButtons()

                        return
                    }

                    if (
                        secretWord.contains(
                            letter
                        )
                    ) {

                        val occurrences =
                            secretWord.count {
                                it == letter
                            }

                        wholeWordAttemptUsed =
                            false

                        showStatus(
                            "Correct! You earned $occurrences point(s). Timer reset.",
                            Color.rgb(
                                0,
                                100,
                                0
                            )
                        )

                    } else {

                        wholeWordAttemptUsed =
                            false

                        showStatus(
                            "Wrong letter. Next player's turn.",
                            Color.RED
                        )
                    }

                    /*
                     * Do not manually restart the timer here.
                     *
                     * Firebase will deliver the authoritative
                     * new turnEndsAt/currentPlayerUid and the
                     * listener will restart it exactly once.
                     */
                }
            }
        )
    }

    private fun setLetterEnabled(
        letter: Char,
        enabled: Boolean
    ) {

        for (
            i in 0 until
                letterBoard.childCount
        ) {

            val row =
                letterBoard.getChildAt(i)
                    as? LinearLayout
                    ?: continue

            for (
                j in 0 until
                    row.childCount
            ) {

                val button =
                    row.getChildAt(j)
                        as? Button
                        ?: continue

                val buttonLetter =
                    button.tag as? Char
                        ?: continue

                if (
                    buttonLetter == letter
                ) {

                    button.isEnabled =
                        enabled

                    return
                }
            }
        }
    }

    private fun advanceTurnInsideTransaction(
        data: MutableData
    ) {

        val players =
            getActivePlayerIds(
                data
            )

        if (
            players.isEmpty()
        ) {
            return
        }

        val currentUid =
            data.child(
                "currentPlayerUid"
            )
                .getValue(
                    String::class.java
                )
                ?: ""

        val eliminated =
            data.child(
                "eliminatedPlayers"
            )

        val masterUid =
            data.child(
                "wordMasterUid"
            )
                .getValue(
                    String::class.java
                )
                ?: ""

        var index =
            players.indexOf(
                currentUid
            )

        if (
            index < 0
        ) {
            index = 0
        }

        var nextUid =
            ""

        for (
            step in 1..players.size
        ) {

            val nextIndex =
                (
                    index + step
                ) % players.size

            val candidate =
                players[nextIndex]

            if (
                eliminated
                    .child(candidate)
                    .getValue(
                        Boolean::class.java
                    ) != true &&
                candidate != masterUid
            ) {

                nextUid =
                    candidate

                break
            }
        }

        if (
            nextUid.isEmpty()
        ) {

            data.child(
                "status"
            ).value =
                "roundFinished"

            data.child(
                "turnEndsAt"
            ).value =
                0L

            return
        }

        data.child(
            "currentPlayerUid"
        ).value =
            nextUid

        val completed =
            data.child(
                "completedTurns"
            )
                .getValue(
                    Int::class.java
                )
                ?: 0

        val newCompleted =
            completed + 1

        data.child(
            "completedTurns"
        ).value =
            newCompleted

        val configuredTotal =
            data.child(
                "totalTurns"
            )
                .getValue(
                    Int::class.java
                )
                ?: 0

        if (
            configuredTotal > 0 &&
            newCompleted >= configuredTotal
        ) {

            startFinalChallengeInsideTransaction(
                data,
                players
            )

            return
        }

        data.child(
            "turnEndsAt"
        ).value =
            if (
                secondsPerTurn <= 0
            ) {

                0L

            } else {

                System.currentTimeMillis() +
                    secondsPerTurn *
                    1000L
            }
    }

    private fun startFinalChallengeInsideTransaction(
        data: MutableData,
        players: List<String>
    ) {

        val order =
            mutableListOf<String>()

        val eliminated =
            data.child(
                "eliminatedPlayers"
            )

        val masterUid =
            data.child(
                "wordMasterUid"
            )
                .getValue(
                    String::class.java
                )
                ?: ""

        for (
            uid in players
        ) {

            if (
                eliminated
                    .child(uid)
                    .getValue(
                        Boolean::class.java
                    ) == true
            ) {
                continue
            }

            if (
                uid == masterUid
            ) {
                continue
            }

            order.add(uid)
        }

        data.child(
            "phase"
        ).value =
            "final"

        data.child(
            "finalChallengeIndex"
        ).value =
            0

        data.child(
            "finalChallengePlayers"
        ).value =
            order

        if (
            order.isEmpty()
        ) {

            data.child(
                "status"
            ).value =
                "roundFinished"

            data.child(
                "turnEndsAt"
            ).value =
                0L

            return
        }

        data.child(
            "currentPlayerUid"
        ).value =
            order.first()

        data.child(
            "turnEndsAt"
        ).value =
            System.currentTimeMillis() +
                20000L
    }

    private fun handleMissedTurn() {

        if (
            roundFinished
        ) {
            return
        }

        if (
            currentPlayerUid != myUid
        ) {
            return
        }

        gameReference().runTransaction(
            object : Transaction.Handler {

                override fun doTransaction(
                    currentData: MutableData
                ): Transaction.Result {

                    val currentUid =
                        currentData
                            .child(
                                "currentPlayerUid"
                            )
                            .getValue(
                                String::class.java
                            )
                            ?: ""

                    if (
                        currentUid != myUid
                    ) {
                        return Transaction.abort()
                    }

                    if (
                        !isPlayerActive(
                            currentData,
                            myUid
                        )
                    ) {
                        return Transaction.abort()
                    }

                    val currentPhase =
                        currentData
                            .child("phase")
                            .getValue(
                                String::class.java
                            )
                            ?: "normal"

                    if (
                        currentPhase != "normal"
                    ) {
                        return Transaction.abort()
                    }

                    val firebaseTurnEndsAt =
                        currentData
                            .child("turnEndsAt")
                            .getValue(
                                Long::class.java
                            )
                            ?: 0L

                    /*
                     * Another action may have already reset
                     * the timer. Do not process this old timer.
                     */
                    if (
                        secondsPerTurn > 0 &&
                        firebaseTurnEndsAt > 0 &&
                        firebaseTurnEndsAt >
                        System.currentTimeMillis()
                    ) {

                        return Transaction.abort()
                    }

                    val misses =
                        currentData
                            .child(
                                "missedTurns"
                            )
                            .child(myUid)
                            .getValue(
                                Int::class.java
                            )
                            ?: 0

                    val newMisses =
                        misses + 1

                    currentData
                        .child(
                            "missedTurns"
                        )
                        .child(myUid)
                        .value =
                        newMisses

                    val activePlayers =
                        getActivePlayerIds(
                            currentData
                        )

                    if (
                        activePlayers.size >= 3 &&
                        newMisses >= 3
                    ) {

                        currentData
                            .child(
                                "eliminatedPlayers"
                            )
                            .child(myUid)
                            .value =
                            true
                    }

                    advanceTurnInsideTransaction(
                        currentData
                    )

                    return Transaction.success(
                        currentData
                    )
                }

                override fun onComplete(
                    error: DatabaseError?,
                    committed: Boolean,
                    currentData: DataSnapshot?
                ) {

                    if (committed) {

                        wholeWordAttemptUsed =
                            false

                        showStatus(
                            "Time is up.",
                            Color.RED
                        )
                    }
                }
            }
        )
    }

    private fun startSynchronizedTimer(
        forceRestart: Boolean = false
    ) {

        if (
            roundFinished ||
            phase != "normal" ||
            status != "playing"
        ) {

            if (
                timer != null
            ) {
                timer?.cancel()
                timer = null
            }

            timerStateUid = ""
            timerStateEndsAt = -1L
            timerStatePhase = ""
            timerStateRound = -1

            return
        }

        /*
         * The local active state is now authoritative from the
         * latest Firebase snapshot.
         */
        if (
            !isMyPlayerActive()
        ) {

            setWholeWordButtonEnabled(
                false
            )

        } else if (
            currentPlayerUid != myUid
        ) {

            setWholeWordButtonEnabled(
                false
            )

        } else {

            setWholeWordButtonEnabled(
                !wholeWordAttemptUsed
            )
        }

        val sameTimerState =
            timerStateUid ==
                currentPlayerUid &&
                timerStateEndsAt ==
                turnEndsAt &&
                timerStatePhase ==
                phase &&
                timerStateRound ==
                roundNumber

        if (
            !forceRestart &&
            sameTimerState &&
            timer != null
        ) {

            return
        }

        timer?.cancel()
        timer = null

        timerStateUid =
            currentPlayerUid

        timerStateEndsAt =
            turnEndsAt

        timerStatePhase =
            phase

        timerStateRound =
            roundNumber

        if (
            secondsPerTurn <= 0
        ) {

            timerText.text =
                "Time: Unlimited"

            timerText.setTextColor(
                Color.rgb(
                    0,
                    100,
                    0
                )
            )

            return
        }

        if (
            turnEndsAt <= 0
        ) {

            timerText.text =
                "Time: --"

            return
        }

        val timerUid =
            currentPlayerUid

        val timerEndsAt =
            turnEndsAt

        val timerRound =
            roundNumber

        val remaining =
            (
                timerEndsAt -
                    System.currentTimeMillis()
            ).coerceAtLeast(
                0L
            )

        if (
            remaining <= 0L
        ) {

            if (
                timerUid == myUid &&
                isMyPlayerActive() &&
                currentPlayerUid ==
                timerUid &&
                roundNumber ==
                timerRound
            ) {

                handleMissedTurn()
            }

            return
        }

        timer =
            object : CountDownTimer(
                remaining,
                250L
            ) {

                override fun onTick(
                    millisUntilFinished: Long
                ) {

                    /*
                     * If Firebase already changed the turn,
                     * this old timer should no longer update
                     * the screen.
                     */
                    if (
                        currentPlayerUid !=
                        timerUid ||
                        turnEndsAt !=
                        timerEndsAt ||
                        roundNumber !=
                        timerRound ||
                        phase !=
                        "normal" ||
                        roundFinished
                    ) {

                        cancel()

                        return
                    }

                    val seconds =
                        (
                            (
                                millisUntilFinished +
                                    999L
                            ) / 1000L
                        ).toInt()

                    timerText.text =
                        "Time: $seconds"

                    timerText.setTextColor(
                        if (
                            seconds <= 3
                        ) {
                            Color.RED
                        } else {
                            Color.BLACK
                        }
                    )
                }

                override fun onFinish() {

                    timerText.text =
                        "Time: 0"

                    timerText.setTextColor(
                        Color.RED
                    )

                    /*
                     * Only the phone whose turn expired can
                     * submit the timeout.
                     *
                     * Also verify the state has not changed
                     * since this timer was created.
                     */
                    if (
                        currentPlayerUid ==
                        timerUid &&
                        turnEndsAt ==
                        timerEndsAt &&
                        roundNumber ==
                        timerRound &&
                        phase ==
                        "normal" &&
                        !roundFinished &&
                        timerUid == myUid &&
                        isMyPlayerActive()
                    ) {

                        handleMissedTurn()
                    }
                }
            }.start()
    }

    private fun maybeStartNormalTurnUI() {

        if (
            !isMyPlayerActive()
        ) {

            statusText.text =
                "You are waiting for an active player to finish the round."

            statusText.setTextColor(
                Color.rgb(
                    0,
                    70,
                    140
                )
            )

            setWholeWordButtonEnabled(
                false
            )

            return
        }

        if (
            currentPlayerUid ==
            myUid
        ) {

            statusText.text =
                "Choose a letter or guess the whole word."

            statusText.setTextColor(
                Color.rgb(
                    0,
                    100,
                    0
                )
            )

        } else {

            statusText.text =
                "Waiting for ${
                    playerNames[
                        currentPlayerUid
                    ] ?: "player"
                }..."

            statusText.setTextColor(
                Color.DKGRAY
            )
        }

        setWholeWordButtonEnabled(
            currentPlayerUid ==
                myUid &&
                !wholeWordAttemptUsed &&
                phase ==
                "normal" &&
                !roundFinished
        )
    }

    private fun setWholeWordButtonEnabled(
        enabled: Boolean
    ) {

        wholeWordButton.isEnabled =
            enabled

        if (enabled) {

            wholeWordButton.setTextColor(
                Color.WHITE
            )

            wholeWordButton.setBackgroundColor(
                Color.rgb(
                    0,
                    70,
                    140
                )
            )

        } else {

            wholeWordButton.setTextColor(
                Color.DKGRAY
            )

            wholeWordButton.setBackgroundColor(
                Color.LTGRAY
            )
        }
    }

    private fun showStatus(
        message: String,
        color: Int
    ) {

        statusText.text =
            message

        statusText.setTextColor(
            color
        )

        statusText.setTypeface(
            null,
            Typeface.BOLD
        )
    }

    private fun showWholeWordDialog() {

        wholeWordAttemptUsed =
            true

        setWholeWordButtonEnabled(
            false
        )

        timer?.cancel()
        timer = null

        val input =
            EditText(this)

        input.hint =
            "Enter your word"

        input.setSingleLine(
            true
        )

        input.textSize =
            18f

        val timerDisplay =
            TextView(this)

        timerDisplay.text =
            "Time: 20"

        timerDisplay.textSize =
            22f

        timerDisplay.gravity =
            Gravity.CENTER

        timerDisplay.setTypeface(
            null,
            Typeface.BOLD
        )

        val dialogLayout =
            LinearLayout(this)

        dialogLayout.orientation =
            LinearLayout.VERTICAL

        dialogLayout.setPadding(
            30,
            10,
            30,
            5
        )

        dialogLayout.addView(
            input
        )

        dialogLayout.addView(
            timerDisplay
        )

        val dialog =
            AlertDialog.Builder(this)
                .setTitle(
                    "Guess Whole Word"
                )
                .setView(
                    dialogLayout
                )
                .setNegativeButton(
                    "Cancel",
                    null
                )
                .setPositiveButton(
                    "Guess",
                    null
                )
                .create()

        dialog.setOnShowListener {

            val cancelButton =
                dialog.getButton(
                    AlertDialog.BUTTON_NEGATIVE
                )

            cancelButton.setOnClickListener {

                wholeWordTimer?.cancel()

                dialog.dismiss()

                resumeNormalTimer()
            }

            val guessButton =
                dialog.getButton(
                    AlertDialog.BUTTON_POSITIVE
                )

            guessButton.setOnClickListener {

                val guess =
                    input.text
                        .toString()
                        .trim()
                        .uppercase()

                if (
                    guess.isEmpty()
                ) {

                    input.error =
                        "Enter a word."

                    return@setOnClickListener
                }

                wholeWordTimer?.cancel()

                dialog.dismiss()

                submitWholeWordGuess(
                    guess
                )
            }

            wholeWordTimer =
                object : CountDownTimer(
                    20000L,
                    250L
                ) {

                    override fun onTick(
                        millisUntilFinished: Long
                    ) {

                        val seconds =
                            (
                                (
                                    millisUntilFinished +
                                        999L
                                ) / 1000L
                            ).toInt()

                        timerDisplay.text =
                            "Time: $seconds"

                        timerDisplay.setTextColor(
                            if (
                                seconds <= 3
                            ) {
                                Color.RED
                            } else {
                                Color.BLACK
                            }
                        )
                    }

                    override fun onFinish() {

                        timerDisplay.text =
                            "Time: 0"

                        dialog.dismiss()

                        submitWholeWordGuess(
                            ""
                        )
                    }
                }.start()
        }

        dialog.setOnCancelListener {

            wholeWordTimer?.cancel()

            resumeNormalTimer()
        }

        dialog.show()
    }

    private fun resumeNormalTimer() {

        if (
            roundFinished ||
            phase != "normal"
        ) {
            return
        }

        val remaining =
            if (
                secondsPerTurn <= 0
            ) {

                0

            } else {

                (
                    (
                        turnEndsAt -
                            System.currentTimeMillis()
                    ) / 1000L
                ).toInt()
                    .coerceAtLeast(0)
            }

        if (
            secondsPerTurn > 0 &&
            remaining <= 0
        ) {

            handleMissedTurn()

            return
        }

        /*
         * Force restart because the original normal timer was
         * deliberately cancelled while the dialog was open.
         */
        startSynchronizedTimer(
            forceRestart = true
        )
    }

    private fun submitWholeWordGuess(
        guess: String
    ) {

        gameReference().runTransaction(
            object : Transaction.Handler {

                override fun doTransaction(
                    currentData: MutableData
                ): Transaction.Result {

                    val currentUid =
                        currentData
                            .child(
                                "currentPlayerUid"
                            )
                            .getValue(
                                String::class.java
                            )
                            ?: ""

                    if (
                        currentUid != myUid
                    ) {

                        return Transaction.abort()
                    }

                    if (
                        !isPlayerActive(
                            currentData,
                            myUid
                        )
                    ) {

                        return Transaction.abort()
                    }

                    if (
                        currentData
                            .child("status")
                            .getValue(
                                String::class.java
                            ) != "playing"
                    ) {

                        return Transaction.abort()
                    }

                    if (
                        currentData
                            .child("phase")
                            .getValue(
                                String::class.java
                            ) != "normal"
                    ) {

                        return Transaction.abort()
                    }

                    val firebaseTurnEndsAt =
                        currentData
                            .child("turnEndsAt")
                            .getValue(
                                Long::class.java
                            )
                            ?: 0L

                    if (
                        secondsPerTurn > 0 &&
                        firebaseTurnEndsAt > 0 &&
                        firebaseTurnEndsAt <=
                        System.currentTimeMillis()
                    ) {

                        return Transaction.abort()
                    }

                    val word =
                        currentData
                            .child("secretWord")
                            .getValue(
                                String::class.java
                            )
                            ?: ""

                    if (
                        guess.isNotEmpty() &&
                        guess ==
                        word.trim().uppercase()
                    ) {

                        currentData
                            .child("status")
                            .value =
                            "roundFinished"

                        currentData
                            .child(
                                "roundWinnerUid"
                            )
                            .value =
                            myUid

                        currentData
                            .child(
                                "roundWinnerName"
                            )
                            .value =
                            playerNames[
                                myUid
                            ] ?: "Player"

                        currentData
                            .child(
                                "turnEndsAt"
                            )
                            .value =
                            0L

                    } else {

                        advanceTurnInsideTransaction(
                            currentData
                        )
                    }

                    return Transaction.success(
                        currentData
                    )
                }

                override fun onComplete(
                    error: DatabaseError?,
                    committed: Boolean,
                    currentData: DataSnapshot?
                ) {

                    if (!committed) {

                        wholeWordAttemptUsed =
                            false

                        showStatus(
                            "That guess could not be submitted. The turn may have changed.",
                            Color.RED
                        )

                        return
                    }

                    if (
                        guess.isEmpty()
                    ) {

                        showStatus(
                            "Whole-word time is up.",
                            Color.RED
                        )

                    } else {

                        val winner =
                            currentData
                                ?.child(
                                    "roundWinnerUid"
                                )
                                ?.getValue(
                                    String::class.java
                                )

                        if (
                            winner == myUid
                        ) {

                            showStatus(
                                "Correct! You win the round!",
                                Color.rgb(
                                    0,
                                    100,
                                    0
                                )
                            )

                        } else {

                            wholeWordAttemptUsed =
                                false

                            showStatus(
                                "Wrong whole-word guess.",
                                Color.RED
                            )
                        }
                    }
                }
            }
        )
    }

    private fun handleFinalChallenge() {

        timer?.cancel()
        timer = null

        setWholeWordButtonEnabled(
            false
        )

        val finalPlayers =
            getFinalPlayers()

        if (
            finalPlayers.isEmpty()
        ) {
            return
        }

        if (
            finalChallengeIndex >=
            finalPlayers.size
        ) {

            if (
                isHostFromCurrentData()
            ) {

                finishWithoutWinner()
            }

            return
        }

        val currentFinalUid =
            finalPlayers[
                finalChallengeIndex
            ]

        if (
            currentPlayerUid !=
            currentFinalUid
        ) {
            return
        }

        if (
            currentFinalUid !=
            myUid
        ) {
            return
        }

        if (
            lastShownFinalIndex ==
            finalChallengeIndex
        ) {
            return
        }

        lastShownFinalIndex =
            finalChallengeIndex

        showFinalWholeWordDialog(
            currentFinalUid
        )
    }

    private fun getFinalPlayers():
        List<String> {

        return playerOrder.filter {
            isPlayerCurrentlyActive(it) &&
                !eliminatedPlayers.contains(it) &&
                it != wordMasterUid
        }
    }

    private fun showFinalWholeWordDialog(
        uid: String
    ) {

        val playerName =
            playerNames[uid]
                ?: "Player"

        val input =
            EditText(this)

        input.hint =
            "Type the entire word"

        input.setSingleLine(
            true
        )

        input.textSize =
            18f

        val timerDisplay =
            TextView(this)

        timerDisplay.text =
            "Time: 20"

        timerDisplay.textSize =
            22f

        timerDisplay.gravity =
            Gravity.CENTER

        timerDisplay.setTypeface(
            null,
            Typeface.BOLD
        )

        val layout =
            LinearLayout(this)

        layout.orientation =
            LinearLayout.VERTICAL

        layout.setPadding(
            30,
            10,
            30,
            5
        )

        layout.addView(input)
        layout.addView(timerDisplay)

        val dialog =
            AlertDialog.Builder(this)
                .setTitle(
                    "$playerName — FINAL WHOLE-WORD GUESS"
                )
                .setMessage(
                    "Type the entire word and press Guess."
                )
                .setView(layout)
                .setNegativeButton(
                    "Skip",
                    null
                )
                .setPositiveButton(
                    "Guess",
                    null
                )
                .setCancelable(false)
                .create()

        dialog.setOnShowListener {

            dialog.getButton(
                AlertDialog.BUTTON_NEGATIVE
            ).setOnClickListener {

                wholeWordTimer?.cancel()

                dialog.dismiss()

                submitFinalGuess(
                    ""
                )
            }

            dialog.getButton(
                AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener {

                val guess =
                    input.text
                        .toString()
                        .trim()
                        .uppercase()

                if (
                    guess.isEmpty()
                ) {

                    input.error =
                        "Enter the entire word."

                    return@setOnClickListener
                }

                wholeWordTimer?.cancel()

                dialog.dismiss()

                submitFinalGuess(
                    guess
                )
            }

            wholeWordTimer =
                object : CountDownTimer(
                    20000L,
                    250L
                ) {

                    override fun onTick(
                        millisUntilFinished: Long
                    ) {

                        val seconds =
                            (
                                (
                                    millisUntilFinished +
                                        999L
                                ) / 1000L
                            ).toInt()

                        timerDisplay.text =
                            "Time: $seconds"

                        timerDisplay.setTextColor(
                            if (
                                seconds <= 3
                            ) {
                                Color.RED
                            } else {
                                Color.BLACK
                            }
                        )
                    }

                    override fun onFinish() {

                        dialog.dismiss()

                        submitFinalGuess(
                            ""
                        )
                    }
                }.start()
        }

        dialog.show()
    }

    private fun submitFinalGuess(
        guess: String
    ) {

        gameReference().runTransaction(
            object : Transaction.Handler {

                override fun doTransaction(
                    data: MutableData
                ): Transaction.Result {

                    val currentUid =
                        data.child(
                            "currentPlayerUid"
                        )
                            .getValue(
                                String::class.java
                            )
                            ?: ""

                    if (
                        currentUid != myUid
                    ) {

                        return Transaction.abort()
                    }

                    if (
                        !isPlayerActive(
                            data,
                            myUid
                        )
                    ) {

                        return Transaction.abort()
                    }

                    if (
                        data.child("phase")
                            .getValue(
                                String::class.java
                            )
                            != "final"
                    ) {

                        return Transaction.abort()
                    }

                    val word =
                        data.child(
                            "secretWord"
                        )
                            .getValue(
                                String::class.java
                            )
                            ?: ""

                    if (
                        guess.isNotEmpty() &&
                        guess ==
                        word.trim().uppercase()
                    ) {

                        data.child(
                            "status"
                        ).value =
                            "roundFinished"

                        data.child(
                            "roundWinnerUid"
                        ).value =
                            myUid

                        data.child(
                            "roundWinnerName"
                        ).value =
                            playerNames[
                                myUid
                            ] ?: "Player"

                        data.child(
                            "turnEndsAt"
                        ).value =
                            0L

                        return Transaction.success(
                            data
                        )
                    }

                    val index =
                        data.child(
                            "finalChallengeIndex"
                        )
                            .getValue(
                                Int::class.java
                            )
                            ?: 0

                    val newIndex =
                        index + 1

                    val finalPlayers =
                        mutableListOf<String>()

                    val finalData =
                        data.child(
                            "finalChallengePlayers"
                        )

                    for (
                        child in finalData.children
                    ) {

                        child.getValue(
                            String::class.java
                        )?.let {
                            finalPlayers.add(it)
                        }
                    }

                    if (
                        newIndex >=
                        finalPlayers.size
                    ) {

                        data.child(
                            "status"
                        ).value =
                            "roundFinished"

                        data.child(
                            "roundWinnerUid"
                        ).value =
                            ""

                        data.child(
                            "roundWinnerName"
                        ).value =
                            ""

                        data.child(
                            "turnEndsAt"
                        ).value =
                            0L

                    } else {

                        data.child(
                            "finalChallengeIndex"
                        ).value =
                            newIndex

                        data.child(
                            "currentPlayerUid"
                        ).value =
                            finalPlayers[
                                newIndex
                            ]

                        data.child(
                            "turnEndsAt"
                        ).value =
                            System.currentTimeMillis() +
                                20000L
                    }

                    return Transaction.success(
                        data
                    )
                }

                override fun onComplete(
                    error: DatabaseError?,
                    committed: Boolean,
                    currentData: DataSnapshot?
                ) {

                    if (!committed) {

                        showStatus(
                            "Final guess could not be submitted.",
                            Color.RED
                        )
                    }
                }
            }
        )
    }

    private fun showRoundFinishedDialogIfNeeded() {

        if (
            roundNumber <= 0 ||
            lastShownRoundFinished ==
            roundNumber
        ) {
            return
        }

        lastShownRoundFinished =
            roundNumber

        val winnerMessage =
            if (
                roundWinnerUid.isNotEmpty()
            ) {

                "$roundWinnerName wins the round!"

            } else {

                "Nobody guessed the whole word."
            }

        val fullMessage =
            "$winnerMessage\n\nThe word was:\n$secretWord"

        val dialog =
            AlertDialog.Builder(this)
                .setTitle(
                    "🏆 ROUND OVER"
                )
                .setMessage(
                    fullMessage
                )
                .setPositiveButton(
                    "Continue Playing",
                    null
                )
                .setNegativeButton(
                    "Leave Game",
                    null
                )
                .setCancelable(false)
                .create()

        dialog.setOnShowListener {

            val continueButton =
                dialog.getButton(
                    AlertDialog.BUTTON_POSITIVE
                )

            continueButton.isEnabled =
                true

            continueButton.setOnClickListener {

                dialog.dismiss()

                requestContinueGame()
            }

            dialog.getButton(
                AlertDialog.BUTTON_NEGATIVE
            ).setOnClickListener {

                dialog.dismiss()

                leaveGame()
            }
        }

        dialog.show()
    }

    private fun requestContinueGame() {

        if (
            wordSelection ==
            "Random Word"
        ) {

            startNextRandomRound()

        } else {

            showNextManualWordDialog()
        }
    }

    private fun startNextRandomRound() {

        gameReference().runTransaction(
            object : Transaction.Handler {

                override fun doTransaction(
                    data: MutableData
                ): Transaction.Result {

                    val currentStatus =
                        data.child(
                            "status"
                        )
                            .getValue(
                                String::class.java
                            )
                            ?: ""

                    if (
                        currentStatus !=
                        "roundFinished"
                    ) {

                        return Transaction.abort()
                    }

                    val selected =
                        chooseOnlineWord()

                    val activePlayers =
                        getActivePlayerIds(
                            data
                        ).filter { uid ->

                            data.child(
                                "eliminatedPlayers"
                            )
                                .child(uid)
                                .getValue(
                                    Boolean::class.java
                                ) != true
                        }

                    if (
                        activePlayers.isEmpty()
                    ) {

                        return Transaction.abort()
                    }

                    val startingPlayer =
                        activePlayers.first()

                    data.child(
                        "status"
                    ).value =
                        "playing"

                    val oldRound =
                        data.child(
                            "roundNumber"
                        )
                            .getValue(
                                Int::class.java
                            )
                            ?: 0

                    data.child(
                        "roundNumber"
                    ).value =
                        oldRound + 1

                    data.child(
                        "phase"
                    ).value =
                        "normal"

                    data.child(
                        "secretWord"
                    ).value =
                        selected.first

                    data.child(
                        "actualCategory"
                    ).value =
                        selected.second

                    data.child(
                        "currentPlayerUid"
                    ).value =
                        startingPlayer

                    data.child(
                        "completedTurns"
                    ).value =
                        0

                    data.child(
                        "roundWinnerUid"
                    ).value =
                        ""

                    data.child(
                        "roundWinnerName"
                    ).value =
                        ""

                    data.child(
                        "wordMasterUid"
                    ).value =
                        ""

                    data.child(
                        "turnEndsAt"
                    ).value =
                        if (
                            secondsPerTurn <= 0
                        ) {

                            0L

                        } else {

                            System.currentTimeMillis() +
                                secondsPerTurn *
                                1000L
                        }

                    data.child(
                        "guessedLetters"
                    ).value =
                        emptyMap<String, Any>()

                    data.child(
                        "missedTurns"
                    ).value =
                        createPlayerMapFromIds(
                            activePlayers
                        )

                    data.child(
                        "eliminatedPlayers"
                    ).value =
                        emptyMap<String, Any>()

                    data.child(
                        "finalChallengePlayers"
                    ).value =
                        emptyList<String>()

                    data.child(
                        "finalChallengeIndex"
                    ).value =
                        0

                    return Transaction.success(
                        data
                    )
                }

                override fun onComplete(
                    error: DatabaseError?,
                    committed: Boolean,
                    currentData: DataSnapshot?
                ) {

                    if (!committed) {

                        showStatus(
                            "Another player already started the next round.",
                            Color.DKGRAY
                        )

                        return
                    }

                    wholeWordAttemptUsed =
                        false

                    singlePlayerDialogShown =
                        false

                    lastShownFinalIndex =
                        -1

                    showStatus(
                        "New round started!",
                        Color.rgb(
                            0,
                            100,
                            0
                        )
                    )
                }
            }
        )
    }

    private fun createPlayerMapFromIds(
        ids: List<String>
    ): Map<String, Any> {

        val map =
            mutableMapOf<String, Any>()

        for (
            uid in ids
        ) {

            map[uid] = 0
        }

        return map
    }

    private fun getHostUid(): String {
        return knownHostUid
    }

    private fun isHostFromCurrentData():
        Boolean {

        return myUid.isNotEmpty() &&
            knownHostUid == myUid
    }

    private fun finishWithoutWinner() {

        gameReference().updateChildren(
            mapOf(
                "status" to
                    "roundFinished",

                "roundWinnerUid" to
                    "",

                "roundWinnerName" to
                    "",

                "turnEndsAt" to
                    0L
            )
        )
    }

    private fun startNextRoundAsHost() {

        if (
            !isActuallyHost()
        ) {
            return
        }

        if (
            wordSelection ==
            "Random Word"
        ) {

            startNextRandomRound()

        } else {

            showNextManualWordDialog()
        }
    }

    private fun createEmptyPlayerMap():
        Map<String, Any> {

        val map =
            mutableMapOf<String, Any>()

        for (
            uid in playerOrder
        ) {

            map[uid] = 0
        }

        return map
    }

    private fun chooseNextStartingPlayer():
        String {

        val active =
            playerOrder.filter {
                isPlayerCurrentlyActive(it) &&
                    !eliminatedPlayers.contains(it) &&
                    it != wordMasterUid
            }

        return active.firstOrNull()
            ?: playerOrder.firstOrNull()
            ?: ""
    }

    private fun showNextManualWordDialog() {

        if (
            nextWordMaster ==
            "Winner chooses"
        ) {

            showWinnerChoosesWordMaster()

            return
        }

        val newMaster =
            chooseNextWordMaster()

        if (
            newMaster.isEmpty()
        ) {

            showStatus(
                "Could not determine the next Word Master.",
                Color.RED
            )

            return
        }

        wordMasterUid =
            newMaster

        showNewWordEntryForMaster(
            newMaster
        )
    }

    private fun showWinnerChoosesWordMaster() {

        val winnerUid =
            roundWinnerUid

        if (
            winnerUid.isEmpty()
        ) {

            showStatus(
                "There was no winner to choose the next Word Master.",
                Color.RED
            )

            return
        }

        if (
            winnerUid != myUid
        ) {

            val winnerName =
                playerNames[
                    winnerUid
                ] ?: "Winner"

            AlertDialog.Builder(this)
                .setTitle(
                    "Next Word Master"
                )
                .setMessage(
                    "$winnerName will choose the next Word Master."
                )
                .setPositiveButton(
                    "Waiting",
                    null
                )
                .show()

            return
        }

        val activePlayers =
            playerOrder.filter {
                isPlayerCurrentlyActive(it) &&
                    !eliminatedPlayers.contains(it)
            }

        if (
            activePlayers.isEmpty()
        ) {

            showStatus(
                "No active players are available.",
                Color.RED
            )

            return
        }

        val names =
            activePlayers.map {
                playerNames[
                    it
                ] ?: "Player"
            }.toTypedArray()

        var selectedIndex =
            -1

        val dialog =
            AlertDialog.Builder(this)
                .setTitle(
                    "Choose the Next Word Master"
                )
                .setSingleChoiceItems(
                    names,
                    -1
                ) { _, which ->

                    selectedIndex =
                        which
                }
                .setNegativeButton(
                    "Cancel"
                ) { _, _ ->
                    finish()
                }
                .setPositiveButton(
                    "Continue",
                    null
                )
                .setCancelable(false)
                .create()

        dialog.setOnShowListener {

            val continueButton =
                dialog.getButton(
                    AlertDialog.BUTTON_POSITIVE
                )

            continueButton.setOnClickListener {

                if (
                    selectedIndex < 0
                ) {

                    Toast.makeText(
                        this,
                        "Choose a Word Master first.",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setOnClickListener
                }

                val selectedUid =
                    activePlayers[
                        selectedIndex
                    ]

                dialog.dismiss()

                wordMasterUid =
                    selectedUid

                showNewWordEntryForMaster(
                    selectedUid
                )
            }
        }

        dialog.show()
    }

    private fun showNewWordEntryForMaster(
        newMaster: String
    ) {

        val masterName =
            playerNames[
                newMaster
            ] ?: "Player"

        val input =
            EditText(this)

        input.hint =
            "Enter the new word"

        input.setSingleLine(
            true
        )

        input.textSize =
            18f

        val dialog =
            AlertDialog.Builder(this)
                .setTitle(
                    "$masterName — New Word"
                )
                .setMessage(
                    "The selected Word Master must enter the word."
                )
                .setView(
                    input
                )
                .setCancelable(false)
                .setNegativeButton(
                    "Cancel"
                ) { _, _ ->
                    finish()
                }
                .setPositiveButton(
                    "Start Round",
                    null
                )
                .create()

        dialog.setOnShowListener {

            val startButton =
                dialog.getButton(
                    AlertDialog.BUTTON_POSITIVE
                )

            if (
                newMaster != myUid
            ) {

                startButton.isEnabled =
                    false

                startButton.text =
                    "Waiting for $masterName"

                return@setOnShowListener
            }

            startButton.setOnClickListener {

                val word =
                    input.text
                        .toString()
                        .trim()
                        .uppercase()

                if (
                    word.isEmpty()
                ) {

                    input.error =
                        "Enter a word."

                    return@setOnClickListener
                }

                dialog.dismiss()

                startManualRound(
                    word,
                    newMaster
                )
            }
        }

        dialog.show()
    }

    private fun chooseNextWordMaster():
        String {

        val winner =
            roundWinnerUid

        val activePlayers =
            playerOrder.filter {
                isPlayerCurrentlyActive(it) &&
                    !eliminatedPlayers.contains(it)
            }

        if (
            activePlayers.isEmpty()
        ) {
            return ""
        }

        return when {

            nextWordMaster ==
                "Same" -> {

                if (
                    activePlayers.contains(
                        wordMasterUid
                    )
                ) {

                    wordMasterUid

                } else {

                    activePlayers.first()
                }
            }

            nextWordMaster ==
                "Winner" ||
                nextWordMaster ==
                "Winner becomes Word Master" -> {

                if (
                    activePlayers.contains(
                        winner
                    )
                ) {

                    winner

                } else {

                    activePlayers.first()
                }
            }

            nextWordMaster.contains(
                "Winner"
            ) &&
                nextWordMaster !=
                "Winner chooses" -> {

                if (
                    activePlayers.contains(
                        winner
                    )
                ) {

                    winner

                } else {

                    activePlayers.first()
                }
            }

            else -> {

                if (
                    activePlayers.contains(
                        knownHostUid
                    )
                ) {

                    knownHostUid

                } else {

                    activePlayers.first()
                }
            }
        }
    }

    private fun startManualRound(
        word: String,
        newMasterUid: String
    ) {

        if (
            newMasterUid != myUid
        ) {
            return
        }

        if (
            !isMyPlayerActive()
        ) {

            showStatus(
                "You are no longer an active player.",
                Color.RED
            )

            return
        }

        /*
         * Use the currently known active state rather than
         * guessing from player position.
         */
        val startingPlayer =
            playerOrder.firstOrNull {
                isPlayerCurrentlyActive(it) &&
                    it != newMasterUid &&
                    !eliminatedPlayers.contains(it)
            } ?: ""

        if (
            startingPlayer.isEmpty()
        ) {

            showStatus(
                "There is no active player available to start the round.",
                Color.RED
            )

            return
        }

        val updates =
            hashMapOf<String, Any>(
                "status" to
                    "playing",

                "roundNumber" to
                    roundNumber + 1,

                "phase" to
                    "normal",

                "secretWord" to
                    word,

                "actualCategory" to
                    selectedCategory,

                "wordMasterUid" to
                    newMasterUid,

                "currentPlayerUid" to
                    startingPlayer,

                "completedTurns" to
                    0,

                "roundWinnerUid" to
                    "",

                "roundWinnerName" to
                    "",

                "turnEndsAt" to
                    getNewTurnEndTime(),

                "guessedLetters" to
                    emptyMap<String, Any>(),

                "scores" to
                    scores,

                "missedTurns" to
                    createEmptyPlayerMap(),

                "eliminatedPlayers" to
                    emptyMap<String, Any>(),

                "finalChallengePlayers" to
                    emptyList<String>(),

                "finalChallengeIndex" to
                    0
            )

        gameReference()
            .updateChildren(
                updates
            )
    }

    private fun isActuallyHost():
        Boolean {

        return auth.currentUser?.uid ==
            getKnownHostUid()
    }

    private fun getKnownHostUid():
        String {

        return knownHostUid
    }

    private fun confirmLeaveGame() {

        if (
            leavingGame
        ) {
            return
        }

        AlertDialog.Builder(this)
            .setTitle(
                "Leave Game?"
            )
            .setMessage(
                "Are you sure you want to leave this game?"
            )
            .setNegativeButton(
                "Cancel",
                null
            )
            .setPositiveButton(
                "Leave"
            ) { _, _ ->
                leaveGame()
            }
            .show()
    }

    private fun leaveGame() {

        if (
            leavingGame
        ) {
            return
        }

        leavingGame =
            true

        stopTimers()

        val uid =
            auth.currentUser?.uid

        if (
            uid == null
        ) {

            removeFirebaseListener()

            finish()

            return
        }

        showStatus(
            "Leaving game...",
            Color.DKGRAY
        )

        gameReference().runTransaction(
            object : Transaction.Handler {

                override fun doTransaction(
                    data: MutableData
                ): Transaction.Result {

                    val playersNode =
                        data.child(
                            "players"
                        )

                    if (
                        playersNode
                            .child(uid)
                            .value == null
                    ) {

                        return Transaction.success(
                            data
                        )
                    }

                    /*
                     * Removing the player completely means the
                     * remaining clients will detect the departure
                     * through their Firebase listener.
                     */
                    playersNode
                        .child(uid)
                        .value =
                        null

                    val remaining =
                        mutableListOf<String>()

                    for (
                        child in playersNode.children
                    ) {

                        child.key?.let {
                            remaining.add(it)
                        }
                    }

                    if (
                        remaining.isEmpty()
                    ) {

                        data.child(
                            "status"
                        ).value =
                            "ended"

                        data.child(
                            "currentPlayerUid"
                        ).value =
                            ""

                        data.child(
                            "wordMasterUid"
                        ).value =
                            ""

                        data.child(
                            "turnEndsAt"
                        ).value =
                            0L

                        return Transaction.success(
                            data
                        )
                    }

                    val activeRemaining =
                        getActivePlayerIds(
                            data
                        ).filter {
                            remaining.contains(it)
                        }

                    val currentUid =
                        data.child(
                            "currentPlayerUid"
                        )
                            .getValue(
                                String::class.java
                            )
                            ?: ""

                    val oldMaster =
                        data.child(
                            "wordMasterUid"
                        )
                            .getValue(
                                String::class.java
                            )
                            ?: ""

                    val oldHost =
                        data.child(
                            "hostUid"
                        )
                            .getValue(
                                String::class.java
                            )
                            ?: ""

                    /*
                     * Transfer host if the host left.
                     *
                     * Prefer an active remaining player, but if
                     * there are only waiting players, use the first
                     * remaining player so the room does not lose
                     * its host.
                     */
                    if (
                        oldHost == uid
                    ) {

                        val newHost =
                            activeRemaining.firstOrNull()
                                ?: remaining.first()

                        data.child(
                            "hostUid"
                        ).value =
                            newHost

                        /*
                         * Keep the host flag in sync if the player
                         * nodes use it.
                         */
                        data.child(
                            "players"
                        )
                            .child(newHost)
                            .child("isHost")
                            .value =
                            true
                    }

                    /*
                     * If the Word Master left, transfer the role
                     * to a remaining active player when possible.
                     */
                    if (
                        oldMaster == uid
                    ) {

                        val newMaster =
                            activeRemaining.firstOrNull()

                        data.child(
                            "wordMasterUid"
                        ).value =
                            newMaster ?: ""
                    }

                    /*
                     * If the current player left, choose the next
                     * active non-eliminated player.
                     */
                    if (
                        currentUid == uid
                    ) {

                        val candidates =
                            activeRemaining.filter {
                                data.child(
                                    "eliminatedPlayers"
                                )
                                    .child(it)
                                    .getValue(
                                        Boolean::class.java
                                    ) != true &&
                                    it !=
                                    data.child(
                                        "wordMasterUid"
                                    )
                                        .getValue(
                                            String::class.java
                                        )
                            }

                        if (
                            candidates.isNotEmpty()
                        ) {

                            val oldIndex =
                                remaining.indexOf(uid)

                            var chosen =
                                candidates.first()

                            if (
                                oldIndex >= 0
                            ) {

                                for (
                                    step in 1..remaining.size
                                ) {

                                    val index =
                                        (
                                            oldIndex +
                                                step
                                        ) %
                                            remaining.size

                                    val candidate =
                                        remaining[
                                            index
                                        ]

                                    if (
                                        candidates.contains(
                                            candidate
                                        )
                                    ) {

                                        chosen =
                                            candidate

                                        break
                                    }
                                }
                            }

                            data.child(
                                "currentPlayerUid"
                            ).value =
                                chosen

                            data.child(
                                "turnEndsAt"
                            ).value =
                                if (
                                    secondsPerTurn <= 0
                                ) {

                                    0L

                                } else {

                                    System.currentTimeMillis() +
                                        secondsPerTurn *
                                        1000L
                                }

                        } else if (
                            activeRemaining.isNotEmpty()
                        ) {

                            data.child(
                                "currentPlayerUid"
                            ).value =
                                activeRemaining.first()

                            data.child(
                                "turnEndsAt"
                            ).value =
                                if (
                                    secondsPerTurn <= 0
                                ) {

                                    0L

                                } else {

                                    System.currentTimeMillis() +
                                        secondsPerTurn *
                                        1000L
                                }

                        } else {

                            data.child(
                                "currentPlayerUid"
                            ).value =
                                ""

                            data.child(
                                "turnEndsAt"
                            ).value =
                                0L
                        }
                    }

                    /*
                     * If exactly one active player remains,
                     * make sure that player is the current player.
                     * This lets the remaining player continue
                     * rather than getting stuck on a departed UID.
                     */
                    if (
                        activeRemaining.size == 1
                    ) {

                        val onlyPlayer =
                            activeRemaining.first()

                        data.child(
                            "currentPlayerUid"
                        ).value =
                            onlyPlayer

                        /*
                         * Once only one active player remains,
                         * there should not be a separate Word Master
                         * blocking that player's turn.
                         */
                        data.child(
                            "wordMasterUid"
                        ).value =
                            ""

                        data.child(
                            "phase"
                        ).value =
                            "normal"

                        data.child(
                            "turnEndsAt"
                        ).value =
                            if (
                                secondsPerTurn <= 0
                            ) {

                                0L

                            } else {

                                System.currentTimeMillis() +
                                    secondsPerTurn *
                                    1000L
                            }
                    }

                    return Transaction.success(
                        data
                    )
                }

                override fun onComplete(
                    error: DatabaseError?,
                    committed: Boolean,
                    currentData: DataSnapshot?
                ) {

                    if (
                        error != null ||
                        !committed
                    ) {

                        leavingGame =
                            false

                        Toast.makeText(
                            this@OnlineGameRoundActivity,
                            "Could not leave the game. Please try again.",
                            Toast.LENGTH_LONG
                        ).show()

                        return
                    }

                    removeFirebaseListener()

                    Toast.makeText(
                        this@OnlineGameRoundActivity,
                        "You left the game.",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()
                }
            }
        )
    }

    private fun stopTimers() {

        timer?.cancel()
        wholeWordTimer?.cancel()

        timer = null
        wholeWordTimer = null

        timerStateUid = ""
        timerStateEndsAt = -1L
        timerStatePhase = ""
        timerStateRound = -1
    }

    private fun removeFirebaseListener() {

        gameListener?.let {

            gameReference()
                .removeEventListener(
                    it
                )
        }

        gameListener =
            null
    }

    override fun onDestroy() {

        stopTimers()

        removeFirebaseListener()

        super.onDestroy()
    }
}

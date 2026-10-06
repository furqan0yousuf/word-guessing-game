package com.wordguessing.game

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class CreateGameActivity : Activity() {

    private val auth =
        FirebaseAuth.getInstance()

    private val database =
        FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        scrollView.addView(layout)

        // TITLE
        val title =
            TextView(this)

        title.text =
            "Create Game"

        title.textSize =
            28f

        title.gravity =
            Gravity.CENTER

        title.setTextColor(
            Color.BLACK
        )

        layout.addView(title)

        // YOUR NAME
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

        // ROOM TYPE
        val roomTypeTitle =
            TextView(this)

        roomTypeTitle.text =
            "Game Visibility"

        roomTypeTitle.textSize =
            20f

        roomTypeTitle.setPadding(
            0,
            30,
            0,
            10
        )

        layout.addView(roomTypeTitle)

        val roomTypeGroup =
            RadioGroup(this)

        roomTypeGroup.orientation =
            RadioGroup.VERTICAL

        val publicRadio =
            RadioButton(this)

        publicRadio.text =
            "Public Game"

        publicRadio.textSize =
            18f

        val privateRadio =
            RadioButton(this)

        privateRadio.text =
            "Private Game"

        privateRadio.textSize =
            18f

        roomTypeGroup.addView(
            publicRadio
        )

        roomTypeGroup.addView(
            privateRadio
        )

        /*
         * Default = Public Game.
         */
        publicRadio.isChecked =
            true

        layout.addView(
            roomTypeGroup
        )

        // PRIVATE PASSWORD
        val passwordLayout =
            LinearLayout(this)

        passwordLayout.orientation =
            LinearLayout.VERTICAL

        passwordLayout.visibility =
            View.GONE

        layout.addView(
            passwordLayout
        )

        val passwordTitle =
            TextView(this)

        passwordTitle.text =
            "Private Game Password"

        passwordTitle.textSize =
            20f

        passwordTitle.setPadding(
            0,
            15,
            0,
            10
        )

        passwordLayout.addView(
            passwordTitle
        )

        val passwordInput =
            EditText(this)

        passwordInput.hint =
            "Enter password"

        passwordInput.textSize =
            20f

        passwordInput.setSingleLine(true)

        passwordLayout.addView(
            passwordInput
        )

        roomTypeGroup.setOnCheckedChangeListener {
                _,
                checkedId ->

            passwordLayout.visibility =
                if (
                    checkedId ==
                    privateRadio.id
                ) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
        }

        // MAXIMUM ACTIVE PLAYERS
        val playersTitle =
            TextView(this)

        playersTitle.text =
            "Maximum Active Players"

        playersTitle.textSize =
            20f

        playersTitle.setPadding(
            0,
            30,
            0,
            10
        )

        layout.addView(playersTitle)

        val playersSpinner =
            Spinner(this)

        val playerOptions =
            arrayOf(
                "2 Players",
                "3 Players",
                "4 Players"
            )

        playersSpinner.adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                playerOptions
            )

        layout.addView(playersSpinner)

        // MAXIMUM ROOM PLAYERS
        val roomCapacityTitle =
            TextView(this)

        roomCapacityTitle.text =
            "Maximum Room Players"

        roomCapacityTitle.textSize =
            20f

        roomCapacityTitle.setPadding(
            0,
            25,
            0,
            10
        )

        layout.addView(roomCapacityTitle)

        val roomCapacitySpinner =
            Spinner(this)

        val roomCapacityOptions =
            arrayOf(
                "4 People",
                "5 People",
                "6 People",
                "7 People",
                "8 People",
                "9 People",
                "10 People"
            )

        roomCapacitySpinner.adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                roomCapacityOptions
            )

        /*
         * Default room capacity is 4.
         */
        roomCapacitySpinner.setSelection(0)

        layout.addView(
            roomCapacitySpinner
        )

        // WAITING PLAYER GUESSES
        val waitingGuessesTitle =
            TextView(this)

        waitingGuessesTitle.text =
            "Waiting Player Whole-Word Guesses"

        waitingGuessesTitle.textSize =
            20f

        waitingGuessesTitle.setPadding(
            0,
            25,
            0,
            10
        )

        layout.addView(
            waitingGuessesTitle
        )

        val waitingGuessesSpinner =
            Spinner(this)

        val waitingGuessOptions =
            arrayOf(
                "1 Guess",
                "2 Guesses",
                "3 Guesses",
                "4 Guesses",
                "5 Guesses"
            )

        waitingGuessesSpinner.adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                waitingGuessOptions
            )

        /*
         * Default = 2 guesses.
         */
        waitingGuessesSpinner.setSelection(1)

        layout.addView(
            waitingGuessesSpinner
        )

        // WORD SELECTION
        val wordSelectionTitle =
            TextView(this)

        wordSelectionTitle.text =
            "WORD SELECTION"

        wordSelectionTitle.textSize =
            18f

        wordSelectionTitle.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        wordSelectionTitle.setPadding(
            0,
            30,
            0,
            10
        )

        layout.addView(
            wordSelectionTitle
        )

        val wordSelectionSpinner =
            Spinner(this)

        val wordSelectionOptions =
            arrayOf(
                "Random Word",
                "Manual Word"
            )

        wordSelectionSpinner.adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                wordSelectionOptions
            )

        layout.addView(
            wordSelectionSpinner
        )

        // MANUAL WORD
        val manualWordInput =
            EditText(this)

        manualWordInput.hint =
            "Enter the word"

        manualWordInput.textSize =
            20f

        manualWordInput.setSingleLine(true)

        manualWordInput.visibility =
            View.GONE

        layout.addView(
            manualWordInput
        )

        wordSelectionSpinner.onItemSelectedListener =
            object :
                AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    manualWordInput.visibility =
                        if (position == 1) {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }
                }

                override fun onNothingSelected(
                    parent: AdapterView<*>?
                ) {
                }
            }

        // CATEGORY
        val categoryTitle =
            TextView(this)

        categoryTitle.text =
            "CATEGORY"

        categoryTitle.textSize =
            18f

        categoryTitle.setTypeface(
            null,
            android.graphics.Typeface.BOLD
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

        val categoryOptions =
            arrayOf(
                "Random",
                "Animals",
                "Food",
                "Places",
                "Sports",
                "Movies",
                "Things"
            )

        categorySpinner.adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                categoryOptions
            )

        layout.addView(
            categorySpinner
        )

        // ADVANCED SETTINGS BUTTON
        val advancedButton =
            Button(this)

        advancedButton.text =
            "ADVANCED SETTINGS"

        advancedButton.textSize =
            17f

        layout.addView(
            advancedButton
        )

        val advancedLayout =
            LinearLayout(this)

        advancedLayout.orientation =
            LinearLayout.VERTICAL

        advancedLayout.visibility =
            View.GONE

        layout.addView(
            advancedLayout
        )

        // TIME PER TURN
        val timeTitle =
            TextView(this)

        timeTitle.text =
            "Time per Turn"

        timeTitle.textSize =
            20f

        timeTitle.setPadding(
            0,
            20,
            0,
            10
        )

        advancedLayout.addView(
            timeTitle
        )

        val timeSpinner =
            Spinner(this)

        val timeOptions =
            arrayOf(
                "20 Seconds",
                "30 Seconds",
                "45 Seconds",
                "60 Seconds",
                "Unlimited"
            )

        timeSpinner.adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                timeOptions
            )

        advancedLayout.addView(
            timeSpinner
        )

        // TOTAL TURNS
        val turnsTitle =
            TextView(this)

        turnsTitle.text =
            "Total Turns"

        turnsTitle.textSize =
            20f

        turnsTitle.setPadding(
            0,
            20,
            0,
            10
        )

        advancedLayout.addView(
            turnsTitle
        )

        val turnsSpinner =
            Spinner(this)

        val turnsOptions =
            arrayOf(
                "Unlimited",
                "5 Turns",
                "10 Turns",
                "15 Turns",
                "20 Turns",
                "30 Turns"
            )

        turnsSpinner.adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                turnsOptions
            )

        advancedLayout.addView(
            turnsSpinner
        )

        // NEXT WORD MASTER
        val nextMasterTitle =
            TextView(this)

        nextMasterTitle.text =
            "Next Word Master"

        nextMasterTitle.textSize =
            20f

        nextMasterTitle.setPadding(
            0,
            20,
            0,
            10
        )

        advancedLayout.addView(
            nextMasterTitle
        )

        val nextMasterSpinner =
            Spinner(this)

        val nextMasterOptions =
            arrayOf(
                "Same Word Master",
                "Winner becomes Word Master",
                "Host chooses Word Master",
                "Winner chooses Word Master"
            )

        nextMasterSpinner.adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                nextMasterOptions
            )

        advancedLayout.addView(
            nextMasterSpinner
        )

        // DEFAULT SETTINGS BUTTON
        val defaultButton =
            Button(this)

        defaultButton.text =
            "Use Default Settings"

        defaultButton.textSize =
            16f

        advancedLayout.addView(
            defaultButton
        )

        advancedButton.setOnClickListener {

            if (
                advancedLayout.visibility ==
                View.GONE
            ) {

                advancedLayout.visibility =
                    View.VISIBLE

                advancedButton.text =
                    "HIDE ADVANCED SETTINGS"

            } else {

                advancedLayout.visibility =
                    View.GONE

                advancedButton.text =
                    "ADVANCED SETTINGS"
            }
        }

        defaultButton.setOnClickListener {

            timeSpinner.setSelection(0)

            turnsSpinner.setSelection(0)

            nextMasterSpinner.setSelection(1)

            Toast.makeText(
                this,
                "Default settings restored.",
                Toast.LENGTH_SHORT
            ).show()
        }

        // CREATE GAME
        val createButton =
            Button(this)

        createButton.text =
            "CREATE GAME & JOIN"

        createButton.textSize =
            18f

        createButton.setPadding(
            0,
            20,
            0,
            20
        )

        layout.addView(
            createButton
        )

        // BACK
        val backButton =
            Button(this)

        backButton.text =
            "BACK"

        backButton.textSize =
            18f

        layout.addView(
            backButton
        )

        backButton.setOnClickListener {
            finish()
        }

        createButton.setOnClickListener {

            val playerName =
                nameInput.text
                    .toString()
                    .trim()

            if (playerName.isEmpty()) {

                Toast.makeText(
                    this,
                    "Please enter your name.",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            /*
             * Determine whether this is
             * a public or private room.
             */
            val isPublic =
                publicRadio.isChecked

            val roomType =
                if (isPublic) {
                    "public"
                } else {
                    "private"
                }

            val password =
                if (isPublic) {
                    ""
                } else {
                    passwordInput.text
                        .toString()
                        .trim()
                }

            /*
             * A private room must have a password.
             */
            if (
                !isPublic &&
                password.isEmpty()
            ) {

                Toast.makeText(
                    this,
                    "Please enter a password for the private game.",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val playerCount =
                playersSpinner
                    .selectedItemPosition + 2

            val roomCapacity =
                roomCapacitySpinner
                    .selectedItemPosition + 4

            /*
             * The room must always be large enough
             * for the maximum active players.
             */
            if (
                roomCapacity < playerCount
            ) {

                Toast.makeText(
                    this,
                    "Room capacity cannot be less than active players.",
                    Toast.LENGTH_LONG
                ).show()

                return@setOnClickListener
            }

            val waitingPlayerGuesses =
                waitingGuessesSpinner
                    .selectedItemPosition + 1

            val selectedWordSelection =
                wordSelectionSpinner
                    .selectedItem
                    .toString()

            val manualWord =
                manualWordInput.text
                    .toString()
                    .trim()
                    .uppercase()

            if (
                selectedWordSelection ==
                "Manual Word" &&
                manualWord.isEmpty()
            ) {

                Toast.makeText(
                    this,
                    "Please enter the word.",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val selectedCategory =
                categorySpinner
                    .selectedItem
                    .toString()

            val selectedTime =
                when (
                    timeSpinner.selectedItem.toString()
                ) {

                    "20 Seconds" -> 20
                    "30 Seconds" -> 30
                    "45 Seconds" -> 45
                    "60 Seconds" -> 60
                    else -> 0
                }

            val selectedTotalTurns =
                when (
                    turnsSpinner.selectedItem.toString()
                ) {

                    "5 Turns" -> 5
                    "10 Turns" -> 10
                    "15 Turns" -> 15
                    "20 Turns" -> 20
                    "30 Turns" -> 30
                    else -> 0
                }

            val selectedNextMaster =
                nextMasterSpinner
                    .selectedItem
                    .toString()

            createButton.isEnabled =
                false

            val currentUser =
                auth.currentUser

            if (currentUser != null) {

                createFirebaseGame(
                    uid = currentUser.uid,
                    playerName = playerName,
                    playerCount = playerCount,
                    roomCapacity = roomCapacity,
                    waitingPlayerGuesses =
                        waitingPlayerGuesses,
                    selectedWordSelection =
                        selectedWordSelection,
                    manualWord = manualWord,
                    selectedCategory =
                        selectedCategory,
                    selectedTime =
                        selectedTime,
                    selectedTotalTurns =
                        selectedTotalTurns,
                    selectedNextMaster =
                        selectedNextMaster,
                    password = password,
                    roomType = roomType,
                    isPublic = isPublic
                )

            } else {

                auth.signInAnonymously()
                    .addOnSuccessListener { result ->

                        val uid =
                            result.user?.uid

                        if (uid == null) {

                            createButton.isEnabled =
                                true

                            Toast.makeText(
                                this,
                                "Firebase login failed.",
                                Toast.LENGTH_LONG
                            ).show()

                            return@addOnSuccessListener
                        }

                        createFirebaseGame(
                            uid = uid,
                            playerName =
                                playerName,
                            playerCount =
                                playerCount,
                            roomCapacity =
                                roomCapacity,
                            waitingPlayerGuesses =
                                waitingPlayerGuesses,
                            selectedWordSelection =
                                selectedWordSelection,
                            manualWord =
                                manualWord,
                            selectedCategory =
                                selectedCategory,
                            selectedTime =
                                selectedTime,
                            selectedTotalTurns =
                                selectedTotalTurns,
                            selectedNextMaster =
                                selectedNextMaster,
                            password =
                                password,
                            roomType =
                                roomType,
                            isPublic =
                                isPublic
                        )
                    }
                    .addOnFailureListener {

                        createButton.isEnabled =
                            true

                        Toast.makeText(
                            this,
                            "Firebase login failed.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
        }

        setContentView(scrollView)
    }

    private fun createFirebaseGame(
        uid: String,
        playerName: String,
        playerCount: Int,
        roomCapacity: Int,
        waitingPlayerGuesses: Int,
        selectedWordSelection: String,
        manualWord: String,
        selectedCategory: String,
        selectedTime: Int,
        selectedTotalTurns: Int,
        selectedNextMaster: String,
        password: String,
        roomType: String,
        isPublic: Boolean
    ) {

        val gamesReference =
            database
                .getReference("games")

        findAvailableGameCode(
            gamesReference =
                gamesReference,
            uid = uid,
            playerName =
                playerName,
            playerCount =
                playerCount,
            roomCapacity =
                roomCapacity,
            waitingPlayerGuesses =
                waitingPlayerGuesses,
            selectedWordSelection =
                selectedWordSelection,
            manualWord =
                manualWord,
            selectedCategory =
                selectedCategory,
            selectedTime =
                selectedTime,
            selectedTotalTurns =
                selectedTotalTurns,
            selectedNextMaster =
                selectedNextMaster,
            password =
                password,
            roomType =
                roomType,
            isPublic =
                isPublic
        )
    }

    private fun findAvailableGameCode(
        gamesReference:
            com.google.firebase.database.DatabaseReference,
        uid: String,
        playerName: String,
        playerCount: Int,
        roomCapacity: Int,
        waitingPlayerGuesses: Int,
        selectedWordSelection: String,
        manualWord: String,
        selectedCategory: String,
        selectedTime: Int,
        selectedTotalTurns: Int,
        selectedNextMaster: String,
        password: String,
        roomType: String,
        isPublic: Boolean
    ) {

        val gameCode =
            (100000..999999)
                .random()
                .toString()

        val gameReference =
            gamesReference
                .child(gameCode)

        gameReference.get()
            .addOnSuccessListener { snapshot ->

                if (snapshot.exists()) {

                    findAvailableGameCode(
                        gamesReference =
                            gamesReference,
                        uid =
                            uid,
                        playerName =
                            playerName,
                        playerCount =
                            playerCount,
                        roomCapacity =
                            roomCapacity,
                        waitingPlayerGuesses =
                            waitingPlayerGuesses,
                        selectedWordSelection =
                            selectedWordSelection,
                        manualWord =
                            manualWord,
                        selectedCategory =
                            selectedCategory,
                        selectedTime =
                            selectedTime,
                        selectedTotalTurns =
                            selectedTotalTurns,
                        selectedNextMaster =
                            selectedNextMaster,
                        password =
                            password,
                        roomType =
                            roomType,
                        isPublic =
                            isPublic
                    )

                    return@addOnSuccessListener
                }

                val currentTime =
                    System.currentTimeMillis()

                val hostPlayer =
                    hashMapOf<String, Any>(
                        "uid" to uid,
                        "name" to playerName,
                        "isHost" to true,
                        "isActive" to true,
                        "joinedAt" to currentTime
                    )

                val gameData =
                    hashMapOf<String, Any>(
                        "gameCode" to gameCode,

                        "hostUid" to uid,

                        "hostName" to playerName,

                        /*
                         * PUBLIC / PRIVATE
                         */
                        "roomType" to roomType,

                        "isPublic" to isPublic,

                        /*
                         * Existing field.
                         *
                         * This represents the maximum
                         * ACTIVE players.
                         */
                        "maxPlayers" to playerCount,

                        /*
                         * Maximum number of people
                         * who can be inside the room.
                         */
                        "roomCapacity" to roomCapacity,

                        /*
                         * Number of whole-word guesses
                         * allowed for waiting players.
                         */
                        "waitingPlayerGuesses" to
                            waitingPlayerGuesses,

                        "wordSelection" to
                            selectedWordSelection,

                        "manualWord" to
                            manualWord,

                        "category" to
                            selectedCategory,

                        "secondsPerTurn" to
                            selectedTime,

                        "totalTurns" to
                            selectedTotalTurns,

                        "nextWordMaster" to
                            selectedNextMaster,

                        /*
                         * Password is blank for public rooms.
                         */
                        "password" to password,

                        "status" to "waiting",

                        "createdAt" to currentTime,

                        /*
                         * This will be used later for
                         * private-room expiration.
                         */
                        "lastPlayedAt" to currentTime,

                        "players" to
                            mapOf(
                                uid to hostPlayer
                            )
                    )

                gameReference
                    .setValue(gameData)
                    .addOnSuccessListener {

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
                            selectedWordSelection
                        )

                        intent.putExtra(
                            "manualWord",
                            manualWord
                        )

                        intent.putExtra(
                            "category",
                            selectedCategory
                        )

                        intent.putExtra(
                            "secondsPerTurn",
                            selectedTime
                        )

                        intent.putExtra(
                            "totalTurns",
                            selectedTotalTurns
                        )

                        intent.putExtra(
                            "nextWordMaster",
                            selectedNextMaster
                        )

                        intent.putExtra(
                            "password",
                            password
                        )

                        intent.putExtra(
                            "roomType",
                            roomType
                        )

                        intent.putExtra(
                            "isPublic",
                            isPublic
                        )

                        intent.putExtra(
                            "playerName",
                            playerName
                        )

                        intent.putExtra(
                            "isHost",
                            true
                        )

                        startActivity(intent)

                        finish()
                    }
                    .addOnFailureListener {

                        Toast.makeText(
                            this,
                            "Could not create game.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Could not connect to Firebase.",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}


package com.wordguessing.game

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.gravity = Gravity.CENTER
        layout.setPadding(24, 24, 24, 24)

        val title = TextView(this)
        title.text = "Word Guessing Game"
        title.textSize = 28f
        title.gravity = Gravity.CENTER
        layout.addView(title)

        val singleButton = Button(this)
        singleButton.text = "Single Player"
        singleButton.setOnClickListener {
            startActivity(
                Intent(this, SinglePlayerSetupActivity::class.java)
            )
        }
        layout.addView(singleButton)

        val localButton = Button(this)
        localButton.text = "Local Multiplayer"
        localButton.setOnClickListener {
            startActivity(
                Intent(this, PlayerSetupActivity::class.java)
            )
        }
        layout.addView(localButton)

        val onlineButton = Button(this)
        onlineButton.text = "Online Multiplayer"
        onlineButton.setOnClickListener {
            startActivity(
                Intent(this, OnlineMultiplayerActivity::class.java)
            )
        }
        layout.addView(onlineButton)

        // TEMPORARY: Identify this phone's Firebase user.
        val uidButton = Button(this)
        uidButton.text = "Show Firebase UID"
        layout.addView(uidButton)

        val uidText = TextView(this)
        uidText.textSize = 14f
        uidText.setTextIsSelectable(true)
        uidText.gravity = Gravity.CENTER
        layout.addView(uidText)

        uidButton.setOnClickListener {
            val auth = FirebaseAuth.getInstance()
            val currentUser = auth.currentUser

            if (currentUser != null) {
                uidText.text = "Your Firebase UID:\n\n${currentUser.uid}"
            } else {
                uidText.text = "Checking Firebase sign-in..."

                auth.signInAnonymously()
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val uid = auth.currentUser?.uid

                            uidText.text = if (uid != null) {
                                "Your Firebase UID:\n\n$uid"
                            } else {
                                "Unable to retrieve UID. Please try again."
                            }
                        } else {
                            uidText.text = "Sign-in failed."
                            Toast.makeText(
                                this,
                                task.exception?.localizedMessage
                                    ?: "Firebase sign-in failed",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            }
        }

        setContentView(layout)
    }
}

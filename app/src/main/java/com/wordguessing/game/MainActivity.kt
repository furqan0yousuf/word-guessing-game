package com.wordguessing.game

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)

        layout.orientation =
            LinearLayout.VERTICAL

        layout.gravity =
            Gravity.CENTER

        layout.setPadding(
            24,
            24,
            24,
            24
        )

        /*
         * TITLE
         */

        val title =
            TextView(this)

        title.text =
            "Word Guessing Game"

        title.textSize =
            28f

        title.gravity =
            Gravity.CENTER

        layout.addView(title)

        /*
         * SINGLE PLAYER
         */

        val singleButton =
            Button(this)

        singleButton.text =
            "Single Player"

        singleButton.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    SinglePlayerSetupActivity::class.java
                )
            )
        }

        layout.addView(singleButton)

        /*
         * LOCAL MULTIPLAYER
         */

        val localButton =
            Button(this)

        localButton.text =
            "Local Multiplayer"

        localButton.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    PlayerSetupActivity::class.java
                )
            )
        }

        layout.addView(localButton)

        /*
         * ONLINE MULTIPLAYER
         */

        val onlineButton =
            Button(this)

        onlineButton.text =
            "Online Multiplayer"

        onlineButton.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    OnlineMultiplayerActivity::class.java
                )
            )
        }

        layout.addView(onlineButton)

        setContentView(layout)
    }
}

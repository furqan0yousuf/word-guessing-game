package com.wordguessing.game

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val root = findViewById<ViewGroup>(android.R.id.content)

        val singlePlayerButton =
            findButtonByText(root, "Single Player")

        val localGameButton =
            findButtonByText(root, "Local Multiplayer")

        val onlineGameButton =
            findButtonByText(root, "Online Multiplayer")

        singlePlayerButton?.setOnClickListener {
            startActivity(
                Intent(this, SinglePlayerActivity::class.java)
            )
        }

        localGameButton?.setOnClickListener {
            startActivity(
                Intent(this, PlayerSetupActivity::class.java)
            )
        }

        onlineGameButton?.setOnClickListener {
            startActivity(
                Intent(this, OnlineGameActivity::class.java)
            )
        }
    }

    private fun findButtonByText(
        parent: ViewGroup,
        text: String
    ): Button? {

        for (i in 0 until parent.childCount) {

            val child = parent.getChildAt(i)

            if (child is Button &&
                child.text.toString() == text
            ) {
                return child
            }

            if (child is ViewGroup) {

                val result =
                    findButtonByText(child, text)

                if (result != null) {
                    return result
                }
            }
        }

        return null
    }
}

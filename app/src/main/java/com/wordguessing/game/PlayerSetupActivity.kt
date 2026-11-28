package com.wordguessing.game

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class PlayerSetupActivity : Activity() {

    private val backgroundColor = Color.rgb(247, 249, 245)
    private val forestColor = Color.rgb(53, 75, 63)
    private val bodyColor = Color.rgb(105, 119, 108)
    private val sageColor = Color.rgb(226, 235, 224)
    private val borderColor = Color.rgb(229, 235, 227)

    private lateinit var content: LinearLayout
    private lateinit var selectionCard: LinearLayout
    private lateinit var nameCard: LinearLayout
    private lateinit var nameFieldsContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createScreen()
    }

    private fun createScreen() {
        window.statusBarColor = backgroundColor
        window.navigationBarColor = backgroundColor
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR

        val scrollView = ScrollView(this).apply {
            setBackgroundColor(backgroundColor)
            isFillViewport = true
            clipToPadding = false
        }

        content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(22), dp(22), dp(22), dp(30))
        }

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val backButton = textView("‹", 30f, forestColor, Typeface.NORMAL).apply {
            gravity = Gravity.CENTER
            background = roundedBackground(sageColor, dp(14))
            isClickable = true
            isFocusable = true
            setOnClickListener { finish() }
        }
        header.addView(backButton, LinearLayout.LayoutParams(dp(46), dp(46)))

        val brand = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), 0, 0, 0)
        }
        brand.addView(textView("WORDPLAY", 11f, forestColor, Typeface.BOLD).apply {
            letterSpacing = 0.16f
        })
        brand.addView(textView("LOCAL MULTIPLAYER", 9f, bodyColor, Typeface.NORMAL).apply {
            letterSpacing = 0.10f
            setPadding(0, dp(3), 0, 0)
        })
        header.addView(brand)
        content.addView(header)

        val intro = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, dp(34), 0, dp(24))
        }
        intro.addView(textView("Bring everyone in.", 28f, forestColor, Typeface.BOLD))
        intro.addView(textView(
            "Set up your players and get ready for a friendly word challenge.",
            14f,
            bodyColor,
            Typeface.NORMAL
        ).apply { setPadding(0, dp(8), 0, 0) })
        content.addView(intro)

        content.addView(sectionHeading("01", "HOW MANY PLAYERS?"))

        selectionCard = sectionCard()
        selectionCard.addView(textView(
            "Choose the number of people playing on this device.",
            13f,
            bodyColor,
            Typeface.NORMAL
        ))
        val playerOptions = listOf(
            Triple("2", "Two players", "A quick head-to-head"),
            Triple("3", "Three players", "Add another challenger"),
            Triple("4", "Four players", "Make it a group game")
        )
        playerOptions.forEachIndexed { index, option ->
            val optionCard = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                background = roundedBackground(backgroundColor, dp(13)).also {
                    it.setStroke(dp(1), borderColor)
                }
                setPadding(dp(13), dp(12), dp(13), dp(12))
                isClickable = true
                isFocusable = true
            }
            val count = textView(option.first, 20f, forestColor, Typeface.BOLD).apply {
                gravity = Gravity.CENTER
                background = roundedBackground(sageColor, dp(11))
            }
            optionCard.addView(count, LinearLayout.LayoutParams(dp(44), dp(44)))
            val labels = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(dp(12), 0, 0, 0)
            }
            labels.addView(textView(option.second, 15f, forestColor, Typeface.BOLD))
            labels.addView(textView(option.third, 12f, bodyColor, Typeface.NORMAL).apply {
                setPadding(0, dp(3), 0, 0)
            })
            optionCard.addView(labels, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
            optionCard.addView(textView("›", 25f, bodyColor, Typeface.NORMAL))
            optionCard.setOnClickListener { showNameFields(option.first.toInt()) }
            selectionCard.addView(optionCard, spacedParams(top = if (index == 0) 12 else 9))
        }
        content.addView(selectionCard, spacedParams(top = 10))

        nameCard = sectionCard().apply {
            visibility = View.GONE
        }
        nameCard.addView(textView(
            "Give each player a name. These names will appear during the game.",
            13f,
            bodyColor,
            Typeface.NORMAL
        ))
        nameFieldsContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        nameCard.addView(nameFieldsContainer, spacedParams(top = 8))
        val continueButton = primaryButton("Continue   →")
        nameCard.addView(continueButton, spacedParams(top = 16))
        continueButton.setOnClickListener { continueToGameSetup() }
        content.addView(nameCard, spacedParams(top = 18))

        scrollView.addView(content)
        setContentView(scrollView)
    }

    private fun showNameFields(playerCount: Int) {
        selectionCard.visibility = View.GONE
        nameCard.visibility = View.VISIBLE
        nameFieldsContainer.removeAllViews()

        for (i in 1..playerCount) {
            val fieldGroup = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(0, dp(9), 0, dp(5))
            }
            fieldGroup.addView(textView("PLAYER $i", 10f, bodyColor, Typeface.BOLD).apply {
                letterSpacing = 0.10f
            })
            val nameInput = EditText(this).apply {
                hint = "Enter player $i name"
                tag = "player$i"
                textSize = 15f
                setTextColor(forestColor)
                setHintTextColor(bodyColor)
                singleLine = true
                background = roundedBackground(backgroundColor, dp(11)).also {
                    it.setStroke(dp(1), borderColor)
                }
                setPadding(dp(13), dp(12), dp(13), dp(12))
                minHeight = dp(50)
            }
            fieldGroup.addView(nameInput, spacedParams(top = 7))
            nameFieldsContainer.addView(fieldGroup)
        }
        nameCard.visibility = View.VISIBLE
    }

    private fun continueToGameSetup() {
        val names = ArrayList<String>()
        val count = nameFieldsContainer.childCount

        for (i in 1..count) {
            val input = nameFieldsContainer.findViewWithTag<EditText>("player$i")
            val name = input?.text?.toString()?.trim().orEmpty()

            if (name.isEmpty()) {
                input?.error = "Enter a name"
                input?.requestFocus()
                Toast.makeText(this, "Please enter a name for every player.", Toast.LENGTH_SHORT).show()
                return
            }
            names.add(name)
        }

        if (names.size !in 2..4) {
            Toast.makeText(this, "Choose between 2 and 4 players.", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(this, GameSetupActivity::class.java)
        intent.putStringArrayListExtra("playerNames", names)
        startActivity(intent)
    }

    override fun onBackPressed() {
        if (::nameCard.isInitialized && nameCard.visibility == View.VISIBLE) {
            nameCard.visibility = View.GONE
            selectionCard.visibility = View.VISIBLE
        } else {
            super.onBackPressed()
        }
    }

    private fun sectionHeading(number: String, label: String): LinearLayout {
        val heading = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        heading.addView(textView(number, 11f, forestColor, Typeface.BOLD).apply {
            gravity = Gravity.CENTER
            background = roundedBackground(sageColor, dp(8))
        }, LinearLayout.LayoutParams(dp(28), dp(25)))
        heading.addView(textView(label, 11f, bodyColor, Typeface.BOLD).apply {
            letterSpacing = 0.12f
            setPadding(dp(10), 0, 0, 0)
        })
        return heading
    }

    private fun sectionCard(): LinearLayout =
        LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = cardBackground()
            setPadding(dp(14), dp(14), dp(14), dp(14))
        }

    private fun primaryButton(label: String): TextView =
        textView(label, 15f, Color.WHITE, Typeface.BOLD).apply {
            gravity = Gravity.CENTER
            background = roundedBackground(forestColor, dp(14))
            isClickable = true
            isFocusable = true
            setPadding(dp(16), dp(16), dp(16), dp(16))
        }

    private fun textView(value: String, size: Float, color: Int, style: Int): TextView =
        TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
            typeface = Typeface.create("sans-serif", style)
            includeFontPadding = true
        }

    private fun roundedBackground(color: Int, radius: Int): GradientDrawable =
        GradientDrawable().apply {
            setColor(color)
            cornerRadius = radius.toFloat()
        }

    private fun cardBackground(): GradientDrawable =
        GradientDrawable().apply {
            setColor(Color.WHITE)
            cornerRadius = dp(18).toFloat()
            setStroke(dp(1), borderColor)
        }

    private fun spacedParams(top: Int = 0): LinearLayout.LayoutParams =
        LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply { topMargin = dp(top) }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()
}

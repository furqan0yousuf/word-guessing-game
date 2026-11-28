package com.wordguessing.game

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class MainActivity : Activity() {

    private val backgroundColor = Color.rgb(247, 249, 245)
    private val forestColor = Color.rgb(53, 75, 63)
    private val bodyColor = Color.rgb(105, 119, 108)
    private val sageColor = Color.rgb(226, 235, 224)
    private val borderColor = Color.rgb(229, 235, 227)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(24), dp(28), dp(24), dp(28))
        }

        val brandRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val brandMark = TextView(this).apply {
            text = "W"
            textSize = 23f
            typeface = Typeface.create("sans-serif", Typeface.BOLD)
            setTextColor(forestColor)
            gravity = Gravity.CENTER
            background = roundedBackground(sageColor, dp(16))
        }
        brandRow.addView(brandMark, LinearLayout.LayoutParams(dp(48), dp(48)))

        val brandText = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), 0, 0, 0)
        }
        brandText.addView(textView(
            "WORDPLAY",
            12f,
            forestColor,
            Typeface.BOLD
        ).apply { letterSpacing = 0.16f })
        brandText.addView(textView(
            "WORD GUESSING GAME",
            10f,
            bodyColor,
            Typeface.NORMAL
        ).apply {
            letterSpacing = 0.10f
            setPadding(0, dp(3), 0, 0)
        })
        brandRow.addView(brandText, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        content.addView(brandRow)

        val intro = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, dp(48), 0, dp(28))
        }
        intro.addView(textView(
            "A little wordplay,",
            31f,
            forestColor,
            Typeface.NORMAL
        ))
        intro.addView(textView(
            "a lot of fun.",
            31f,
            forestColor,
            Typeface.BOLD
        ).apply { setPadding(0, dp(1), 0, 0) })
        intro.addView(textView(
            "Choose how you want to play today.",
            15f,
            bodyColor,
            Typeface.NORMAL
        ).apply { setPadding(0, dp(13), 0, 0) })
        content.addView(intro)

        content.addView(sectionLabel("CHOOSE YOUR GAME"))

        content.addView(gameOption(
            symbol = "1",
            title = "Single Player",
            description = "Enjoy a word challenge on your own",
            iconColor = Color.rgb(235, 241, 231)
        ) {
            startActivity(Intent(this, SinglePlayerSetupActivity::class.java))
        }, spacedParams(top = 12))

        content.addView(gameOption(
            symbol = "2",
            title = "Local Multiplayer",
            description = "Play together on one device",
            iconColor = Color.rgb(238, 240, 229)
        ) {
            startActivity(Intent(this, PlayerSetupActivity::class.java))
        }, spacedParams(top = 12))

        content.addView(gameOption(
            symbol = "↗",
            title = "Online Multiplayer",
            description = "Create a room or join your friends",
            iconColor = Color.rgb(230, 239, 233)
        ) {
            startActivity(Intent(this, OnlineMultiplayerActivity::class.java))
        }, spacedParams(top = 12))

        val footer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(0, dp(38), 0, dp(8))
        }
        val divider = View(this).apply { setBackgroundColor(borderColor) }
        footer.addView(divider, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dp(1)
        ))
        footer.addView(textView(
            "GOOD WORDS. GREAT COMPANY.",
            10f,
            bodyColor,
            Typeface.BOLD
        ).apply {
            letterSpacing = 0.13f
            gravity = Gravity.CENTER
            setPadding(0, dp(18), 0, 0)
        })
        content.addView(footer)

        scrollView.addView(content)
        setContentView(scrollView)
    }

    private fun gameOption(
        symbol: String,
        title: String,
        description: String,
        iconColor: Int,
        onClick: () -> Unit
    ): LinearLayout {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = cardBackground()
            isClickable = true
            isFocusable = true
        }

        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
        }

        val icon = TextView(this).apply {
            text = symbol
            textSize = if (symbol == "↗") 23f else 19f
            typeface = Typeface.create("sans-serif", Typeface.BOLD)
            setTextColor(forestColor)
            gravity = Gravity.CENTER
            background = roundedBackground(iconColor, dp(14))
        }
        row.addView(icon, LinearLayout.LayoutParams(dp(52), dp(52)))

        val labels = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), 0, dp(8), 0)
        }
        labels.addView(textView(title, 17f, forestColor, Typeface.BOLD))
        labels.addView(textView(description, 12f, bodyColor, Typeface.NORMAL).apply {
            setPadding(0, dp(5), 0, 0)
        })
        row.addView(labels, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))

        row.addView(textView("›", 29f, bodyColor, Typeface.NORMAL).apply {
            gravity = Gravity.CENTER
        }, LinearLayout.LayoutParams(dp(24), ViewGroup.LayoutParams.MATCH_PARENT))

        card.addView(row)
        card.setOnClickListener { onClick() }
        return card
    }

    private fun sectionLabel(label: String): TextView =
        textView(label, 11f, bodyColor, Typeface.BOLD).apply {
            letterSpacing = 0.13f
            setPadding(0, 0, 0, dp(2))
        }

    private fun textView(
        value: String,
        size: Float,
        color: Int,
        style: Int
    ): TextView = TextView(this).apply {
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
        ).apply {
            topMargin = dp(top)
        }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()
}

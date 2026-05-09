package com.example.cauldron

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
// These imports are now updated to match the new package name
import com.example.cauldron.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Current list of ingredient IDs tapped by the user
    private val currentIngredients = mutableListOf<String>()

    // SharedPreferences key
    private val PREFS_NAME = "CauldronPrefs"
    private val KEY_LAST_BREW = "last_successful_brew"

    // ── Ingredient IDs ──────────────────────────────────
    companion object {
        const val PHOENIX    = "phoenix_feather"
        const val MANDRAKE   = "mandrake_root"
        const val MOONSTONE  = "moonstone_powder"
        const val DRAGON     = "dragon_scale"
        const val NIGHTSHADE = "nightshade"
        const val WOLFSBANE  = "wolfsbane"
        const val MERMAID    = "mermaid_tear"
        const val UNICORN    = "unicorn_hair"
        const val BOOMSLANG  = "boomslang_skin"
        const val SALAMANDER = "salamander_blood"
        const val BEZOAR     = "bezoar"
        const val DITTANY    = "dittany"
    }

    // ── Potion data class ────────────────────────────────
    data class Potion(
        val name: String,
        val effect: String,
        val cauldronColor: String,
        val emoji: String
    )

    // ── Recipe library: set of ingredients (order-independent) ──
    private val recipeLibrary: Map<Set<String>, Potion> = mapOf(
        setOf(MOONSTONE, PHOENIX) to Potion(
            name   = "✨ Liquid Luck (Felix Felicis)",
            effect = "Grants the user extraordinary success and luck for 24 hours.",
            cauldronColor = "#FFD700",
            emoji  = "✨"
        ),
        setOf(NIGHTSHADE, WOLFSBANE, BEZOAR) to Potion(
            name   = "💀 Draught of Living Death",
            effect = "Sends the drinker into a powerful, death-like sleep; use with extreme caution.",
            cauldronColor = "#4B0082",
            emoji  = "💀"
        ),
        setOf(MERMAID, UNICORN, MOONSTONE) to Potion(
            name   = "💫 Veritaserum",
            effect = "A powerful truth serum that compels the drinker to answer any question honestly.",
            cauldronColor = "#F0F8FF",
            emoji  = "💫"
        ),
        setOf(DRAGON, PHOENIX, SALAMANDER) to Potion(
            name   = "♾ Elixir of Life",
            effect = "A legendary potion that grants the drinker immortality as long as they continue to drink it.",
            cauldronColor = "#E0115F",
            emoji  = "♾"
        ),
        setOf(MANDRAKE, BOOMSLANG, DITTANY) to Potion(
            name   = "🧬 Polyjuice Potion",
            effect = "Allows the drinker to assume the physical form of another individual.",
            cauldronColor = "#556B2F",
            emoji  = "🧬"
        ),
        setOf(MERMAID, DITTANY, UNICORN) to Potion(
            name   = "💕 Amortentia",
            effect = "The world's most powerful love potion, characterized by its mother-of-pearl sheen.",
            cauldronColor = "#FFB6C1",
            emoji  = "💕"
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupIngredientButtons()
        setupActionButtons()
        loadLastBrew()
    }

    private fun setupIngredientButtons() {
        val buttons = mapOf(
            binding.btnMandrake   to MANDRAKE,
            binding.btnPhoenix    to PHOENIX,
            binding.btnUnicorn    to UNICORN,
            binding.btnWolfsbane  to WOLFSBANE,
            binding.btnBezoar     to BEZOAR,
            binding.btnSalamander to SALAMANDER,
            binding.btnBoomslang  to BOOMSLANG,
            binding.btnDittany    to DITTANY,
            binding.btnMoonstone  to MOONSTONE,
            binding.btnDragon     to DRAGON,
            binding.btnNightshade to NIGHTSHADE,
            binding.btnMermaid    to MERMAID
        )

        for ((view, id) in buttons) {
            view.setOnClickListener {
                onIngredientTapped(view, id)
            }
        }
    }

    private fun setupActionButtons() {
        binding.btnBrew.setOnClickListener { onBrew() }
        binding.btnDissolve.setOnClickListener { onDissolve() }
        binding.btnInstructions.setOnClickListener { showInstructionsModal() }
    }

    private fun onIngredientTapped(view: View, ingredientId: String) {
        currentIngredients.add(ingredientId)
        updateIngredientDisplay()
        animateBounce(view)
        animateCauldronBubble()
    }

    private fun onBrew() {
        if (currentIngredients.isEmpty()) {
            Toast.makeText(this, "Add ingredients first, young wizard! 🧙", Toast.LENGTH_SHORT).show()
            return
        }

        val ingredientSet = currentIngredients.toSet()
        val matchedPotion = recipeLibrary[ingredientSet]

        if (matchedPotion != null) {
            showPotionResult(matchedPotion)
            saveLastBrew(matchedPotion.name)
            loadLastBrew()
            animateSuccess(matchedPotion.cauldronColor)
        } else {
            val failedPotion = Potion(
                name   = "💨 Failed Brew",
                effect = "The mixture has curdled into a useless sludge. Clear the cauldron and try again.",
                cauldronColor = "#808080",
                emoji  = "💨"
            )
            showPotionResult(failedPotion)
            animateFail()
        }
    }

    private fun onDissolve() {
        currentIngredients.clear()
        binding.tvIngredients.text = "Tap ingredients to begin brewing..."
        binding.resultCard.visibility = View.GONE

        binding.ivCauldron.colorFilter = null
        // Replaced R reference with correct package resource access
        binding.cauldronGlow.setBackgroundResource(R.drawable.bg_cauldron_glow)

        val rotate = ObjectAnimator.ofFloat(binding.ivCauldron, "rotation", 0f, 360f)
        rotate.duration = 600
        rotate.start()

        Toast.makeText(this, "Cauldron cleared! 🫧", Toast.LENGTH_SHORT).show()
    }

    private fun showInstructionsModal() {
        // Ensure BrewingInstructionsDialog package is also updated to com.example.cauldron
        val dialog = BrewingInstructionsDialog.newInstance()
        dialog.show(supportFragmentManager, BrewingInstructionsDialog.TAG)
    }

    private fun updateIngredientDisplay() {
        if (currentIngredients.isEmpty()) {
            binding.tvIngredients.text = "Tap ingredients to begin brewing..."
            return
        }
        val labels = mapOf(
            PHOENIX    to "Phoenix Feather",
            MANDRAKE   to "Mandrake Root",
            MOONSTONE  to "Moonstone Powder",
            DRAGON     to "Dragon Scale",
            NIGHTSHADE to "Nightshade",
            WOLFSBANE  to "Wolfsbane",
            MERMAID    to "Mermaid Tear",
            UNICORN    to "Unicorn Hair",
            BOOMSLANG  to "Boomslang Skin",
            SALAMANDER to "Salamander Blood",
            BEZOAR     to "Bezoar",
            DITTANY    to "Dittany"
        )
        val names = currentIngredients.mapNotNull { labels[it] }
        binding.tvIngredients.text = "🧪 " + names.joinToString(" + ")
    }

    private fun showPotionResult(potion: Potion) {
        binding.tvPotionName.text = potion.name
        binding.tvPotionEffect.text = potion.effect
        binding.resultCard.visibility = View.VISIBLE

        binding.resultCard.alpha = 0f
        binding.resultCard.translationY = 40f
        binding.resultCard.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(400)
            .setInterpolator(OvershootInterpolator())
            .start()
    }

    private fun setCauldronColor(hexColor: String) {
        val color = Color.parseColor(hexColor)
        binding.ivCauldron.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY)
        binding.cauldronGlow.setBackgroundColor(Color.parseColor(hexColor))
    }

    private fun animateSuccess(colorHex: String) {
        setCauldronColor(colorHex)
        val scaleX = ObjectAnimator.ofFloat(binding.ivCauldron, "scaleX", 1f, 1.15f, 1f)
        val scaleY = ObjectAnimator.ofFloat(binding.ivCauldron, "scaleY", 1f, 1.15f, 1f)
        val glowAlpha = ObjectAnimator.ofFloat(binding.cauldronGlow, "alpha", 0.4f, 1f, 0.4f)

        val set = AnimatorSet()
        set.playTogether(scaleX, scaleY, glowAlpha)
        set.duration = 700
        set.start()
    }

    private fun animateFail() {
        setCauldronColor("#808080")
        val shake = ObjectAnimator.ofFloat(
            binding.ivCauldron, "translationX",
            0f, -20f, 20f, -15f, 15f, -10f, 10f, 0f
        )
        shake.duration = 500
        shake.start()
    }

    private fun animateBounce(view: View) {
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.85f, 1f)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.85f, 1f)
        val set = AnimatorSet()
        set.playTogether(scaleX, scaleY)
        set.duration = 200
        set.interpolator = OvershootInterpolator()
        set.start()
    }

    private fun animateCauldronBubble() {
        val translateY = ObjectAnimator.ofFloat(binding.ivCauldron, "translationY", 0f, -6f, 0f)
        translateY.duration = 250
        translateY.start()
    }

    private fun saveLastBrew(potionName: String) {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LAST_BREW, potionName).apply()
    }

    private fun loadLastBrew() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val last = prefs.getString(KEY_LAST_BREW, "None")
        binding.tvLastBrew.text = "⚗ Last Successful Brew: $last"
    }
}
package com.shiftspend.app

import android.content.Intent
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.shiftspend.app.data.PreferencesManager
import com.shiftspend.app.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var prefs: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. MUST be called before super.onCreate
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = PreferencesManager(this)

        // Reset initial states for the custom car-and-wallet logo animation
        binding.ivSplashLogo.alpha = 0f
        binding.ivSplashLogo.scaleX = 0.5f
        binding.ivSplashLogo.scaleY = 0.5f
        binding.tvSplashName.alpha = 0f

        // 2. Animate logo (Fade + Scale In)
        binding.ivSplashLogo.animate()
            .alpha(1f)
            .scaleX(1.0f)
            .scaleY(1.0f)
            .setDuration(800)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()

        // 3. Animate App Name (Slight delay for impact)
        binding.tvSplashName.animate()
            .alpha(1f)
            .setStartDelay(400)
            .setDuration(600)
            .start()

        // 4. Conditional Navigation
        binding.root.postDelayed({
            navigateToNextScreen()
        }, 2000)
    }

    private fun navigateToNextScreen() {
        val destination = if (prefs.isLoggedIn) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, LoginActivity::class.java)
        }

        startActivity(destination)
        overridePendingTransition(R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
}
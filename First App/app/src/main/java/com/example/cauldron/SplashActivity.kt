package com.example.cauldron

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import androidx.appcompat.app.AppCompatActivity
// Updated binding import to match the new package name
import com.example.cauldron.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Fade + scale animation on the logo
        val fadeIn = AlphaAnimation(0f, 1f).apply { duration = 1200 }
        val scaleUp = ScaleAnimation(
            0.7f, 1f, 0.7f, 1f,
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f
        ).apply { duration = 1200 }

        val animSet = AnimationSet(true).apply {
            addAnimation(fadeIn)
            addAnimation(scaleUp)
            fillAfter = true
        }

        // Make sure these IDs match your activity_splash.xml file
        binding.ivSplashLogo.startAnimation(animSet)
        binding.tvSplashTitle.startAnimation(fadeIn)
        binding.tvSplashTagline.startAnimation(fadeIn)

        // After 2.5 seconds, go to MainActivity
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            // Apply a smooth cross-fade transition
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, 2500)
    }
}
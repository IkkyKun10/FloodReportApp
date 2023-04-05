package com.riezki.floodreportapp.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.riezki.floodreportapp.MainActivity
import com.riezki.floodreportapp.R
import com.riezki.floodreportapp.ui.auth.AuthActivity

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler(Looper.getMainLooper()).postDelayed({
            Intent(this, AuthActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }, 3000)
    }

}
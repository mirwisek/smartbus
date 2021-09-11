package com.fyp.smartbus.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.fyp.smartbus.R
import com.fyp.smartbus.utils.switchActivity

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.splash_screen)

        Handler(Looper.getMainLooper()).postDelayed({
            switchActivity(RegistrationActivity::class.java)
        }, 2000L)
    }
}
package com.codingschool.ideabase

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val homeIntent = Intent(this@SplashScreen, MainActivity::class.java)
            startActivity(homeIntent)
            finish()
    }
}
package com.example.knowing_simple.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.knowing_simple.R
import com.example.knowing_simple.ui.main.MainActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // 스플래시 화면을 2초 동안 보여준 후 MainActivity로 전환
        Handler(Looper.getMainLooper()).postDelayed({
            // MainActivity로 이동
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            // SplashActivity 종료
            finish()
        }, 2000) // 2000ms = 2초
    }
}
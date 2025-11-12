package com.example.turnofacil

import android.content.Intent
import android.os.Bundle
import android.widget.Button

class LoginActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val buttonLogin: Button = findViewById(R.id.buttonLogin)

        buttonLogin.setOnClickListener {
            // Lógica de inicio de sesión aquí
            val intent = Intent(this, AdminActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
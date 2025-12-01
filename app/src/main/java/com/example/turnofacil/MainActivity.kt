package com.example.turnofacil

import android.content.Intent
import android.os.Bundle
import android.widget.Button

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonEmployee: Button = findViewById(R.id.buttonLogin)
        val buttonCustomer: Button = findViewById(R.id.buttonCustomer)

        buttonCustomer.setOnClickListener {
            val intent = Intent(this, CustomerWelcomeActivity::class.java)
            startActivity(intent)
        }

        buttonEmployee.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
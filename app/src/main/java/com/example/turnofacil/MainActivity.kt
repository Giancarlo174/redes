package com.example.turnofacil

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import java.util.Locale

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonEmployee: Button = findViewById(R.id.buttonLogin)
        val buttonChangeLanguage: ImageButton = findViewById(R.id.buttonChangeLanguage)

        buttonCustomer.setOnClickListener {
            val intent = Intent(this, CustomerActivity::class.java)
            startActivity(intent)
        }

        buttonEmployee.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        buttonChangeLanguage.setOnClickListener {
            val currentLang = resources.configuration.locales.get(0).language
            val newLang = if (currentLang == "es") "en" else "es"
            setLocale(newLang)
        }
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
        recreate()
    }
}
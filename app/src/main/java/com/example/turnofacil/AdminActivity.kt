package com.example.turnofacil

import android.os.Bundle

class AdminActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
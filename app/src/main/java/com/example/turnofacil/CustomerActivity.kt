package com.example.turnofacil

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CustomerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewTurns)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val turns = listOf(
            Turn("A01", "En atención", isAttending = true),
            Turn("B02", "En espera"),
            Turn("C03", "En espera"),
            Turn("D04", "En espera")
        )

        val adapter = TurnAdapter(turns)
        recyclerView.adapter = adapter
    }
}

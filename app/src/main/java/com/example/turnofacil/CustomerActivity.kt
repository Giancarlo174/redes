package com.example.turnofacil

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CustomerActivity : AppCompatActivity() {
    
    private var userId: Int = -1
    private var userName: String = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer)

        // Obtener datos del intent
        userId = intent.getIntExtra("USER_ID", -1)
        userName = intent.getStringExtra("USER_NAME") ?: "Cliente"
        
        // Actualizar el título con el nombre del usuario
        val titleTextView: TextView = findViewById(R.id.textView)
        titleTextView.text = "Hola, $userName"
        
        // Configurar botón de salir
        val fabExit: FloatingActionButton = findViewById(R.id.floatingActionButton2)
        fabExit.setOnClickListener {
            finish()
        }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewTurns)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Datos de ejemplo - en el futuro se cargarán desde la API
        val turns = listOf(
            Turn("A01", "En atención", isAttending = true),
            Turn("B02", "En espera"),
            Turn("C03", "En espera"),
            Turn("D04", "En espera")
        )

        val adapter = TurnAdapter(turns)
        recyclerView.adapter = adapter
        
        if (userId != -1) {
            Toast.makeText(this, "Sesión iniciada como: $userName", Toast.LENGTH_SHORT).show()
        }
    }
}

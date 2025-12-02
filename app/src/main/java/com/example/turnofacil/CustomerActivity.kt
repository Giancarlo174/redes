package com.example.turnofacil

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CustomerActivity : AppCompatActivity() {
    
    private var userId: Int = -1
    private var userName: String = ""
    private var userTurnNumber: String? = null
    
    private lateinit var titleTextView: TextView
    private lateinit var textViewTurnNumber: TextView
    private lateinit var textViewYourTurn: TextView
    private lateinit var textViewInfoContent: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var turnAdapter: TurnAdapter
    private val turnsList = mutableListOf<Turn>()
    
    private val handler = Handler(Looper.getMainLooper())
    private val refreshInterval = 5000L // 5 segundos
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer)

        // Obtener datos del intent
        userId = intent.getIntExtra("USER_ID", -1)
        userName = intent.getStringExtra("USER_NAME") ?: "Cliente"
        userTurnNumber = intent.getStringExtra("turno_numero")
        
        initViews()
        setupRecyclerView()
        loadTurnsData()
        startAutoRefresh()
        
        if (userId != -1) {
            Toast.makeText(this, "Sesión iniciada como: $userName", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun initViews() {
        titleTextView = findViewById(R.id.textView)
        titleTextView.text = "Bienvenido, $userName!"
        
        textViewTurnNumber = findViewById(R.id.textViewTurnNumber)
        textViewYourTurn = findViewById(R.id.textViewYourTurn)
        textViewInfoContent = findViewById(R.id.textView2)
        recyclerView = findViewById(R.id.recyclerViewTurns)
        
        val fabExit: FloatingActionButton = findViewById(R.id.floatingActionButton2)
        fabExit.setOnClickListener { finish() }
    }
    
    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        turnAdapter = TurnAdapter(turnsList)
        recyclerView.adapter = turnAdapter
    }
    
    private fun loadTurnsData() {
        if (userId == -1) {
            Toast.makeText(this, "Error: Usuario no identificado", Toast.LENGTH_SHORT).show()
            return
        }
        
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    ApiClient.get("/turnos/list_user.php?id_usuario=$userId")
                }
                
                if (response.getBoolean("success")) {
                    val data = response.getJSONObject("data")
                    val turnosArray = data.getJSONArray("turnos")
                    val turnoEnAtencion = if (data.isNull("turno_en_atencion")) null else data.getString("turno_en_atencion")
                    val posicionEnCola = data.getInt("posicion_en_cola")
                    
                    // Limpiar y agregar turnos del usuario
                    turnsList.clear()
                    
                    var userSucursalId = -1
                    
                    for (i in 0 until turnosArray.length()) {
                        val turno = turnosArray.getJSONObject(i)
                        val numeroTurno = turno.getString("numero_turno")
                        val estado = turno.getString("estado")
                        
                        if (i == 0) {
                            userSucursalId = turno.optInt("id_sucursal", -1)
                        }
                        
                        val statusText = when (estado) {
                            "pendiente" -> "En espera"
                            "completado" -> "Atendido"
                            "cancelado" -> "Cancelado"
                            else -> estado
                        }
                        
                        turnsList.add(Turn(
                            id = turno.getInt("id"),
                            turnNumber = numeroTurno,
                            status = statusText,
                            isAttending = false,
                            createdAt = turno.getString("fecha_creacion")
                        ))
                    }
                    
                    // Actualizar el turno principal (el más reciente del usuario)
                    if (turnsList.isNotEmpty()) {
                        val miTurno = turnsList[0]
                        textViewTurnNumber.text = miTurno.turnNumber
                        
                        when (miTurno.status) {
                            "En espera" -> {
                                textViewInfoContent.text = "Hay $posicionEnCola turno(s) antes que el tuyo"
                            }
                            "Atendido" -> {
                                textViewInfoContent.text = "Tu turno ha sido atendido"
                            }
                            "Cancelado" -> {
                                textViewInfoContent.text = "Tu turno ha sido cancelado"
                            }
                        }
                    } else {
                        textViewTurnNumber.text = "--"
                        textViewInfoContent.text = "No tienes turnos activos"
                    }
                    
                    // Manejar el turno actualmente en atención
                    if (turnoEnAtencion != null) {
                        // Buscar si ya existe en la lista
                        val existingIndex = turnsList.indexOfFirst { it.turnNumber == turnoEnAtencion }
                        
                        if (existingIndex != -1) {
                            // Si existe, lo movemos al principio y actualizamos su estado
                            val existingTurn = turnsList.removeAt(existingIndex)
                            val updatedTurn = existingTurn.copy(
                                status = "En atención",
                                isAttending = true
                            )
                            turnsList.add(0, updatedTurn)
                        } else {
                            // Si no existe, lo agregamos al principio
                            turnsList.add(0, Turn(
                                id = 0,
                                turnNumber = turnoEnAtencion,
                                status = "En atención",
                                isAttending = true
                            ))
                        }
                    }
                    
                    // Agregar turnos pendientes de otros usuarios para contexto
                    loadOtherTurns(userSucursalId)
                    
                    turnAdapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                Log.e("CustomerActivity", "Error loading turns: ${e.message}")
                Toast.makeText(this@CustomerActivity, "Error al cargar turnos", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private suspend fun loadOtherTurns(sucursalId: Int) {
        try {
            var url = "/turnos/list.php?estado=pendiente"
            if (sucursalId != -1) {
                url += "&id_sucursal=$sucursalId"
            }
            
            val response = withContext(Dispatchers.IO) {
                ApiClient.get(url)
            }
            
            if (response.getBoolean("success")) {
                val turnosArray = response.getJSONArray("data")
                
                // Agregar solo algunos turnos pendientes de otros (máximo 3)
                val maxOtherTurns = 3
                var added = 0
                
                for (i in 0 until turnosArray.length()) {
                    if (added >= maxOtherTurns) break
                    
                    val turno = turnosArray.getJSONObject(i)
                    val numeroTurno = turno.getString("numero_turno")
                    
                    // No agregar si ya está en la lista (es del usuario)
                    if (turnsList.any { it.turnNumber == numeroTurno }) {
                        continue
                    }
                    
                    turnsList.add(Turn(
                        id = turno.getInt("id"),
                        turnNumber = numeroTurno,
                        status = "En espera",
                        isAttending = false,
                        createdAt = turno.getString("fecha_creacion")
                    ))
                    
                    added++
                }
            }
        } catch (e: Exception) {
            Log.e("CustomerActivity", "Error loading other turns: ${e.message}")
        }
    }
    
    private fun startAutoRefresh() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                loadTurnsData()
                handler.postDelayed(this, refreshInterval)
            }
        }, refreshInterval)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}

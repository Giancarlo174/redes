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
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class AdminActivity : AppCompatActivity() {

    private lateinit var cardCurrentTurn: CardView
    private lateinit var textViewCurrentTurn: TextView
    private lateinit var countWaiting: TextView
    private lateinit var countAttended: TextView
    private lateinit var countCanceled: TextView
    private lateinit var countTotal: TextView
    private lateinit var buttonCallNext: Button
    private lateinit var recyclerViewHistory: RecyclerView
    private lateinit var fabExit: FloatingActionButton

    private val historyList = mutableListOf<TurnHistoryItem>()
    private lateinit var historyAdapter: TurnHistoryAdapter
    private var currentTurnId: Int? = null
    private var currentTurnNumber: String? = null

    private val handler = Handler(Looper.getMainLooper())
    private val refreshInterval = 3000L // 3 segundos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        initViews()
        setupRecyclerView()
        setupListeners()
        loadData()
        startAutoRefresh()
    }

    private fun initViews() {
        cardCurrentTurn = findViewById(R.id.cardCurrentTurn)
        textViewCurrentTurn = findViewById(R.id.textViewCurrentTurn)
        countWaiting = findViewById(R.id.countWaiting)
        countAttended = findViewById(R.id.countAttended)
        countCanceled = findViewById(R.id.countCanceled)
        countTotal = findViewById(R.id.countTotal)
        buttonCallNext = findViewById(R.id.buttonCallNext)
        recyclerViewHistory = findViewById(R.id.recyclerViewHistory)
        fabExit = findViewById(R.id.floatingActionButton3)
    }

    private fun setupRecyclerView() {
        recyclerViewHistory.layoutManager = LinearLayoutManager(this)
        historyAdapter = TurnHistoryAdapter(historyList, object : TurnHistoryAdapter.OnTurnActionListener {
            override fun onCancelTurn(position: Int) {
                cancelTurn(position)
            }
        })
        recyclerViewHistory.adapter = historyAdapter
    }

    private fun setupListeners() {
        buttonCallNext.setOnClickListener { callNextTurn() }
        fabExit.setOnClickListener { finish() }
    }

    private fun loadData() {
        loadStatistics()
        loadTurnsList()
    }

    private fun loadStatistics() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = ApiClient.get("/turnos/stats.php")
                
                if (response.getBoolean("success")) {
                    val data = response.getJSONObject("data")
                    countWaiting.text = data.getInt("en_espera").toString()
                    countAttended.text = data.getInt("atendidos").toString()
                    countCanceled.text = data.getInt("cancelados").toString()
                    countTotal.text = data.getInt("total").toString()
                }
            } catch (e: Exception) {
                Log.e("AdminActivity", "Error loading stats: ${e.message}")
            }
        }
    }

    private fun loadTurnsList() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = ApiClient.get("/turnos/list.php")
                
                if (response.getBoolean("success")) {
                    val turnosArray = response.getJSONArray("data")
                    historyList.clear()
                    
                    for (i in 0 until turnosArray.length()) {
                        val turno = turnosArray.getJSONObject(i)
                        val status = when (turno.getString("estado")) {
                            "pendiente" -> "En espera"
                            "completado" -> "Atendido"
                            "cancelado" -> "Cancelado"
                            else -> turno.getString("estado")
                        }
                        
                        val time = formatTime(turno.getString("fecha_creacion"))
                        
                        historyList.add(TurnHistoryItem(
                            id = turno.getInt("id"),
                            turnNumber = turno.getString("numero_turno"),
                            time = time,
                            status = status,
                            userName = turno.getString("nombre")
                        ))
                    }
                    
                    historyAdapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                Log.e("AdminActivity", "Error loading turns: ${e.message}")
            }
        }
    }

    private fun callNextTurn() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Si hay un turno actual, marcarlo como completado
                if (currentTurnId != null) {
                    updateTurnStatus(currentTurnId!!, "completado")
                }
                
                // Buscar el primer turno pendiente
                val pendingTurn = historyList.find { it.status == "En espera" }
                
                if (pendingTurn != null) {
                    currentTurnId = pendingTurn.id
                    currentTurnNumber = pendingTurn.turnNumber
                    
                    // Actualizar a completado
                    updateTurnStatus(currentTurnId!!, "completado")
                    
                    // Mostrar la tarjeta de turno actual
                    cardCurrentTurn.visibility = View.VISIBLE
                    textViewCurrentTurn.text = currentTurnNumber
                    
                    Toast.makeText(this@AdminActivity, "Llamando turno $currentTurnNumber", Toast.LENGTH_SHORT).show()
                    
                    // Recargar datos
                    loadData()
                } else {
                    // No hay más turnos en espera
                    currentTurnId = null
                    currentTurnNumber = null
                    cardCurrentTurn.visibility = View.GONE
                    Toast.makeText(this@AdminActivity, "No hay turnos en espera", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("AdminActivity", "Error calling next turn: ${e.message}")
                Toast.makeText(this@AdminActivity, "Error al llamar turno", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cancelTurn(position: Int) {
        val item = historyList[position]
        if (item.status == "En espera") {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    updateTurnStatus(item.id, "cancelado")
                    Toast.makeText(this@AdminActivity, "Turno ${item.turnNumber} cancelado", Toast.LENGTH_SHORT).show()
                    loadData()
                } catch (e: Exception) {
                    Log.e("AdminActivity", "Error canceling turn: ${e.message}")
                    Toast.makeText(this@AdminActivity, "Error al cancelar turno", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun updateTurnStatus(turnId: Int, newStatus: String) {
        withContext(Dispatchers.IO) {
            val jsonBody = JSONObject().apply {
                put("id", turnId)
                put("estado", newStatus)
            }
            ApiClient.put("/turnos/update.php", jsonBody)
        }
    }

    private fun formatTime(isoDateTime: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("hh:mm a", Locale("es", "ES"))
            val date = inputFormat.parse(isoDateTime)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            isoDateTime
        }
    }

    private fun startAutoRefresh() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                loadData()
                handler.postDelayed(this, refreshInterval)
            }
        }, refreshInterval)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    // Clase de datos para representar un turno en el historial
    data class TurnHistoryItem(
        val id: Int,
        val turnNumber: String,
        val time: String,
        var status: String,
        val userName: String = ""
    )
}

package com.example.turnofacil

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class CustomerWelcomeActivity : BaseActivity() {
    
    private lateinit var editTextNombre: TextInputEditText
    private lateinit var editTextCedula: TextInputEditText
    private lateinit var buttonTakeTurn: Button
    private lateinit var fabExit: FloatingActionButton
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_welcome)

        editTextNombre = findViewById(R.id.editTextNombre)
        editTextCedula = findViewById(R.id.editTextCedula)
        buttonTakeTurn = findViewById(R.id.buttonTakeTurn)
        fabExit = findViewById(R.id.fabExit)

        buttonTakeTurn.setOnClickListener {
            val nombre = editTextNombre.text.toString().trim()
            val cedula = editTextCedula.text.toString().trim()
            
            if (nombre.isEmpty() || cedula.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            crearTurno(nombre, cedula)
        }

        fabExit.setOnClickListener {
            finish()
        }
    }
    
    private fun crearTurno(nombre: String, cedula: String) {
        buttonTakeTurn.isEnabled = false
        
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val jsonBody = JSONObject().apply {
                    put("nombre", nombre)
                    put("cedula", cedula)
                }
                
                val response = ApiClient.post("/turnos/create.php", jsonBody)
                
                if (response.getBoolean("success")) {
                    val data = response.getJSONObject("data")
                    val numeroTurno = data.getString("numero_turno")
                    val usuarioId = data.getInt("usuario_id")
                    
                    Toast.makeText(
                        this@CustomerWelcomeActivity,
                        "Turno $numeroTurno generado exitosamente",
                        Toast.LENGTH_LONG
                    ).show()
                    
                    // Navegar a CustomerActivity con el turno
                    val intent = Intent(this@CustomerWelcomeActivity, CustomerActivity::class.java)
                    intent.putExtra("turno_numero", numeroTurno)
                    intent.putExtra("USER_NAME", nombre)
                    intent.putExtra("USER_ID", usuarioId)
                    startActivity(intent)
                    finish()
                } else {
                    val message = response.getString("message")
                    Toast.makeText(
                        this@CustomerWelcomeActivity,
                        message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@CustomerWelcomeActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                buttonTakeTurn.isEnabled = true
            }
        }
    }
}

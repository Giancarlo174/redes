package com.example.turnofacil

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class LoginActivity : BaseActivity() {
    
    private lateinit var buttonLogin: Button
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        buttonLogin = findViewById(R.id.buttonLogin)
        emailEditText = findViewById(R.id.editTextTextEmailAddress)
        passwordEditText = findViewById(R.id.editTextTextPassword)

        buttonLogin.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            performLogin(email, password)
        }
    }
    
    private fun performLogin(email: String, password: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Mostrar loading
                buttonLogin.isEnabled = false
                buttonLogin.text = "Iniciando sesión..."
                
                val jsonBody = JSONObject().apply {
                    put("email", email)
                    put("password", password)
                }
                
                val response = withContext(Dispatchers.IO) {
                    ApiClient.post("/auth/login.php", jsonBody)
                }
                
                if (response.getBoolean("success")) {
                    val data = response.getJSONObject("data")
                    val rol = data.getString("rol")
                    val nombre = data.getString("nombre")
                    val userId = data.getInt("id")
                    
                    Toast.makeText(this@LoginActivity, "Bienvenido $nombre", Toast.LENGTH_SHORT).show()
                    
                    // Redirigir según el rol
                    when (rol) {
                        "admin" -> {
                            val intent = Intent(this@LoginActivity, AdminActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        "cliente" -> {
                            val intent = Intent(this@LoginActivity, CustomerActivity::class.java)
                            intent.putExtra("USER_ID", userId)
                            intent.putExtra("USER_NAME", nombre)
                            startActivity(intent)
                            finish()
                        }
                        else -> {
                            Toast.makeText(this@LoginActivity, "Rol no reconocido", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    val message = response.getString("message")
                    Toast.makeText(this@LoginActivity, message, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Error de conexión: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                // Restaurar botón
                buttonLogin.isEnabled = true
                buttonLogin.text = "Iniciar Sesión"
            }
        }
    }
}

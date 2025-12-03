package com.example.turnofacil

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

object ApiClient {
    private const val BASE_URL = "http://10.0.2.2/redes/api" // Para emulador Android
    // Si usas dispositivo real, cambia por tu IP: "http://192.168.x.x/redes/api"

    suspend fun post(endpoint: String, jsonBody: JSONObject): JSONObject = withContext(Dispatchers.IO) {
        try {
            val url = URL("$BASE_URL$endpoint")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true
            connection.doInput = true

            val writer = OutputStreamWriter(connection.outputStream)
            writer.write(jsonBody.toString())
            writer.flush()
            writer.close()

            val responseCode = connection.responseCode
            val reader = BufferedReader(InputStreamReader(
                if (responseCode == HttpURLConnection.HTTP_OK) connection.inputStream
                else connection.errorStream
            ))
            val response = reader.readText()
            reader.close()

            Log.d("ApiClient", "Response: $response")
            JSONObject(response)
        } catch (e: Exception) {
            Log.e("ApiClient", "Error: ${e.message}")
            JSONObject().apply {
                put("success", false)
                put("message", "Error de conexión: ${e.message}")
            }
        }
    }

    suspend fun get(endpoint: String): JSONObject = withContext(Dispatchers.IO) {
        try {
            val url = URL("$BASE_URL$endpoint")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("Content-Type", "application/json")

            val responseCode = connection.responseCode
            val reader = BufferedReader(InputStreamReader(
                if (responseCode == HttpURLConnection.HTTP_OK) connection.inputStream
                else connection.errorStream
            ))
            val response = reader.readText()
            reader.close()

            Log.d("ApiClient", "Response: $response")
            JSONObject(response)
        } catch (e: Exception) {
            Log.e("ApiClient", "Error: ${e.message}")
            JSONObject().apply {
                put("success", false)
                put("message", "Error de conexión: ${e.message}")
            }
        }
    }

    suspend fun put(endpoint: String, jsonBody: JSONObject): JSONObject = withContext(Dispatchers.IO) {
        try {
            val url = URL("$BASE_URL$endpoint")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "PUT"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true
            connection.doInput = true

            val writer = OutputStreamWriter(connection.outputStream)
            writer.write(jsonBody.toString())
            writer.flush()
            writer.close()

            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val response = reader.readText()
            reader.close()

            Log.d("ApiClient", "Response: $response")
            JSONObject(response)
        } catch (e: Exception) {
            Log.e("ApiClient", "Error: ${e.message}")
            JSONObject().apply {
                put("success", false)
                put("message", "Error de conexión: ${e.message}")
            }
        }
    }
}

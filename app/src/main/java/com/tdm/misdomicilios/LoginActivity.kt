package com.tdm.misdomicilios

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var editTextUsername: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLogin: Button
    private lateinit var textViewRegister: TextView
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializar vistas
        editTextUsername = findViewById(R.id.editTextUsername)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonLogin = findViewById(R.id.buttonLogin)
        textViewRegister = findViewById(R.id.textViewRegister)

        // Inicializar la base de datos
        dbHelper = DatabaseHelper(this)

        // Acción del botón de iniciar sesión
        buttonLogin.setOnClickListener {
            val username = editTextUsername.text.toString()
            val password = editTextPassword.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                if (dbHelper.autenticarUsuario(username, password)) {
                    Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                    navegarAMapsActivity(username) // Navegar al mapa
                } else {
                    Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        // Acción para ir a la actividad de registro
        textViewRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun navegarAMapsActivity(username: String) {
        // Obtener el ID del usuario autenticado
        val cursor = dbHelper.obtenerUsuarioPorId(dbHelper.obtenerUsuarioPorUsername(username))
        if (cursor.moveToFirst()) {
            val userId = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            cursor.close()

            val intent = Intent(this, MapsActivity::class.java).apply {
                putExtra("USER_ID", userId) // Pasar el ID del usuario a la actividad MapsActivity
            }
            startActivity(intent)
            finish()
        } else {
            cursor.close()
            Toast.makeText(this, "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show()
        }
    }
}

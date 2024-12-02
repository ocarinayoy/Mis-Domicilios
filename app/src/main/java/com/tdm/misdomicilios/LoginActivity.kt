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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializar vistas
        editTextUsername = findViewById(R.id.editTextUsername)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonLogin = findViewById(R.id.buttonLogin)
        textViewRegister = findViewById(R.id.textViewRegister)

        // Configurar el evento del botón de login
        buttonLogin.setOnClickListener {
            val username = editTextUsername.text.toString()
            val password = editTextPassword.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                // Lógica para autenticar al usuario
                autenticarUsuario(username, password)
            } else {
                Toast.makeText(this, "Por favor ingresa usuario y contraseña", Toast.LENGTH_SHORT).show()
            }
        }

        // Configurar evento para ir a la actividad de registro (si es necesario)
        textViewRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    // Método para autenticar al usuario
    private fun autenticarUsuario(username: String, password: String) {
        // Lógica de autenticación (esto puede ser con una base de datos local o API)
        // Para este ejemplo, se usa una autenticación simple (como prueba)
        if (username == "admin" && password == "admin") {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
            finish()  // Cerrar la actividad de login
        } else {
            Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
        }
    }
}

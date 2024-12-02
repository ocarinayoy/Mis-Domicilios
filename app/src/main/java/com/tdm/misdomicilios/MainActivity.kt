package com.tdm.misdomicilios

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.ActionBarDrawerToggle

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var drawerLayout: DrawerLayout
    private val LOCATION_PERMISSION_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configurar DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)

        // Configurar el ActionBarDrawerToggle (botón de hamburguesa)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.open_drawer, R.string.close_drawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Asegurarse de que el DrawerLayout permita el gesto de deslizar
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

        // Solicitar permisos de ubicación si no están otorgados
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permiso otorgado, iniciar MapsActivity
            iniciarMapsActivity()
        }

        // Inicializar el helper de la base de datos
        dbHelper = DatabaseHelper(this)

        // Insertar un domicilio de prueba
        val id = dbHelper.insertarDomicilio(
            "Casa de Juan",
            "Calle Ficticia 123",
            19.432608,
            -99.133209
        )
        Toast.makeText(this, "Domicilio insertado con ID: $id", Toast.LENGTH_LONG).show()

        // Leer los domicilios y mostrarlos
        val domicilios = dbHelper.obtenerDomicilios()
        if (domicilios.isNotEmpty()) {
            for (domicilio in domicilios) {
                val mensaje = "Domicilio: ${domicilio["nombre"]} - ${domicilio["direccion"]}"
                Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "No hay domicilios guardados", Toast.LENGTH_LONG).show()
        }

        // Actualizar un domicilio (suponiendo que el ID del domicilio insertado es 1)
        val filasAfectadas = dbHelper.actualizarDomicilio(
            1, // Cambiar por el ID correcto si es necesario
            "Casa de Juan Actualizada",
            "Calle Ficticia 123 Actualizada",
            19.432609,
            -99.133210
        )
        Toast.makeText(this, "Filas actualizadas: $filasAfectadas", Toast.LENGTH_LONG).show()

        // Eliminar un domicilio (suponiendo que el ID es 1)
        val filasEliminadas = dbHelper.eliminarDomicilio(1)
        Toast.makeText(this, "Filas eliminadas: $filasEliminadas", Toast.LENGTH_LONG).show()
    }

    // Manejar la respuesta del usuario para los permisos
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso otorgado, iniciar MapsActivity
                iniciarMapsActivity()
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Método para iniciar MapsActivity
    private fun iniciarMapsActivity() {
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }
}

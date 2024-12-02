package com.tdm.misdomicilios

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.navigation.NavigationView

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var textViewLatitud: TextView
    private lateinit var textViewLongitud: TextView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Configurar DrawerLayout y NavigationView
        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)

        // Configurar el ActionBarDrawerToggle
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.open_drawer, R.string.close_drawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Inicializar vistas y base de datos
        textViewLatitud = findViewById(R.id.textViewLatitud)
        textViewLongitud = findViewById(R.id.textViewLongitud)
        dbHelper = DatabaseHelper(this)

        // Configurar el mapa
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Configurar acción del botón flotante
        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabGuardarDomicilio)
            .setOnClickListener { mostrarDialogoDomicilio() }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val inicial = LatLng(19.432608, -99.133209)
        mMap.addMarker(MarkerOptions().position(inicial).title("Inicio"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(inicial, 12f))
        actualizarLatLng(inicial)

        mMap.setOnMapClickListener { latLng ->
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(latLng).title("Nuevo domicilio"))
            actualizarLatLng(latLng)
        }
    }

    private fun actualizarLatLng(latLng: LatLng) {
        textViewLatitud.text = "Latitud: ${latLng.latitude}"
        textViewLongitud.text = "Longitud: ${latLng.longitude}"
    }

    private fun mostrarDialogoDomicilio() {
        val builder = android.app.AlertDialog.Builder(this)
        val dialogLayout = layoutInflater.inflate(R.layout.dialog_domicilio, null)
        val nombreEditText = dialogLayout.findViewById<EditText>(R.id.nombreDomicilio)
        val direccionEditText = dialogLayout.findViewById<EditText>(R.id.direccionDomicilio)

        builder.setView(dialogLayout)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = nombreEditText.text.toString()
                val direccion = direccionEditText.text.toString()
                val id = dbHelper.insertarDomicilio(
                    nombre,
                    direccion,
                    mMap.cameraPosition.target.latitude,
                    mMap.cameraPosition.target.longitude
                )
                Toast.makeText(this, "Domicilio guardado con ID: $id", Toast.LENGTH_LONG).show()
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(androidx.core.view.GravityCompat.START)) {
            drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}

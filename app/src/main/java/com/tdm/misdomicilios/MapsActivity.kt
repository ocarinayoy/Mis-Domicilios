package com.tdm.misdomicilios

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.tdm.misdomicilios.databinding.ActivityMapsBinding
import com.tdm.misdomicilios.DatabaseHelper
import android.app.AlertDialog


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var textViewLatitud: TextView
    private lateinit var textViewLongitud: TextView
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializamos los TextViews
        textViewLatitud = findViewById(R.id.textViewLatitud)
        textViewLongitud = findViewById(R.id.textViewLongitud)

        // Inicializar dbHelper
        dbHelper = DatabaseHelper(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Acción del FAB para guardar el domicilio
        binding.fabGuardarDomicilio.setOnClickListener {
            // Mostrar cuadro de diálogo para ingresar los detalles del domicilio
            val builder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.dialog_domicilio, null)

            val nombreEditText = dialogLayout.findViewById<EditText>(R.id.nombreDomicilio)
            val direccionEditText = dialogLayout.findViewById<EditText>(R.id.direccionDomicilio)

            builder.setView(dialogLayout)
                .setPositiveButton("Guardar") { dialog, id ->
                    val nombre = nombreEditText.text.toString()
                    val direccion = direccionEditText.text.toString()

                    // Guardar en la base de datos
                    val idDomicilio = dbHelper.insertarDomicilio(
                        nombre,
                        direccion,
                        mMap.cameraPosition.target.latitude,
                        mMap.cameraPosition.target.longitude
                    )

                    Toast.makeText(this, "Domicilio guardado con ID: $idDomicilio", Toast.LENGTH_LONG).show()
                }
                .setNegativeButton("Cancelar") { dialog, id ->
                    dialog.dismiss()
                }

            builder.show()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Coordenadas de la Ciudad de México como ejemplo inicial
        val ciudadMexico = LatLng(19.432608, -99.133209)
        mMap.addMarker(MarkerOptions().position(ciudadMexico).title("Ciudad de México"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ciudadMexico, 12f))

        // Actualizar los TextViews con las coordenadas iniciales
        actualizarLatLng(ciudadMexico)

        mMap.setOnMapClickListener { latLng ->
            // Actualizar los TextViews con las nuevas coordenadas
            actualizarLatLng(latLng)

            // Limpiar el marcador anterior
            mMap.clear()

            // Colocar el nuevo marcador en la nueva ubicación
            val marcador = MarkerOptions().position(latLng).title("Nuevo domicilio")
            mMap.addMarker(marcador)
        }
    }

    // Metodo para actualizar los TextViews con latitud y longitud
    private fun actualizarLatLng(latLng: LatLng) {
        textViewLatitud.text = "Latitud: ${latLng.latitude}"
        textViewLongitud.text = "Longitud: ${latLng.longitude}"
    }
}

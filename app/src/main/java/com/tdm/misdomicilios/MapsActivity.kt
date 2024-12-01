package com.tdm.misdomicilios

import android.app.AlertDialog
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.tdm.misdomicilios.databinding.ActivityMapsBinding
import com.tdm.misdomicilios.DatabaseHelper

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var dbHelper: DatabaseHelper  // Declara dbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar el helper de la base de datos
        dbHelper = DatabaseHelper(this)  // Inicializa dbHelper

        // Obtener el SupportMapFragment y notificar cuando el mapa esté listo para ser usado
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Cambiar las coordenadas a Ciudad de México
        val ciudadMexico = LatLng(19.432608, -99.133209)  // Coordenadas de la Ciudad de México
        mMap.addMarker(MarkerOptions().position(ciudadMexico).title("Ciudad de México"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ciudadMexico, 12f))  // Ajustar el zoom

        // Agregar listener para detectar clics en el mapa
        mMap.setOnMapClickListener { latLng ->
            // Mostrar el cuadro de diálogo para ingresar los datos del domicilio
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
                        latLng.latitude,
                        latLng.longitude
                    )
                    Toast.makeText(this, "Domicilio guardado con ID: $idDomicilio", Toast.LENGTH_LONG).show()

                    // Colocar el marcador en el mapa
                    val marcador = MarkerOptions().position(latLng).title(nombre)
                    mMap.addMarker(marcador)
                }
                .setNegativeButton("Cancelar") { dialog, id ->
                    dialog.dismiss()
                }

            builder.show()
        }
    }
}

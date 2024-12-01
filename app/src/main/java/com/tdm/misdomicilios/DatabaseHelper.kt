package com.tdm.misdomicilios

import android.content.Context
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    companion object {
        const val DATABASE_NAME = "AppDatabase.db"
        const val DATABASE_VERSION = 1
    }

    // Crear la tabla
    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE Domicilios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT,
                direccion TEXT,
                latitud REAL,
                longitud REAL
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    // Actualizar la base de datos (si es necesario)
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS Domicilios")
        onCreate(db)
    }

    // Insertar un domicilio
    fun insertarDomicilio(nombre: String, direccion: String, latitud: Double, longitud: Double): Long {
        val db = writableDatabase // Este es el método heredado que te da el acceso a la base de datos en modo escritura
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("direccion", direccion)
            put("latitud", latitud)
            put("longitud", longitud)
        }
        return db.insert("Domicilios", null, values)
    }

    // Leer todos los domicilios
    fun obtenerDomicilios(): List<Map<String, Any>> {
        val db = readableDatabase // Este es el método heredado que te da el acceso a la base de datos en modo lectura
        val cursor: Cursor = db.rawQuery("SELECT * FROM Domicilios", null)
        val domicilios = mutableListOf<Map<String, Any>>()

        if (cursor.moveToFirst()) {
            do {
                val domicilio = mapOf(
                    "id" to cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    "nombre" to cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    "direccion" to cursor.getString(cursor.getColumnIndexOrThrow("direccion")),
                    "latitud" to cursor.getDouble(cursor.getColumnIndexOrThrow("latitud")),
                    "longitud" to cursor.getDouble(cursor.getColumnIndexOrThrow("longitud"))
                )
                domicilios.add(domicilio)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return domicilios
    }

    // Actualizar un domicilio
    fun actualizarDomicilio(id: Int, nombre: String, direccion: String, latitud: Double, longitud: Double): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("direccion", direccion)
            put("latitud", latitud)
            put("longitud", longitud)
        }
        return db.update("Domicilios", values, "id = ?", arrayOf(id.toString()))
    }

    // Eliminar un domicilio
    fun eliminarDomicilio(id: Int): Int {
        val db = writableDatabase
        return db.delete("Domicilios", "id = ?", arrayOf(id.toString()))
    }
}

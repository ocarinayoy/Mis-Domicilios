package com.tdm.misdomicilios

import android.content.ContentValues
import android.content.Context
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

    override fun onCreate(db: SQLiteDatabase) {
        val createTableUsuarios = """
            CREATE TABLE usuarios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE,
                email TEXT,
                password TEXT
            )
        """.trimIndent()

        val createTableDomicilios = """
            CREATE TABLE domicilios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT,
                direccion TEXT,
                latitud REAL,
                longitud REAL
            )
        """.trimIndent()

        val createTableDomiciliosUsuarios = """
            CREATE TABLE domicilios_usuarios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                usuario_id INTEGER,
                domicilio_id INTEGER,
                FOREIGN KEY(usuario_id) REFERENCES usuarios(id),
                FOREIGN KEY(domicilio_id) REFERENCES domicilios(id)
            )
        """.trimIndent()

        db.execSQL(createTableUsuarios)
        db.execSQL(createTableDomicilios)
        db.execSQL(createTableDomiciliosUsuarios)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        db.execSQL("DROP TABLE IF EXISTS domicilios")
        db.execSQL("DROP TABLE IF EXISTS domicilios_usuarios")
        onCreate(db)
    }

    // CRUD para tabla usuarios
    fun registrarUsuario(username: String, email: String, password: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("username", username)
            put("email", email)
            put("password", password)
        }
        return db.insert("usuarios", null, values)
    }

    fun existeUsername(username: String): Boolean {
        val db = readableDatabase
        val query = "SELECT * FROM usuarios WHERE username = ?"
        val cursor = db.rawQuery(query, arrayOf(username))
        val existe = cursor.count > 0
        cursor.close()
        return existe
    }

    fun obtenerUsuarioPorId(id: Int): Cursor {
        val db = readableDatabase
        return db.rawQuery("SELECT * FROM usuarios WHERE id = ?", arrayOf(id.toString()))
    }

    fun obtenerTodosLosUsuarios(): Cursor {
        val db = readableDatabase
        return db.rawQuery("SELECT * FROM usuarios", null)
    }

    fun actualizarUsuario(id: Int, username: String, email: String, password: String): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("username", username)
            put("email", email)
            put("password", password)
        }
        return db.update("usuarios", values, "id = ?", arrayOf(id.toString()))
    }

    fun eliminarUsuario(id: Int): Int {
        val db = writableDatabase
        return db.delete("usuarios", "id = ?", arrayOf(id.toString()))
    }

    // CRUD para tabla domicilios
    fun insertarDomicilio(nombre: String, direccion: String, latitud: Double, longitud: Double): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("direccion", direccion)
            put("latitud", latitud)
            put("longitud", longitud)
        }
        return db.insert("domicilios", null, values)
    }

    fun obtenerDomicilioPorId(id: Int): Cursor {
        val db = readableDatabase
        return db.rawQuery("SELECT * FROM domicilios WHERE id = ?", arrayOf(id.toString()))
    }

    fun obtenerTodosLosDomicilios(): Cursor {
        val db = readableDatabase
        return db.rawQuery("SELECT * FROM domicilios", null)
    }

    fun actualizarDomicilio(id: Int, nombre: String, direccion: String, latitud: Double, longitud: Double): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("direccion", direccion)
            put("latitud", latitud)
            put("longitud", longitud)
        }
        return db.update("domicilios", values, "id = ?", arrayOf(id.toString()))
    }

    fun eliminarDomicilio(id: Int): Int {
        val db = writableDatabase
        return db.delete("domicilios", "id = ?", arrayOf(id.toString()))
    }

    // CRUD para tabla domicilios_usuarios
    fun asociarDomicilioUsuario(usuarioId: Int, domicilioId: Int): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("usuario_id", usuarioId)
            put("domicilio_id", domicilioId)
        }
        return db.insert("domicilios_usuarios", null, values)
    }

    fun obtenerDomiciliosPorUsuario(usuarioId: Int): Cursor {
        val db = readableDatabase
        val query = """
            SELECT domicilios.* FROM domicilios
            INNER JOIN domicilios_usuarios ON domicilios.id = domicilios_usuarios.domicilio_id
            WHERE domicilios_usuarios.usuario_id = ?
        """
        return db.rawQuery(query, arrayOf(usuarioId.toString()))
    }

    fun eliminarAsociacionDomicilioUsuario(id: Int): Int {
        val db = writableDatabase
        return db.delete("domicilios_usuarios", "id = ?", arrayOf(id.toString()))
    }

    // Agregar a la clase DatabaseHelper
    fun autenticarUsuario(username: String, password: String): Boolean {
        val db = readableDatabase
        val query = """
        SELECT * FROM usuarios WHERE username = ? AND password = ?
    """
        val cursor = db.rawQuery(query, arrayOf(username, password))
        val autenticado = cursor.count > 0
        cursor.close()
        return autenticado
    }

    fun obtenerUsuarioPorUsername(username: String): Int {
        val db = readableDatabase
        val query = """
        SELECT id FROM usuarios WHERE username = ?
    """
        val cursor = db.rawQuery(query, arrayOf(username))
        var userId = -1
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
        }
        cursor.close()
        return userId
    }
}

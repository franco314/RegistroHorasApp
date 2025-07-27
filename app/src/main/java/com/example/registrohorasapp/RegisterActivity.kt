package com.example.registrohorasapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.registrohorasapp.retrofit.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : ComponentActivity() {

    private lateinit var etNuevoUsuario: EditText
    private lateinit var etNuevaContrasena: EditText
    private lateinit var etRepetirContrasena: EditText
    private lateinit var btnRegistrar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        RetrofitClient.init(applicationContext)

        etNuevoUsuario = findViewById(R.id.etNuevoUsuario)
        etNuevaContrasena = findViewById(R.id.etNuevaContrasena)
        etRepetirContrasena = findViewById(R.id.etRepetirContrasena)
        btnRegistrar = findViewById(R.id.btnRegistrar)

        btnRegistrar.setOnClickListener {
            val usuario = etNuevoUsuario.text.toString().trim()
            val contrasena = etNuevaContrasena.text.toString()
            val repetirContrasena = etRepetirContrasena.text.toString()

            // Validaciones
            if (usuario.isEmpty() || contrasena.isEmpty() || repetirContrasena.isEmpty()) {
                Toast.makeText(this, "Completá todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (contrasena != repetirContrasena) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                etRepetirContrasena.setText("")
                etRepetirContrasena.requestFocus()
                return@setOnClickListener
            }

            if (contrasena.length < 6) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitClient.apiService.register(usuario, contrasena)

                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@RegisterActivity, "Usuario creado con éxito", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this@RegisterActivity, "Error al crear usuario: ${response.code()}", Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@RegisterActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}

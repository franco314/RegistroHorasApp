package com.example.registrohorasapp

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.ComponentActivity

class ConfigurarFrancoActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configurar_franco)

        val radioGroup = findViewById<RadioGroup>(R.id.radioGroupFranco)
        val rbDomingo = findViewById<RadioButton>(R.id.rbDomingo)
        val rbOtro = findViewById<RadioButton>(R.id.rbOtro)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarFranco)

        // Cargar configuración actual
        val prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val francoActual = prefs.getString("franco", "Domingo")

        // Establecer selección actual
        if (francoActual == "Domingo") {
            rbDomingo.isChecked = true
        } else {
            rbOtro.isChecked = true
        }

        btnGuardar.setOnClickListener {
            try {
                // Validar que se haya seleccionado una opción
                if (!rbDomingo.isChecked && !rbOtro.isChecked) {
                    Toast.makeText(this, "Seleccioná una opción", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val editor = prefs.edit()
                val seleccionado = if (rbDomingo.isChecked) "Domingo" else "Otro"
                editor.putString("franco", seleccionado)
                editor.apply()

                Toast.makeText(this, "Franco guardado: $seleccionado", Toast.LENGTH_SHORT).show()
                finish()
            } catch (e: Exception) {
                Toast.makeText(this, "Error al guardar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

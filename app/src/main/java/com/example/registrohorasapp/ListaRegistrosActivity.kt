package com.example.registrohorasapp

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.registrohorasapp.models.RegistroRequest
import com.example.registrohorasapp.models.RegistroResponse
import com.example.registrohorasapp.retrofit.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.EditText
import java.util.Calendar


class ListaRegistrosActivity : ComponentActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: RegistroAdapter
    private var listaRegistros = mutableListOf<RegistroResponse>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_registros)

        recyclerView = findViewById(R.id.rvRegistros)
        progressBar = findViewById(R.id.progressBar)
        val btnVolver = findViewById<Button>(R.id.btnVolver)

        adapter = RegistroAdapter(listaRegistros,
            onEditarClick = { registro -> mostrarEditarDialogo(registro) },
            onBorrarClick = { registro -> confirmarBorrar(registro) }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        cargarRegistros()

        btnVolver.setOnClickListener {
            finish()
        }
    }

    private fun cargarRegistros() {
        progressBar.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val registros = RetrofitClient.apiService.getRegistros()
                withContext(Dispatchers.Main) {
                    listaRegistros.clear()
                    listaRegistros.addAll(registros)
                    adapter.notifyDataSetChanged()
                    progressBar.visibility = View.GONE
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    val errorMessage = when {
                        e.message?.contains("Failed to connect") == true -> 
                            "Error de conexión. Verifica tu conexión a internet."
                        e.message?.contains("timeout") == true -> 
                            "Tiempo de espera agotado. Intenta nuevamente."
                        e.message?.contains("Unable to resolve host") == true -> 
                            "No se puede conectar al servidor. Verifica la configuración."
                        else -> "Error al cargar registros: ${e.localizedMessage}"
                    }
                    Toast.makeText(this@ListaRegistrosActivity, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun mostrarEditarDialogo(registro: RegistroResponse) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_editar_registro, null)
        val etFecha = dialogView.findViewById<EditText>(R.id.etFecha)
        val etHoras = dialogView.findViewById<EditText>(R.id.etHoras)

        etFecha.setText(registro.fecha)
        etHoras.setText(registro.horas.toString())

        // Agregar DatePickerDialog al EditText de fecha
        etFecha.setOnClickListener {
            try {
                val fechaActual = registro.fecha
                val partes = fechaActual.split("-")
                
                if (partes.size != 3) {
                    Toast.makeText(this, "Formato de fecha inválido", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                
                val year = partes[0].toInt()
                val month = partes[1].toInt() - 1
                val day = partes[2].toInt()

                val dpd = DatePickerDialog(
                    this,
                    { _, selectedYear, selectedMonth, selectedDay ->
                        try {
                            val mesFormateado = String.format("%02d", selectedMonth + 1)
                            val diaFormateado = String.format("%02d", selectedDay)
                            etFecha.setText("$selectedYear-$mesFormateado-$diaFormateado")
                        } catch (e: Exception) {
                            Toast.makeText(this, "Error al formatear fecha: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    },
                    year,
                    month,
                    day
                )
                
                dpd.show()
            } catch (e: Exception) {
                Toast.makeText(this, "Error al abrir selector de fecha: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        AlertDialog.Builder(this)
            .setTitle("Editar Registro")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val nuevaFecha = etFecha.text.toString()
                val nuevasHoras = etHoras.text.toString().toFloatOrNull() ?: return@setPositiveButton

                val request = RegistroRequest(nuevaFecha, nuevasHoras)

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        RetrofitClient.apiService.editarRegistro(registro.id, request)
                        withContext(Dispatchers.Main) {
                            cargarRegistros()
                            Toast.makeText(this@ListaRegistrosActivity, "Registro actualizado", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@ListaRegistrosActivity, "Error al editar: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun confirmarBorrar(registro: RegistroResponse) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar borrado")
            .setMessage("¿Querés borrar el registro del día ${registro.fecha}?")
            .setPositiveButton("Sí") { dialog, _ ->
                dialog.dismiss()
                borrarRegistro(registro)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun borrarRegistro(registro: RegistroResponse) {
        progressBar.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.borrarRegistro(registro.id)
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        listaRegistros.remove(registro)
                        adapter.notifyDataSetChanged()
                        Toast.makeText(this@ListaRegistrosActivity, "Registro borrado", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@ListaRegistrosActivity, "Error al borrar (código ${response.code()})", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@ListaRegistrosActivity, "Error de red: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}

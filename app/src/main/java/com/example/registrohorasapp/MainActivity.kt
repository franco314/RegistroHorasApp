package com.example.registrohorasapp

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.registrohorasapp.models.RegistroRequest
import com.example.registrohorasapp.retrofit.RetrofitClient
import com.example.registrohorasapp.utils.TokenManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.TemporalAdjusters
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var etFecha: EditText
    private lateinit var etHoras: EditText
    private lateinit var btnEnviar: Button
    private lateinit var btnVerRegistros: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configuro Toolbar como ActionBar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Inicializo RetrofitClient con contexto para poder obtener token
        RetrofitClient.init(this)

        // Verifico token, si no está voy a Login y cierro
        val token = TokenManager(this).getToken()
        if (token.isNullOrEmpty()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        etFecha = findViewById(R.id.etFecha)
        etHoras = findViewById(R.id.etHoras)
        btnEnviar = findViewById(R.id.btnEnviar)
        btnVerRegistros = findViewById(R.id.btnVerRegistros)

        etFecha.setOnClickListener {
            try {
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)

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

        btnEnviar.setOnClickListener {
            val fecha = etFecha.text.toString()
            val horasStr = etHoras.text.toString()

            if (fecha.isEmpty() || horasStr.isEmpty()) {
                Toast.makeText(this, "Completá fecha y horas", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val horas = horasStr.toFloatOrNull()
            if (horas == null) {
                Toast.makeText(this, "Ingresá un número válido en horas", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val registro = RegistroRequest(fecha, horas)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    RetrofitClient.apiService.crearRegistro(registro)

                    val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
                    val franco = prefs.getString("franco", "Domingo") ?: "Domingo"

                    val registros = RetrofitClient.apiService.getRegistros()
                    val resumen = calcularResumen(registros, franco)

                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val fechaActual = LocalDate.parse(fecha, formatter)

                    val mostrarResumen = esUltimoDiaSemana(fechaActual, franco)

                    withContext(Dispatchers.Main) {
                        if (mostrarResumen) {
                            mostrarResumen(
                                this@MainActivity,
                                resumen.totalSemana,
                                resumen.extraSemana,
                                resumen.totalMes,
                                resumen.extraMes
                            )
                        } else {
                            Toast.makeText(this@MainActivity, "Registro guardado", Toast.LENGTH_SHORT).show()
                        }

                        etFecha.text.clear()
                        etHoras.text.clear()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        btnVerRegistros.setOnClickListener {
            startActivity(Intent(this, ListaRegistrosActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_configurar_franco -> {
                startActivity(Intent(this, ConfigurarFrancoActivity::class.java))
                true
            }
            R.id.action_ver_horas_acumuladas -> {
                val progressDialog = android.app.ProgressDialog(this)
                progressDialog.setMessage("Cargando resumen...")
                progressDialog.setCancelable(false)
                progressDialog.show()

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val registros = RetrofitClient.apiService.getRegistros()
                        val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
                        val franco = prefs.getString("franco", "Domingo") ?: "Domingo"
                        val resumen = calcularResumen(registros, franco)

                        withContext(Dispatchers.Main) {
                            progressDialog.dismiss()
                            mostrarResumen(
                                this@MainActivity,
                                resumen.totalSemana,
                                resumen.extraSemana,
                                resumen.totalMes,
                                resumen.extraMes
                            )
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            progressDialog.dismiss()
                            Toast.makeText(this@MainActivity, "Error al obtener registros: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
                true
            }
            R.id.action_logout -> {
                TokenManager(this).clearToken()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun mostrarResumen(
        context: android.content.Context,
        totalSemana: Double,
        extraSemana: Double,
        totalMes: Double,
        extraMes: Double
    ) {
        val intent = Intent(context, ResumenActivity::class.java).apply {
            putExtra(ResumenActivity.EXTRA_TOTAL_SEMANA, totalSemana)
            putExtra(ResumenActivity.EXTRA_EXTRA_SEMANA, extraSemana)
            putExtra(ResumenActivity.EXTRA_TOTAL_MES, totalMes)
            putExtra(ResumenActivity.EXTRA_EXTRA_MES, extraMes)
        }
        context.startActivity(intent)
    }

    private fun calcularResumen(
        registros: List<com.example.registrohorasapp.models.RegistroResponse>,
        franco: String
    ): Resumen {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val hoy = LocalDate.now()

        val ultimoDiaSemana = if (franco == "Domingo") DayOfWeek.SATURDAY else DayOfWeek.SUNDAY
        val inicioSemana = hoy.with(DayOfWeek.MONDAY)
        val finSemana = hoy.with(TemporalAdjusters.nextOrSame(ultimoDiaSemana))

        val registrosSemana = registros.filter {
            val fecha = LocalDate.parse(it.fecha, formatter)
            !fecha.isBefore(inicioSemana) && !fecha.isAfter(finSemana)
        }
        val totalSemana = registrosSemana.sumOf { it.horas.toDouble() }
        val extraSemana = if (totalSemana > 24) totalSemana - 24 else 0.0

        val inicioMes: LocalDate
        val finMes: LocalDate
        if (hoy.dayOfMonth >= 20) {
            inicioMes = LocalDate.of(hoy.year, hoy.month, 20)
            finMes = LocalDate.of(hoy.year, hoy.month.plus(1), 19)
        } else {
            inicioMes = LocalDate.of(hoy.year, hoy.month.minus(1), 20)
            finMes = LocalDate.of(hoy.year, hoy.month, 19)
        }

        val registrosMes = registros.filter {
            val fecha = LocalDate.parse(it.fecha, formatter)
            !fecha.isBefore(inicioMes) && !fecha.isAfter(finMes)
        }
        val totalMes = registrosMes.sumOf { it.horas.toDouble() }
        val extraMes = if (totalMes > 96) totalMes - 96 else 0.0

        return Resumen(totalSemana, extraSemana, totalMes, extraMes)
    }

    private fun esUltimoDiaSemana(fecha: LocalDate, franco: String): Boolean {
        return if (franco == "Domingo") {
            fecha.dayOfWeek == DayOfWeek.SATURDAY
        } else {
            fecha.dayOfWeek == DayOfWeek.SUNDAY
        }
    }

    data class Resumen(
        val totalSemana: Double,
        val extraSemana: Double,
        val totalMes: Double,
        val extraMes: Double
    )
}

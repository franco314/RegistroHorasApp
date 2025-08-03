package com.example.registrohorasapp

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity

class ResumenActivity : ComponentActivity() {

    companion object {
        const val EXTRA_TOTAL_SEMANA = "EXTRA_TOTAL_SEMANA"
        const val EXTRA_EXTRA_SEMANA = "EXTRA_EXTRA_SEMANA"
        const val EXTRA_TOTAL_MES = "EXTRA_TOTAL_MES"
        const val EXTRA_EXTRA_MES = "EXTRA_EXTRA_MES"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resumen)

        val tvHorasSemana = findViewById<TextView>(R.id.tvHorasSemana)
        val tvHorasExtraSemana = findViewById<TextView>(R.id.tvHorasExtraSemana)
        val tvTotalHoras = findViewById<TextView>(R.id.tvTotalHoras)
        val tvHorasExtras = findViewById<TextView>(R.id.tvHorasExtras)
        val btnCerrar = findViewById<Button>(R.id.btnCerrar)

        val totalSemana = intent.getDoubleExtra(EXTRA_TOTAL_SEMANA, 0.0)
        val extraSemana = intent.getDoubleExtra(EXTRA_EXTRA_SEMANA, 0.0)
        val totalMes = intent.getDoubleExtra(EXTRA_TOTAL_MES, 0.0)
        val extraMes = intent.getDoubleExtra(EXTRA_EXTRA_MES, 0.0)

        tvHorasSemana.text = "Horas trabajadas en la semana: %.1f hs".format(totalSemana)
        tvHorasExtraSemana.text = "🕐 Horas extras esta semana: %.1f hs".format(extraSemana)
        tvTotalHoras.text = "Horas trabajadas en el mes: %.1f hs".format(totalMes)
        tvHorasExtras.text = "🕐 Horas extras este mes: %.1f hs".format(extraMes)

        btnCerrar.setOnClickListener {
            finish()
        }
    }
}


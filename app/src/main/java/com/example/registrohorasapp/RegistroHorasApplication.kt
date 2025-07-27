package com.example.registrohorasapp

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class RegistroHorasApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // Inicializar ThreeTenABP para manejo de fechas
        AndroidThreeTen.init(this)
    }
} 
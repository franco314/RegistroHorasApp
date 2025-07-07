package com.example.registrohorasapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.registrohorasapp.models.RegistroResponse

class RegistroAdapter(
    private val registros: List<RegistroResponse>,
    private val onEditarClick: (RegistroResponse) -> Unit,
    private val onBorrarClick: (RegistroResponse) -> Unit
) : RecyclerView.Adapter<RegistroAdapter.RegistroViewHolder>() {

    class RegistroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)
        val tvHoras: TextView = itemView.findViewById(R.id.tvHoras)
        val btnEditar: Button = itemView.findViewById(R.id.btnEditar)
        val btnBorrar: Button = itemView.findViewById(R.id.btnBorrar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegistroViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_registro, parent, false)
        return RegistroViewHolder(view)
    }

    override fun onBindViewHolder(holder: RegistroViewHolder, position: Int) {
        val registro = registros[position]
        holder.tvFecha.text = registro.fecha
        holder.tvHoras.text = "${registro.horas} hs"

        holder.btnEditar.setOnClickListener {
            onEditarClick(registro)
        }

        holder.btnBorrar.setOnClickListener {
            onBorrarClick(registro)
        }
    }

    override fun getItemCount(): Int = registros.size
}

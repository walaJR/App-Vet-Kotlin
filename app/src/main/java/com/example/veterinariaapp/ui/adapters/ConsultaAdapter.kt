package com.example.veterinariaapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.veterinariaapp.R
import com.example.veterinariaapp.data.model.Consulta
import com.example.veterinariaapp.data.model.EstadoConsulta
import org.threeten.bp.format.DateTimeFormatter
import java.text.NumberFormat
import java.util.Locale

class ConsultaAdapter(
    private val onCompartirClick: (Consulta) -> Unit,
    private val onEstadoClick: (Consulta) -> Unit,
    private val onDeleteClick: (Consulta) -> Unit
) : ListAdapter<Consulta, ConsultaAdapter.ConsultaViewHolder>(ConsultaDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsultaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_consulta, parent, false)
        return ConsultaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConsultaViewHolder, position: Int) {
        val consulta = getItem(position)
        holder.bind(consulta, position, onCompartirClick, onEstadoClick, onDeleteClick)
    }

    class ConsultaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView: CardView = itemView.findViewById(R.id.cardConsulta)
        private val tvMascota: TextView = itemView.findViewById(R.id.tvMascotaConsulta)
        private val tvTipoServicio: TextView = itemView.findViewById(R.id.tvTipoServicio)
        private val tvFecha: TextView = itemView.findViewById(R.id.tvFechaConsulta)
        private val tvVeterinario: TextView = itemView.findViewById(R.id.tvVeterinario)
        private val tvCosto: TextView = itemView.findViewById(R.id.tvCostoConsulta)
        private val tvEstado: TextView = itemView.findViewById(R.id.tvEstadoConsulta)
        private val btnCompartir: ImageButton = itemView.findViewById(R.id.btnCompartirConsulta)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDeleteConsulta)

        fun bind(
            consulta: Consulta,
            position: Int,
            onCompartirClick: (Consulta) -> Unit,
            onEstadoClick: (Consulta) -> Unit,
            onDeleteClick: (Consulta) -> Unit
        ) {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "CL"))

            tvMascota.text = "${consulta.mascota.nombre} (${consulta.mascota.especie})"
            tvTipoServicio.text = consulta.tipoServicio.displayName
            tvFecha.text = consulta.fechaHora.format(formatter)
            tvVeterinario.text = "Dr. ${consulta.veterinario.nombre}"
            tvCosto.text = currencyFormat.format(consulta.costoTotal)

            // Configurar estado
            tvEstado.text = when (consulta.estado) {
                EstadoConsulta.PENDIENTE -> "⏳ Pendiente"
                EstadoConsulta.REALIZADO -> "✅ Realizado"
                EstadoConsulta.CANCELADO -> "❌ Cancelado"
            }

            val colorEstado = when (consulta.estado) {
                EstadoConsulta.PENDIENTE -> itemView.context.getColor(R.color.estado_pendiente)
                EstadoConsulta.REALIZADO -> itemView.context.getColor(R.color.estado_realizado)
                EstadoConsulta.CANCELADO -> itemView.context.getColor(R.color.estado_cancelado)
            }
            tvEstado.setBackgroundColor(colorEstado)

            // Accesibilidad
            cardView.contentDescription = "Consulta de ${consulta.mascota.nombre}, ${consulta.tipoServicio.displayName}"
            btnCompartir.contentDescription = "Compartir consulta"
            btnDelete.contentDescription = "Eliminar consulta"

            // Eventos
            btnCompartir.setOnClickListener { onCompartirClick(consulta) }
            btnDelete.setOnClickListener { onDeleteClick(consulta) }
            tvEstado.setOnClickListener { onEstadoClick(consulta) }

            // Animación
            cardView.alpha = 0f
            cardView.translationY = 50f
            cardView.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(300)
                .setStartDelay((position % 5) * 50L)
                .start()
        }
    }

    class ConsultaDiffCallback : DiffUtil.ItemCallback<Consulta>() {
        override fun areItemsTheSame(oldItem: Consulta, newItem: Consulta): Boolean {
            return oldItem.fechaHora == newItem.fechaHora &&
                    oldItem.mascota.nombre == newItem.mascota.nombre
        }

        override fun areContentsTheSame(oldItem: Consulta, newItem: Consulta): Boolean {
            return oldItem == newItem
        }
    }
}
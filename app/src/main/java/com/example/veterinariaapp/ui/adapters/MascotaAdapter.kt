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
import com.example.veterinariaapp.data.model.Mascota

class MascotaAdapter(
    private val onEditClick: (Mascota) -> Unit,
    private val onDeleteClick: (Mascota) -> Unit
) : ListAdapter<Mascota, MascotaAdapter.MascotaViewHolder>(MascotaDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MascotaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mascota, parent, false)
        return MascotaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MascotaViewHolder, position: Int) {
        val mascota = getItem(position)
        holder.bind(mascota, onEditClick, onDeleteClick)
    }

    class MascotaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView: CardView = itemView.findViewById(R.id.cardMascota)
        private val tvNombre: TextView = itemView.findViewById(R.id.tvNombreMascota)
        private val tvEspecie: TextView = itemView.findViewById(R.id.tvEspecieMascota)
        private val tvEdad: TextView = itemView.findViewById(R.id.tvEdadMascota)
        private val tvPeso: TextView = itemView.findViewById(R.id.tvPesoMascota)
        private val tvDueno: TextView = itemView.findViewById(R.id.tvDuenoMascota)
        private val btnEdit: ImageButton = itemView.findViewById(R.id.btnEditMascota)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDeleteMascota)

        fun bind(
            mascota: Mascota,
            onEditClick: (Mascota) -> Unit,
            onDeleteClick: (Mascota) -> Unit
        ) {
            tvNombre.text = mascota.nombre
            tvEspecie.text = "Especie: ${mascota.especie}"
            tvEdad.text = "Edad: ${mascota.edad} años"
            tvPeso.text = "Peso: ${mascota.peso} kg"
            tvDueno.text = "Dueño: ${mascota.dueno.nombre}"

            // Accesibilidad
            cardView.contentDescription = "Mascota ${mascota.nombre}, ${mascota.especie} de ${mascota.edad} años"
            btnEdit.contentDescription = "Editar mascota ${mascota.nombre}"
            btnDelete.contentDescription = "Eliminar mascota ${mascota.nombre}"

            // Eventos
            btnEdit.setOnClickListener { onEditClick(mascota) }
            btnDelete.setOnClickListener { onDeleteClick(mascota) }

            // Animación
            cardView.alpha = 0f
            cardView.animate()
                .alpha(1f)
                .setDuration(300)
                .start()
        }
    }

    class MascotaDiffCallback : DiffUtil.ItemCallback<Mascota>() {
        override fun areItemsTheSame(oldItem: Mascota, newItem: Mascota): Boolean {
            return oldItem.nombre == newItem.nombre && oldItem.dueno.email == newItem.dueno.email
        }

        override fun areContentsTheSame(oldItem: Mascota, newItem: Mascota): Boolean {
            return oldItem == newItem
        }
    }
}
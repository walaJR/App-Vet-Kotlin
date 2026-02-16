package com.example.veterinariaapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.veterinariaapp.R
import com.example.veterinariaapp.data.model.Mascota
import com.example.veterinariaapp.ui.adapters.MascotaAdapter
import com.example.veterinariaapp.ui.dialogs.ConfirmDeleteDialog
import com.example.veterinariaapp.ui.dialogs.EditMascotaDialog
import com.example.veterinariaapp.ui.viewmodel.ListadoViewModel
import kotlinx.coroutines.launch

class MascotasFragment : Fragment() {

    private val viewModel: ListadoViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var adapter: MascotaAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mascotas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews(view)
        setupRecyclerView(view)
        observeData()
    }

    private fun setupViews(view: View) {
        tvEmpty = view.findViewById(R.id.tvEmptyMascotas)
        tvEmpty.contentDescription = "No hay mascotas registradas en el sistema"
    }

    private fun setupRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.recyclerViewMascotas)
        adapter = MascotaAdapter(
            onEditClick = { mascota ->
                showEditDialog(mascota)
            },
            onDeleteClick = { mascota ->
                showDeleteDialog(mascota)
            }
        )

        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@MascotasFragment.adapter
            setHasFixedSize(true)
            contentDescription = "Lista de mascotas registradas en el sistema"
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.mascotas.collect { mascotas ->
                adapter.submitList(mascotas)
                updateEmptyState(mascotas.isEmpty())
            }
        }
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        tvEmpty.isVisible = isEmpty
        recyclerView.isVisible = !isEmpty
    }

    private fun showEditDialog(mascota: Mascota) {
        EditMascotaDialog(
            context = requireContext(),
            mascota = mascota,
            onSave = { mascotaActualizada ->
                viewModel.actualizarMascota(mascotaActualizada)
            }
        ).show()
    }

    private fun showDeleteDialog(mascota: Mascota) {
        ConfirmDeleteDialog(
            context = requireContext(),
            title = "Eliminar Mascota",
            message = "¿Está seguro que desea eliminar a ${mascota.nombre}? Esta acción no se puede deshacer.",
            onConfirm = {
                viewModel.eliminarMascota(mascota)
            }
        ).show()
    }
}
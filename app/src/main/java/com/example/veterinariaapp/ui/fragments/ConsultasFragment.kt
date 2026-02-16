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
import com.example.veterinariaapp.data.model.Consulta
import com.example.veterinariaapp.ui.adapters.ConsultaAdapter
import com.example.veterinariaapp.ui.dialogs.ConfirmDeleteDialog
import com.example.veterinariaapp.ui.viewmodel.ListadoViewModel
import kotlinx.coroutines.launch


class ConsultasFragment : Fragment() {

    private val viewModel: ListadoViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var adapter: ConsultaAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_consultas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews(view)
        setupRecyclerView(view)
        observeData()
    }

    private fun setupViews(view: View) {
        tvEmpty = view.findViewById(R.id.tvEmptyConsultas)
        tvEmpty.contentDescription = "No hay consultas registradas en el sistema"
    }

    private fun setupRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.recyclerViewConsultas)
        adapter = ConsultaAdapter(
            onCompartirClick = { consulta ->
                viewModel.compartirConsulta(requireContext(), consulta)
            },
            onEstadoClick = { consulta ->
                viewModel.cambiarEstadoConsulta(consulta)
            },
            onDeleteClick = { consulta ->
                showDeleteDialog(consulta)
            }
        )

        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ConsultasFragment.adapter
            setHasFixedSize(true)
            contentDescription = "Lista de consultas veterinarias registradas"
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.consultas.collect { consultas ->
                adapter.submitList(consultas)
                updateEmptyState(consultas.isEmpty())
            }
        }
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        tvEmpty.isVisible = isEmpty
        recyclerView.isVisible = !isEmpty
    }

    private fun showDeleteDialog(consulta: Consulta) {
        ConfirmDeleteDialog(
            context = requireContext(),
            title = "Eliminar Consulta",
            message = "¿Está seguro que desea eliminar la consulta de ${consulta.mascota.nombre}?",
            onConfirm = {
                viewModel.eliminarConsulta(consulta)
            }
        ).show()
    }
}

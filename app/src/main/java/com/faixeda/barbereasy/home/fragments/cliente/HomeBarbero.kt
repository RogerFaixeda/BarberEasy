package com.faixeda.barbereasy.home.fragments.cliente

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.faixeda.barbereasy.R
import com.faixeda.barbereasy.databinding.FragmentHomeBarberoBinding
import com.faixeda.barbereasy.home.adapters.TimeSlotAdapter
import com.google.firebase.firestore.FirebaseFirestore

class HomeBarbero : Fragment() {

    private var _binding: FragmentHomeBarberoBinding? = null
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: TimeSlotAdapter
    private lateinit var barberId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Recoge el ID pasado como argumento
        barberId = requireArguments().getString("barberId")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home_barbero, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Consulta Firestore para datos del barbero
        db.collection("barberos")
            .document(barberId)
            .get()
            .addOnSuccessListener { doc ->
                val name    = doc.getString("fullName") ?: ""
                val price   = doc.getString("price") ?: ""
                // Muestra nombre y precio
                binding.tvBarberName.text  = name
                binding.tvBarberPrice.text = price

                // 1) Configura RecyclerView con adapter ya preparado
                adapter = TimeSlotAdapter(
                    barberId,
                    name,
                    price,
                    emptyList()
                )
                binding.rvSchedule.apply {
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = this@HomeBarbero.adapter
                }

                // 2) Carga horarios disponibles
                doc.reference.collection("horarios")
                    .whereEqualTo("available", true)
                    .get()
                    .addOnSuccessListener { snap ->
                        val slots = snap.documents.mapNotNull { it.getString("time") }
                        adapter.update(slots)
                    }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

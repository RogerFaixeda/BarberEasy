package com.faixeda.barbereasy.home.fragments.barber

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.faixeda.barbereasy.R
import com.faixeda.barbereasy.databinding.FragmentMiBarberiaBinding
import com.faixeda.barbereasy.home.adapters.BarberReservationAdapter
import com.faixeda.barbereasy.home.models.BarberReservation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MiBarberia : Fragment() {

    private var _binding: FragmentMiBarberiaBinding? = null
    private val binding get() = _binding!!

    private val db   = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var adapter: BarberReservationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_mi_barberia, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configura RecyclerView
        adapter = BarberReservationAdapter(emptyList())
        binding.rvReservations.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@MiBarberia.adapter
        }

        // Carga reservas del barbero
        val barberId = auth.currentUser?.uid ?: return
        db.collection("barberos")
            .document(barberId)
            .collection("reservas")
            .get()
            .addOnSuccessListener { snap ->
                val list = snap.documents.mapNotNull { doc ->
                    val client = doc.getString("clientName") ?: return@mapNotNull null
                    val time   = doc.getString("time")       ?: return@mapNotNull null
                    BarberReservation(clientName = client, time = time)
                }
                adapter.update(list)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

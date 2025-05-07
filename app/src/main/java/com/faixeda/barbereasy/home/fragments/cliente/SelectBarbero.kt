package com.faixeda.barbereasy.home.fragments.cliente

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.faixeda.barbereasy.R
import com.faixeda.barbereasy.databinding.FragmentSelectBarberoBinding
import com.faixeda.barbereasy.home.adapters.BarberAdapter
import com.faixeda.barbereasy.home.models.Barber
import com.google.firebase.firestore.FirebaseFirestore

class SelectBarbero : Fragment() {

    private var _binding: FragmentSelectBarberoBinding? = null
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: BarberAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_select_barbero, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1) Leer ciudad de los argumentos
        val city = requireArguments().getString("city", "")

        // 2) Botón para volver atrás
        binding.btnCity.text = city
        binding.btnCity.setOnClickListener {
            findNavController().navigateUp()
        }

        // 3) Configurar RecyclerView con el nuevo adapter
        adapter = BarberAdapter(emptyList())
        binding.rvBarbers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@SelectBarbero.adapter
        }

        // 4) Cargar barberos de Firestore filtrados por ciudad
        db.collection("barberos")
            .whereEqualTo("location", city)
            .get()
            .addOnSuccessListener { snap ->
                val list = snap.documents.mapNotNull { doc ->
                    Barber(
                        fullName = doc.getString("fullName") ?: "",
                        price    = doc.getString("price")    ?: "",
                        id       = doc.id
                    )
                }
                adapter.update(list)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

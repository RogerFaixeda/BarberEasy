package com.faixeda.barbereasy.home.fragments.cliente

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.faixeda.barbereasy.R
import com.faixeda.barbereasy.databinding.FragmentReservaRealizadaBinding

class ReservaRealizada : Fragment() {

    private var _binding: FragmentReservaRealizadaBinding? = null
    private val binding get() = _binding!!

    private lateinit var barberName: String
    private lateinit var timeSlot: String
    private lateinit var price: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            barberName = it.getString("barberName") ?: ""
            timeSlot   = it.getString("timeSlot") ?: ""
            price      = it.getString("barberPrice") ?: ""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_reserva_realizada, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Rellenar detalles
        binding.tvDetailName.text   = barberName
        binding.tvDetailHour.text   = timeSlot
        binding.tvDetailPrice.text  = price

        // Botón para cerrar / volver al inicio
        binding.btnDone.setOnClickListener {
            // Por ejemplo: a la pantalla principal del cliente
            findNavController().navigate(R.id.action_reserva_realizada_to_select_ciudad)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.faixeda.barbereasy.home.fragments.cliente

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.faixeda.barbereasy.R
import com.faixeda.barbereasy.databinding.FragmentSelectCiudadBinding

class SelectCiudad : Fragment() {

    private var _binding: FragmentSelectCiudadBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_select_ciudad, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLBuscar.setOnClickListener {
            // 1) Lee la ciudad seleccionada
            val city = binding.spinnerCiudades.selectedItem as String

            // 2) Navega al siguiente fragment pasando un Bundle con la ciudad
            findNavController().navigate(
                R.id.action_select_ciudad_to_select_barbero,
                bundleOf("city" to city)
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

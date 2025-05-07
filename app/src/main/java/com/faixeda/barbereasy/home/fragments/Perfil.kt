package com.faixeda.barbereasy.home.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.faixeda.barbereasy.LoginAct
import com.faixeda.barbereasy.databinding.FragmentPerfilBinding
import com.faixeda.barbereasy.home.adapters.ReservationAdapter
import com.faixeda.barbereasy.home.models.Reservation
import com.faixeda.barbereasy.home.viewmodels.SharedUserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Perfil : Fragment() {
    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!

    private val sharedUserVm: SharedUserViewModel by activityViewModels()
    private val auth = FirebaseAuth.getInstance()
    private val db   = FirebaseFirestore.getInstance()
    private lateinit var reservationAdapter: ReservationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)

        sharedUserVm.user.observe(viewLifecycleOwner) { user ->
            val display = "${user.firstName} ${user.lastName}"
            binding.tvUsername.text = display
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configura RecyclerView de reservas
        reservationAdapter = ReservationAdapter(emptyList())
        binding.rvReservations.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = reservationAdapter
        }

        // Carga reservas del cliente
        val uid = auth.currentUser?.uid ?: return
        db.collection("users")
            .document(uid)
            .collection("reservas")
            .get()
            .addOnSuccessListener { snap ->
                val list = snap.documents.mapNotNull { doc ->
                    val barber = doc.getString("barberName") ?: return@mapNotNull null
                    val time   = doc.getString("time")      ?: return@mapNotNull null
                    Reservation(barberName = barber, time = time)
                }
                reservationAdapter.update(list)
            }
        binding.btnLogout.setOnClickListener {
            auth.signOut()
            Toast.makeText(requireContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireActivity(), LoginAct::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
            requireActivity().finish()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

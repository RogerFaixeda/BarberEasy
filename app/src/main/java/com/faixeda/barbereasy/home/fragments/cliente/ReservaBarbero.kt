package com.faixeda.barbereasy.home.fragments.cliente

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.faixeda.barbereasy.R
import com.faixeda.barbereasy.databinding.FragmentReservaBarberoBinding
import com.faixeda.barbereasy.home.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ReservaBarbero : Fragment() {

    private var _binding: FragmentReservaBarberoBinding? = null
    private val binding get() = _binding!!

    private val db   = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private lateinit var barberId: String
    private lateinit var barberName: String
    private lateinit var barberPrice: String
    private lateinit var timeSlot: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            barberId    = it.getString("barberId")!!
            barberName  = it.getString("barberName")!!
            barberPrice = it.getString("barberPrice")!!
            timeSlot    = it.getString("timeSlot")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_reserva_barbero, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Mostrar datos recibidos
        binding.tvName.text  = barberName
        binding.tvPrice.text = barberPrice
        binding.tvTime.text  = "Hora: $timeSlot"

        binding.btnConfirm.setOnClickListener {
            val clientId = auth.currentUser?.uid
            if (clientId.isNullOrEmpty()) {
                Toast.makeText(requireContext(),
                    "Usuario no autenticado", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Leer nombre del cliente
            db.collection("users")
                .document(clientId)
                .get()
                .addOnSuccessListener { doc ->
                    val user = doc.toObject(User::class.java)
                    val clientName = if (user != null)
                        "${user.firstName} ${user.lastName}"
                    else
                        "(Desconocido)"

                    // Batch para ambas colecciones
                    val batch = db.batch()
                    val barberResRef = db.collection("barberos")
                        .document(barberId)
                        .collection("reservas")
                        .document()
                    batch.set(barberResRef, mapOf(
                        "time"       to timeSlot,
                        "clientName" to clientName
                    ))

                    val clientResRef = db.collection("users")
                        .document(clientId)
                        .collection("reservas")
                        .document()
                    batch.set(clientResRef, mapOf(
                        "time"       to timeSlot,
                        "barberName" to barberName
                    ))

                    batch.commit()
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(),
                                "Reserva creada", Toast.LENGTH_SHORT).show()

                            // Navegar con argumentos al fragmento de confirmación
                            val args = bundleOf(
                                "barberName"  to barberName,
                                "barberPrice" to barberPrice,
                                "timeSlot"    to timeSlot
                            )
                            findNavController().navigate(
                                R.id.action_reserva_barbero_to_reserva_realizada,
                                args
                            )
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(requireContext(),
                                "Error creando reserva: ${e.message}",
                                Toast.LENGTH_LONG).show()
                        }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(),
                        "Error leyendo datos de cliente: ${e.message}",
                        Toast.LENGTH_LONG).show()
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

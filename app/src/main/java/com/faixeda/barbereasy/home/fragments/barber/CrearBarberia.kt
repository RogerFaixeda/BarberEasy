package com.faixeda.barbereasy.home.fragments.barber

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.faixeda.barbereasy.R
import com.faixeda.barbereasy.databinding.FragmentCrearBarberiaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import java.util.Locale

class CrearBarberia : Fragment() {

    private var _binding: FragmentCrearBarberiaBinding? = null
    private val binding get() = _binding!!

    private var startHour = -1
    private var startMinute = -1
    private var endHour = -1
    private var endMinute = -1

    private val auth = FirebaseAuth.getInstance()
    private val db   = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_crear_barberia, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Selección de hora de inicio
        binding.btnStartTime.setOnClickListener {
            val now = Calendar.getInstance()
            TimePickerDialog(
                requireContext(),
                { _, h, m ->
                    startHour = h; startMinute = m
                    binding.tvStartTime.text = formatTime(h, m)
                },
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
            ).show()
        }

        // Selección de hora de fin
        binding.btnEndTime.setOnClickListener {
            val now = Calendar.getInstance()
            TimePickerDialog(
                requireContext(),
                { _, h, m ->
                    endHour = h; endMinute = m
                    binding.tvEndTime.text = formatTime(h, m)
                },
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
            ).show()
        }

        // Guardar slots en Firestore
        binding.btnSaveSchedule.setOnClickListener {
            // Validación de horas
            if (startHour < 0 || endHour < 0) {
                Toast.makeText(requireContext(),
                    "Debes elegir ambas horas", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val startCal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, startHour)
                set(Calendar.MINUTE, startMinute)
                set(Calendar.SECOND, 0)
            }
            val endCal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, endHour)
                set(Calendar.MINUTE, endMinute)
                set(Calendar.SECOND, 0)
            }
            if (!endCal.after(startCal)) {
                Toast.makeText(requireContext(),
                    "La hora de fin debe ser posterior", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Generar slots cada 30 minutos
            val slots = mutableListOf<String>()
            val iter = startCal.clone() as Calendar
            while (iter <= endCal) {
                slots.add(formatTime(
                    iter.get(Calendar.HOUR_OF_DAY),
                    iter.get(Calendar.MINUTE)
                ))
                iter.add(Calendar.MINUTE, 30)
            }

            // Leer ciudad seleccionada
            val location = binding.spinnerCiudades.selectedItem as String

            //Precio
            val priceStr = binding.etPrice.text.toString().trim()
            if (priceStr.isEmpty()) {
                Toast.makeText(requireContext(),
                    "Introduce un precio válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            // Referencia a documento y colección
            val uid = auth.currentUser?.uid ?: return@setOnClickListener
            val barberDoc   = db.collection("barberos").document(uid)
            val horariosCol = barberDoc.collection("horarios")

            // 1) Leer todos los documentos actuales y crear batch de eliminación + inserción
            horariosCol.get().addOnSuccessListener { snapshot ->
                val batch = db.batch()

                // Borrar cada documento antiguo
                snapshot.documents.forEach { doc ->
                    batch.delete(doc.reference)
                }

                // Actualizar solo el campo "location" en el documento del barbero
                batch.update(barberDoc, "location", location)
                batch.update(barberDoc, "price", priceStr)

                // Añadir nuevos slots
                slots.forEach { timeStr ->
                    val docRef = horariosCol.document(timeStr)
                    batch.set(docRef, mapOf(
                        "time" to timeStr,
                        "available" to true
                    ))
                }

                // Ejecutar batch
                batch.commit()
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(),
                            "Horario y ciudad actualizados", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(),
                            "Error al actualizar: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(),
                        "Error al leer horarios actuales: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun formatTime(hour: Int, minute: Int): String =
        String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
}

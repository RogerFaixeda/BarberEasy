package com.faixeda.barbereasy.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.faixeda.barbereasy.R
import com.faixeda.barbereasy.databinding.ItemTimeSlotBinding

class TimeSlotAdapter(
    private val barberId: String,
    private val barberName: String,
    private val barberPrice: String,
    private var slots: List<String>
) : RecyclerView.Adapter<TimeSlotAdapter.VH>() {

    inner class VH(private val binding: ItemTimeSlotBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(slot: String) {
            binding.tvTime.text = slot
            binding.root.setOnClickListener { view ->
                val args = bundleOf(
                    "barberId"    to barberId,
                    "barberName"  to barberName,
                    "barberPrice" to barberPrice,
                    "timeSlot"    to slot
                )
                view.findNavController()
                    .navigate(R.id.action_home_barbero_to_reserva_barbero, args)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemTimeSlotBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(slots[position])
    }

    override fun getItemCount() = slots.size

    fun update(newSlots: List<String>) {
        slots = newSlots
        notifyDataSetChanged()
    }
}

package com.faixeda.barbereasy.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.faixeda.barbereasy.databinding.ItemReservationBinding
import com.faixeda.barbereasy.home.models.BarberReservation

class BarberReservationAdapter(
    private var items: List<BarberReservation>
) : RecyclerView.Adapter<BarberReservationAdapter.VH>() {

    inner class VH(private val binding: ItemReservationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(r: BarberReservation) {
            binding.tvResBarber.text = "Cliente: ${r.clientName}"
            binding.tvResTime.text   = "Hora: ${r.time}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemReservationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun update(newItems: List<BarberReservation>) {
        items = newItems
        notifyDataSetChanged()
    }
}

package com.faixeda.barbereasy.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.faixeda.barbereasy.databinding.ItemReservationBinding
import com.faixeda.barbereasy.home.models.Reservation

class ReservationAdapter(
    private var items: List<Reservation>
) : RecyclerView.Adapter<ReservationAdapter.VH>() {

    inner class VH(private val binding: ItemReservationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(r: Reservation) {
            binding.tvResBarber.text = "Barbero: ${r.barberName}"
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

    fun update(newItems: List<Reservation>) {
        items = newItems
        notifyDataSetChanged()
    }
}

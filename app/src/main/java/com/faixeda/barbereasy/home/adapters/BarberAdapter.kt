package com.faixeda.barbereasy.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.faixeda.barbereasy.R
import com.faixeda.barbereasy.databinding.ItemBarberBinding
import com.faixeda.barbereasy.home.models.Barber

class BarberAdapter(
    private var items: List<Barber>
) : RecyclerView.Adapter<BarberAdapter.VH>() {

    inner class VH(private val binding: ItemBarberBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(b: Barber) {
            binding.tvName.text = b.fullName
            binding.tvPrice.text = b.price
            binding.btnVerMas.setOnClickListener { view ->
                // Empaqueta el ID y navega al fragment destino
                val args = bundleOf("barberId" to b.id)
                view.findNavController()
                    .navigate(R.id.action_select_barbero_to_home_barbero, args)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemBarberBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun update(newItems: List<Barber>) {
        items = newItems
        notifyDataSetChanged()
    }
}

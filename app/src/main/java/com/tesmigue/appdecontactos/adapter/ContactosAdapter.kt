package com.tesmigue.contacto.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tesmigue.contacto.databinding.ItemContactoBinding
import com.tesmigue.contacto.model.Contacto


class ContactosAdapter(
    private var contactos: List<Contacto>,   // lista interna mutable
    private val onClick: (Contacto) -> Unit
) : RecyclerView.Adapter<ContactosAdapter.ContactoViewHolder>() {

    inner class ContactoViewHolder(val binding: ItemContactoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(contacto: Contacto) {
            binding.tvNombre.text = contacto.nombre
            binding.tvTelefono.text = contacto.telefono
            binding.root.setOnClickListener {
                onClick(contacto)  // CORRECCIÓN aquí
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactoViewHolder {
        val binding = ItemContactoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactoViewHolder(binding)
    }

    override fun getItemCount(): Int = contactos.size  // CORRECCIÓN aquí

    override fun onBindViewHolder(holder: ContactoViewHolder, position: Int) {
        holder.bind(contactos[position])  // CORRECCIÓN aquí
    }

    fun actualizarLista(nuevaLista: List<Contacto>) {
        this.contactos = nuevaLista
        notifyDataSetChanged()
    }

}

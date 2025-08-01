package com.tesmigue.contactos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tesmigue.contactos.databinding.ItemContactoBinding

class ContactosAdapter(
    private var lista: List<Contacto>,
    private val onItemClick: (Contacto) -> Unit
) : RecyclerView.Adapter<ContactosAdapter.ContactoViewHolder>() {

    inner class ContactoViewHolder(val binding: ItemContactoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(contacto: Contacto) {
            binding.tvNombre.text = contacto.nombre
            binding.tvTelefono.text = contacto.telefono
            binding.root.setOnClickListener {
                onItemClick(contacto)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactoViewHolder {
        val binding = ItemContactoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactoViewHolder(binding)
    }

    override fun getItemCount(): Int = lista.size

    override fun onBindViewHolder(holder: ContactoViewHolder, position: Int) {
        holder.bind(lista[position])
    }

    fun actualizarLista(nuevaLista: List<Contacto>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}

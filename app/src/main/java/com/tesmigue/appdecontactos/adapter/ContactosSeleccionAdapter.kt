package com.tesmigue.contacto.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tesmigue.contacto.databinding.ItemCheckboxContactoBinding
import com.tesmigue.contacto.model.Contacto


// Clase simple para manejar el estado de selección sin extensiones
data class ContactoSeleccionState(
    val contacto: Contacto,
    var isSelected: Boolean = false,
    var estaEnGrupo: Boolean = false
)

class ContactosSeleccionAdapter(
    private val onSelectionChanged: (Contacto, Boolean) -> Unit
) : RecyclerView.Adapter<ContactosSeleccionAdapter.ContactoSeleccionViewHolder>() {

    private var estadosSeleccion: MutableList<ContactoSeleccionState> = mutableListOf()

    inner class ContactoSeleccionViewHolder(
        private val binding: ItemCheckboxContactoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(estadoSeleccion: ContactoSeleccionState) {
            val contacto = estadoSeleccion.contacto

            binding.apply {
                tvNombre.text = contacto.nombre
                tvTelefono.text = contacto.telefono
                tvEmail.text = contacto.email

                // Configurar checkbox
                checkbox.isChecked = estadoSeleccion.estaEnGrupo || estadoSeleccion.isSelected

                // Deshabilitar si ya está en el grupo
                checkbox.isEnabled = !estadoSeleccion.estaEnGrupo

                // Cambiar texto si ya está en grupo
                if (estadoSeleccion.estaEnGrupo) {
                    tvNombre.text = "${contacto.nombre} (Ya en grupo)"
                    root.alpha = 0.7f
                } else {
                    root.alpha = 1.0f
                }

                // Listener para cambios en checkbox
                checkbox.setOnCheckedChangeListener(null) // Remover listener anterior
                checkbox.setOnCheckedChangeListener { _, isChecked ->
                    if (!estadoSeleccion.estaEnGrupo) {
                        estadoSeleccion.isSelected = isChecked
                        onSelectionChanged(contacto, isChecked)
                    }
                }

                // Click en toda la fila
                root.setOnClickListener {
                    if (!estadoSeleccion.estaEnGrupo) {
                        checkbox.isChecked = !checkbox.isChecked
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactoSeleccionViewHolder {
        val binding = ItemCheckboxContactoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ContactoSeleccionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactoSeleccionViewHolder, position: Int) {
        holder.bind(estadosSeleccion[position])
    }

    override fun getItemCount(): Int = estadosSeleccion.size

    fun actualizarLista(contactos: List<Contacto>) {
        estadosSeleccion.clear()
        estadosSeleccion.addAll(contactos.map { ContactoSeleccionState(it) })
        notifyDataSetChanged()
    }

    fun marcarContactosExistentes(idsContactos: Set<Int>) {
        estadosSeleccion.forEach { estado ->
            estado.estaEnGrupo = idsContactos.contains(estado.contacto.id)
        }
        notifyDataSetChanged()
    }

    fun obtenerContactosSeleccionados(): List<Contacto> {
        return estadosSeleccion
            .filter { it.isSelected && !it.estaEnGrupo }
            .map { it.contacto }
    }

    fun obtenerContactosDeseleccionados(): List<Contacto> {
        return estadosSeleccion
            .filter { !it.isSelected && it.estaEnGrupo }
            .map { it.contacto }
    }

    fun obtenerTodosLosSeleccionados(): List<Contacto> {
        return estadosSeleccion
            .filter { it.isSelected || it.estaEnGrupo }
            .map { it.contacto }
    }

    fun limpiarSelecciones() {
        estadosSeleccion.forEach { it.isSelected = false }
        notifyDataSetChanged()
    }

    fun seleccionarTodos() {
        estadosSeleccion.forEach {
            if (!it.estaEnGrupo) {
                it.isSelected = true
            }
        }
        notifyDataSetChanged()
    }
}
package com.tesmigue.contactos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.tesmigue.contactos.database.ContactosDatabase
import com.tesmigue.contactos.model.*
import com.tesmigue.contactos.repository.ContactosRepository

class ContactosViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ContactosRepository

    val contactos: LiveData<List<Contacto>>
    val categorias: LiveData<List<Categoria>>
    val grupos: LiveData<List<Grupo>>

    init {
        val db = ContactosDatabase.getDatabase(application)
        repository = ContactosRepository(
            db.contactoDao(),
            db.categoriaDao(),
            db.grupoDao()
        )
        contactos = repository.contactos
        categorias = repository.categorias
        grupos = repository.grupos
    }

    // --- Contactos ---
    fun insertar(contacto: Contacto) = viewModelScope.launch {
        repository.insertarContacto(contacto)
    }

    fun eliminar(contacto: Contacto) = viewModelScope.launch {
        repository.eliminarContacto(contacto)
    }

    fun actualizar(contacto: Contacto) = viewModelScope.launch {
        repository.actualizarContacto(contacto)
    }

    fun buscar(query: String) {
        _busqueda.value = query
    }

    private val _busqueda = MutableLiveData<String>()
    val contactosFiltrados: LiveData<List<Contacto>> = Transformations.switchMap(_busqueda) {
        if (it.isBlank()) contactos else repository.buscarContactos(it)
    }

    // --- Categorías ---
    fun insertarCategoria(categoria: Categoria) = viewModelScope.launch {
        repository.insertarCategoria(categoria)
    }

    // --- Grupos ---
    fun insertarGrupo(grupo: Grupo) = viewModelScope.launch {
        repository.insertarGrupo(grupo)
    }

    fun actualizarGrupo(grupo: Grupo) = viewModelScope.launch {
        repository.actualizarGrupo(grupo)
    }

    fun eliminarGrupo(grupo: Grupo) = viewModelScope.launch {
        repository.eliminarGrupo(grupo)
    }

    // --- Relaciones Contacto-Grupo ---
    fun asociarContactoAGrupo(contactoId: Int, grupoId: Int) = viewModelScope.launch {
        repository.asociarContactoAGrupo(ContactoGrupoCrossRef(contactoId, grupoId))
    }

    fun removerContactoDeGrupo(contactoId: Int, grupoId: Int) = viewModelScope.launch {
        repository.removerContactoDeGrupo(ContactoGrupoCrossRef(contactoId, grupoId))
    }

    // --- Consultas de relaciones ---
    fun obtenerContactosDeGrupo(grupoId: Int): LiveData<GrupoConContactos> {
        return repository.obtenerGrupoConContactos(grupoId)
    }

    fun obtenerGruposDeContacto(contactoId: Int): LiveData<ContactoConGrupos> {
        return repository.obtenerContactoConGrupos(contactoId)
    }

    fun obtenerTodosLosGruposConContactos(): LiveData<List<GrupoConContactos>> {
        return repository.obtenerTodosLosGruposConContactos()
    }

    // --- Consultas adicionales ---
    fun contarContactosEnGrupo(grupoId: Int, callback: (Int) -> Unit) = viewModelScope.launch {
        val count = repository.contarContactosEnGrupo(grupoId)
        callback(count)
    }
}
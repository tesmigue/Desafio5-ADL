package com.tesmigue.contacto.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.tesmigue.contacto.database.CategoriaDao
import com.tesmigue.contacto.database.ContactoDao
import com.tesmigue.contacto.database.GrupoDao
import com.tesmigue.contacto.model.Categoria
import com.tesmigue.contacto.model.Contacto
import com.tesmigue.contacto.model.ContactoConGrupos
import com.tesmigue.contacto.model.ContactoGrupoCrossRef
import com.tesmigue.contacto.model.Grupo
import com.tesmigue.contacto.model.GrupoConContactos


class ContactosRepository(
    private val contactoDao: ContactoDao,
    private val categoriaDao: CategoriaDao,
    private val grupoDao: GrupoDao
) {

    val contactos = contactoDao.obtenerContactos()
    val categorias = categoriaDao.obtenerCategorias()
    val grupos = grupoDao.obtenerGrupos()

    // --- CRUD Contactos ---
    suspend fun insertarContacto(contacto: Contacto) {
        val existe = categoriaDao.existeCategoria(contacto.categoriaId) > 0
        if (existe) {
            contactoDao.insertar(contacto)
        } else {
            // Puedes lanzar una excepción, o devolver un boolean si quieres controlarlo arriba
            Log.e("Repositorio", "La categoría con ID ${contacto.categoriaId} no existe")
        }
    }

    suspend fun actualizarContacto(contacto: Contacto) = contactoDao.actualizar(contacto)
    suspend fun eliminarContacto(contacto: Contacto) = contactoDao.eliminar(contacto)

    // --- CRUD Categorías ---
    suspend fun insertarCategoria(categoria: Categoria) = categoriaDao.insertar(categoria)

    // --- CRUD Grupos ---
    suspend fun insertarGrupo(grupo: Grupo) = grupoDao.insertarGrupo(grupo)
    suspend fun actualizarGrupo(grupo: Grupo) = grupoDao.actualizarGrupo(grupo)
    suspend fun eliminarGrupo(grupo: Grupo) = grupoDao.eliminarGrupo(grupo)

    // --- Buscar contactos ---
    fun buscarContactos(query: String) = contactoDao.buscarContactos("%$query%")

    // --- Relaciones Contacto-Grupo ---
    suspend fun asociarContactoAGrupo(crossRef: ContactoGrupoCrossRef) =
        grupoDao.insertarContactoGrupoCrossRef(crossRef)

    suspend fun removerContactoDeGrupo(crossRef: ContactoGrupoCrossRef) =
        grupoDao.eliminarContactoGrupoCrossRef(crossRef)

    // --- Consultas de relaciones ---
    fun obtenerGrupoConContactos(grupoId: Int): LiveData<GrupoConContactos> =
        grupoDao.obtenerGrupoConContactos(grupoId)

    fun obtenerContactoConGrupos(contactoId: Int): LiveData<ContactoConGrupos> =
        grupoDao.obtenerContactoConGrupos(contactoId)

    fun obtenerTodosLosGruposConContactos(): LiveData<List<GrupoConContactos>> =
        grupoDao.obtenerTodosLosGruposConContactos()

    // --- Consultas adicionales ---
    suspend fun contarContactosEnGrupo(grupoId: Int): Int =
        grupoDao.contarContactosEnGrupo(grupoId)

    fun obtenerContactosDeGrupo(grupoId: Int): LiveData<List<Contacto>> =
        grupoDao.obtenerContactosDeGrupo(grupoId)

    fun obtenerGruposDeContacto(contactoId: Int): LiveData<List<Grupo>> =
        grupoDao.obtenerGruposDeContacto(contactoId)

    suspend fun categoriaExiste(id: Int): Boolean {
        return categoriaDao.existeCategoria(id) > 0
    }





}
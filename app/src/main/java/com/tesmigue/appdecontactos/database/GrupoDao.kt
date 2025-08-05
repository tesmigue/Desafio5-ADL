package com.tesmigue.contacto.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.tesmigue.contacto.model.Contacto
import com.tesmigue.contacto.model.ContactoConGrupos
import com.tesmigue.contacto.model.ContactoGrupoCrossRef
import com.tesmigue.contacto.model.Grupo
import com.tesmigue.contacto.model.GrupoConContactos

@Dao
interface GrupoDao {

    // CRUD para grupos
    @Insert
    suspend fun insertarGrupo(grupo: Grupo)

    @Update
    suspend fun actualizarGrupo(grupo: Grupo)

    @Delete
    suspend fun eliminarGrupo(grupo: Grupo)

    @Query("SELECT * FROM grupos ORDER BY nombre ASC")
    fun obtenerGrupos(): LiveData<List<Grupo>>

    // Relación contacto-grupo
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarContactoGrupoCrossRef(ref: ContactoGrupoCrossRef)

    @Delete
    suspend fun eliminarContactoGrupoCrossRef(ref: ContactoGrupoCrossRef)

    // Obtener grupo con sus contactos
    @Transaction
    @Query("SELECT * FROM grupos WHERE id = :grupoId")
    fun obtenerGrupoConContactos(grupoId: Int): LiveData<GrupoConContactos>

    // Obtener todos los grupos con sus contactos
    @Transaction
    @Query("SELECT * FROM grupos ORDER BY nombre ASC")
    fun obtenerTodosLosGruposConContactos(): LiveData<List<GrupoConContactos>>

    // Obtener contacto con sus grupos (relación inversa)
    @Transaction
    @Query("SELECT * FROM contactos WHERE id = :contactoId")
    fun obtenerContactoConGrupos(contactoId: Int): LiveData<ContactoConGrupos>

    // Consultas adicionales útiles
    @Query("SELECT COUNT(*) FROM contactos c INNER JOIN ContactoGrupoCrossRef cgr ON c.id = cgr.contactoId WHERE cgr.grupoId = :grupoId")
    suspend fun contarContactosEnGrupo(grupoId: Int): Int

    @Query("SELECT * FROM contactos c INNER JOIN ContactoGrupoCrossRef cgr ON c.id = cgr.contactoId WHERE cgr.grupoId = :grupoId")
    fun obtenerContactosDeGrupo(grupoId: Int): LiveData<List<Contacto>>

    @Query("SELECT * FROM grupos g INNER JOIN ContactoGrupoCrossRef cgr ON g.id = cgr.grupoId WHERE cgr.contactoId = :contactoId")
    fun obtenerGruposDeContacto(contactoId: Int): LiveData<List<Grupo>>
}
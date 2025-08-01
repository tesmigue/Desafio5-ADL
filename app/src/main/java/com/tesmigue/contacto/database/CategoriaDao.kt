package com.tesmigue.contactos.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.tesmigue.contactos.model.Categoria

@Dao
interface CategoriaDao {
    @Query("SELECT * FROM categorias")
    fun obtenerCategorias(): LiveData<List<Categoria>>

    @Insert
    suspend fun insertar(categoria: Categoria)
}

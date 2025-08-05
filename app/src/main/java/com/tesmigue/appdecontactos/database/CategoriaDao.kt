package com.tesmigue.contacto.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.tesmigue.contacto.model.Categoria

@Dao
interface CategoriaDao {
    @Query("SELECT * FROM categorias")
    fun obtenerCategorias(): LiveData<List<Categoria>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertar(categoria: Categoria)


    @Query("SELECT COUNT(*) FROM categorias WHERE id = :categoriaId")
    suspend fun existeCategoria(categoriaId: Int): Int

}

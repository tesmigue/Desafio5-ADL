package com.tesmigue.contactos.database

import android.content.Context
import androidx.room.*
import com.tesmigue.contactos.model.*

@Database(
    entities = [
        Contacto::class,
        Categoria::class,
        Grupo::class,
        ContactoGrupoCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters() // Agregar si necesitas convertidores personalizados
abstract class ContactosDatabase : RoomDatabase() {

    abstract fun contactoDao(): ContactoDao
    abstract fun categoriaDao(): CategoriaDao
    abstract fun grupoDao(): GrupoDao

    companion object {
        @Volatile
        private var INSTANCE: ContactosDatabase? = null

        fun getDatabase(context: Context): ContactosDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ContactosDatabase::class.java,
                    "contactos_db"
                )
                    .fallbackToDestructiveMigration() // Solo para desarrollo
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
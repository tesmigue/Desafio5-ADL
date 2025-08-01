package com.tesmigue.contacto.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "contactos",
    foreignKeys = [ForeignKey(
        entity = Categoria::class,
        parentColumns = ["id"],
        childColumns = ["categoriaId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["categoriaId"])]
)
data class Contacto(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val telefono: String,
    val email: String,
    val categoriaId: Int
)

package com.tesmigue.contacto.model

import androidx.room.Entity

@Entity(primaryKeys = ["contactoId", "grupoId"])
data class ContactoGrupoCrossRef(
    val contactoId: Int,
    val grupoId: Int
)
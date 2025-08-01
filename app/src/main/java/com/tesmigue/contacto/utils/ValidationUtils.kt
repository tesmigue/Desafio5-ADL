package com.sakhura.contactos.utils

object ValidationUtils {
    fun validarFormulario(nombre: String, telefono: String, email: String): Boolean {
        return nombre.isNotBlank() &&
                telefono.matches(Regex("^\\+?[0-9]{7,15}$")) &&
                email.matches(Regex("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$"))
    }
}

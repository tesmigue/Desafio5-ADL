package com.tesmigue.contacto

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.tesmigue.contacto.adapter.ContactosAdapter
import com.tesmigue.contacto.databinding.ActivityMainBinding
import com.tesmigue.contacto.viewmodel.ContactosViewModel
import com.tesmigue.contacto.model.Contacto
import java.io.File
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken



class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ContactosViewModel
    private lateinit var contactosAdapter: ContactosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarViewModel()
        configurarToolbar()
        configurarRecyclerView()
        configurarFAB()
        observarDatos()
    }



    private fun configurarViewModel() {
        viewModel = ViewModelProvider(this)[ContactosViewModel::class.java]
    }

    private fun configurarToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Mis Contactos"
    }

    private fun configurarRecyclerView() {
        contactosAdapter = ContactosAdapter(emptyList()) { contacto ->
            // Manejar click en contacto
            mostrarOpcionesContacto(contacto)
        }

        binding.recyclerViewContactos.apply {
            adapter = contactosAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun configurarFAB() {
        binding.fabAgregarContacto.setOnClickListener {
            abrirAgregarContacto()
        }
    }

    private fun observarDatos() {
        // Observar lista de contactos
        viewModel.contactos.observe(this) { contactos ->
            contactos?.let {
                contactosAdapter.actualizarLista(it)

                // Mostrar mensaje si no hay contactos
                if (it.isEmpty()) {
                    binding.textViewNoContactos.visibility = android.view.View.VISIBLE
                    binding.recyclerViewContactos.visibility = android.view.View.GONE
                } else {
                    binding.textViewNoContactos.visibility = android.view.View.GONE
                    binding.recyclerViewContactos.visibility = android.view.View.VISIBLE
                }
            }
        }

        // Observar resultados de búsqueda si hay texto en el SearchView
        viewModel.contactosFiltrados.observe(this) { contactosFiltrados ->
            contactosFiltrados?.let {
                contactosAdapter.actualizarLista(it)
            }
        }
    }

    private fun mostrarOpcionesContacto(contacto: Contacto) {
        val opciones = arrayOf("Ver detalles", "Editar", "Eliminar", "Compartir")

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(contacto.nombre)
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> verDetallesContacto(contacto)
                    1 -> editarContacto(contacto)
                    2 -> confirmarEliminarContacto(contacto)
                    3 -> compartirContacto(contacto)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun verDetallesContacto(contacto: Contacto) {
        val mensaje = """
            Nombre: ${contacto.nombre}
            Teléfono: ${contacto.telefono}
            Email: ${contacto.email}
        """.trimIndent()

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Detalles del Contacto")
            .setMessage(mensaje)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun editarContacto(contacto: Contacto) {
        val intent = Intent(this, AgregarContactoActivity::class.java).apply {
            putExtra("CONTACTO_ID", contacto.id)
            putExtra("CONTACTO_NOMBRE", contacto.nombre)
            putExtra("CONTACTO_TELEFONO", contacto.telefono)
            putExtra("CONTACTO_EMAIL", contacto.email)
            putExtra("MODO_EDICION", true)
        }
        startActivity(intent)
    }

    private fun confirmarEliminarContacto(contacto: Contacto) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Eliminar Contacto")
            .setMessage("¿Estás seguro de que quieres eliminar a ${contacto.nombre}?")
            .setPositiveButton("Eliminar") { _, _ ->
                viewModel.eliminar(contacto)
                Toast.makeText(this, "${contacto.nombre} eliminado", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun compartirContacto(contacto: Contacto) {
        val compartirTexto = """
            Contacto: ${contacto.nombre}
            Teléfono: ${contacto.telefono}
            Email: ${contacto.email}
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, compartirTexto)
            putExtra(Intent.EXTRA_SUBJECT, "Contacto: ${contacto.nombre}")
        }

        startActivity(Intent.createChooser(intent, "Compartir contacto"))
    }

    private fun abrirAgregarContacto() {
        val intent = Intent(this, AgregarContactoActivity::class.java)
        startActivity(intent)
    }

    private fun abrirGestionarGrupos() {
        val intent = Intent(this, AgregarGrupoActivity::class.java)
        startActivity(intent)
    }


    private fun exportarContactos() {
        val contactos = viewModel.contactos.value ?: emptyList()

        if (contactos.isEmpty()) {
            Toast.makeText(this, "No hay contactos para exportar", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val gson = com.google.gson.Gson()
            val json = gson.toJson(contactos)

            val archivoBackup = File(filesDir, "backup_contactos.json")
            archivoBackup.writeText(json)

            Toast.makeText(this, "Contactos exportados a ${archivoBackup.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al exportar contactos", Toast.LENGTH_SHORT).show()
        }
    }


    private fun importarContactos() {
        try {
            val archivoBackup = File(filesDir, "backup_contactos.json")

            if (!archivoBackup.exists()) {
                Toast.makeText(this, "No se encontró el archivo de respaldo", Toast.LENGTH_SHORT).show()
                return
            }

            val json = archivoBackup.readText()

            val gson = Gson()
            val tipoLista = object : TypeToken<List<Contacto>>() {}.type
            val contactosImportados: List<Contacto> = gson.fromJson(json, tipoLista)

            if (contactosImportados.isNotEmpty()) {
                contactosImportados.forEach { contacto ->
                    val contactoSinId = contacto.copy(id = 0) // forzar id 0 para que Room genere uno nuevo
                    viewModel.insertar(contactoSinId)
                }
                Toast.makeText(this, "Importados ${contactosImportados.size} contactos", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "El archivo está vacío", Toast.LENGTH_SHORT).show()
            }


        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al importar: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        // Configurar SearchView
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.buscar(newText ?: "")
                return true
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> true
            R.id.action_grupos -> {
                abrirGestionarGrupos()  // <-- Aquí se llama a abrirGestionarGrupos()
                true
            }
            R.id.action_exportar -> {
                exportarContactos()
                true
            }
            R.id.action_importar -> {
                importarContactos()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
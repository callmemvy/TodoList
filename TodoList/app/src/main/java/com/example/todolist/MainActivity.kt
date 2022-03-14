package com.example.todolist

import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.todolist.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var db: Database
    private lateinit var adapter: NotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Room.databaseBuilder(applicationContext, Database::class.java, "notes").build()

        val sharedPreferences = getPreferences(MODE_PRIVATE)
        if (!sharedPreferences.getBoolean("was_started_before", false)) {
            CoroutineScope(Dispatchers.Default).launch {
                with(sharedPreferences.edit()) {
                    putBoolean("was_started_before", true)
                    commit()
                }

                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.first_start),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        adapter = NotesAdapter(applicationContext) { note ->
            CoroutineScope(Dispatchers.Default).launch {
                db.notesDao().delete(note)

                CoroutineScope(Dispatchers.Main).launch {
                    val idx = adapter.notes.indexOf(note)

                    adapter.notes.removeAt(idx)
                    adapter.notifyItemRemoved(idx)
                }
            }
        }

        with(binding.recyclerView) {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = this@MainActivity.adapter
            setHasFixedSize(true)
        }

        CoroutineScope(Dispatchers.Default).launch {
            val loadedNotes = db.notesDao().getAll()

            if (loadedNotes.isNotEmpty()) {
                CoroutineScope(Dispatchers.Main).launch {
                    adapter.notes.addAll(loadedNotes)
                    adapter.notifyItemRangeInserted(0, loadedNotes.size)
                }
            }
        }

        binding.addButton.setOnClickListener {
            val dialog = AddDialogFragment()

            dialog.addNote = { note ->
                CoroutineScope(Dispatchers.Default).launch {
                    val newNoteId = db.notesDao().add(note)

                    val newNote = note.copy(id = newNoteId)
                    CoroutineScope(Dispatchers.Main).launch {

                        var idx = 0
                        while (idx < adapter.notes.size && adapter.notes[idx] < newNote) {
                            ++idx
                        }

                        adapter.notes.add(idx, newNote)
                        adapter.notifyItemInserted(idx)
                    }
                }
            }

            dialog.show(supportFragmentManager, "addNote")
        }

        binding.listExternalStorageButton.setOnClickListener {
            if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.external_storage_not_mounted),
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val storage = Environment.getExternalStorageDirectory()
                    val files = storage.listFiles()?.toList() ?: throw IOException("null files list")

                    val lines = files.map {
                        val isDir = it.isDirectory
                        val prefix = if (isDir) {
                            getString(R.string.dir_file_prefix)
                        } else {
                            getString(R.string.regular_file_prefix)
                        }

                        "$prefix${it.absolutePath}"
                    }.toMutableList()

                    lines.add(getString(R.string.total_files_line, files.size))

                    if (Build.VERSION.SDK_INT >= 30) {
                        if (!Environment.isExternalStorageManager(storage)) {
                            lines.add(getString(R.string.has_not_manage_permissions))
                        }
                    }

                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(
                            applicationContext,
                            lines.joinToString(System.lineSeparator()),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()

                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.cannot_access_external_storage),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}

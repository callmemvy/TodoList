package com.example.todolist

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.todolist.databinding.AddDialogBinding

class AddDialogFragment: DialogFragment() {

    private lateinit var binding: AddDialogBinding

    var addNote: ((note: Note) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = with(AlertDialog.Builder(activity)) {
        binding = AddDialogBinding.inflate(layoutInflater)
        setView(binding.root)

        setTitle(getString(R.string.add_new_note_title))
        setIcon(android.R.drawable.ic_input_add)
        setPositiveButton(getString(R.string.ok_add_new_note_button)) { _, _ ->
            addNote?.invoke(Note(
                content = binding.contentEditText.text.toString(),
                priority = binding.priorityEditText.text.toString().toIntOrNull(),
            ))
        }
        setNegativeButton(getString(R.string.cancel_add_new_note_button), null)
        create()
    }
}

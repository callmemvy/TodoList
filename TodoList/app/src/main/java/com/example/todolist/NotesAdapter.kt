package com.example.todolist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.databinding.ListItemBinding
import java.io.Serializable

class NotesAdapter(
    private val applicationContext: Context,
    private val onDeleteClick: (note: Note) -> Unit,
): RecyclerView.Adapter<NotesAdapter.NoteViewHolder>(), Serializable {

    val notes = mutableListOf<Note>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        val view = inflater.inflate(R.layout.list_item, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) = holder.bind(notes[position])
    override fun getItemCount() = notes.size

    inner class NoteViewHolder(root: View): RecyclerView.ViewHolder(root) {

        private val binding = ListItemBinding.bind(root)

        fun bind(note: Note) {
            with(binding) {
                if (note.priority == null) {
                    priorityView.visibility = View.GONE
                } else {
                    priorityView.visibility = View.VISIBLE
                    priorityView.text = applicationContext.getString(R.string.note_priority, note.priority)
                }

                contentView.text = note.content

                deleteButton.setOnClickListener {
                    onDeleteClick(note)
                }
            }
        }
    }
}

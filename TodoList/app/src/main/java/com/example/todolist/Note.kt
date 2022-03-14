package com.example.todolist

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "content")
    val content: String,
    @ColumnInfo(name = "priority")
    val priority: Int?,
): Comparable<Note> {

    override fun compareTo(other: Note): Int = when {
        this.priority != other.priority -> when {
            this.priority == null -> 1
            other.priority == null -> -1
            else -> this.priority - other.priority
        }
        else -> (this.id - other.id).toInt()
    }
}

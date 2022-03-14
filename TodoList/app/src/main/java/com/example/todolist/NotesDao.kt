package com.example.todolist

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NotesDao {

    @Query("SELECT * FROM note ORDER BY priority IS NULL, priority, id")
    suspend fun getAll(): List<Note>

    @Insert
    suspend fun addAll(vararg notes: Note): List<Long>

    @Insert
    suspend fun add(note: Note): Long

    @Delete
    suspend fun delete(vararg notes: Note)
}

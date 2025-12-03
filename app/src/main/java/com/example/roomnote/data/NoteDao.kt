package com.example.roomnote.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    // Mengambil semua data, diurutkan dari ID terbesar (terbaru)
    @Query("SELECT * FROM notes ORDER BY id DESC")
    fun getAllNotes(): Flow<List<Note>>

    // Mengambil 1 data berdasarkan ID
    @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): Note?

    // Menambah data. Jika konflik, data lama diganti (REPLACE)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note): Long

    // Mengupdate data
    @Update
    suspend fun update(note: Note)

    // Menghapus data
    @Delete
    suspend fun delete(note: Note)
}
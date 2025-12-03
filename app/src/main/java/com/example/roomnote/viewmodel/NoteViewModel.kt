package com.example.roomnote.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomnote.data.Note
import com.example.roomnote.data.NoteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NoteViewModel(private val repo: NoteRepository) : ViewModel() {

    // Mengambil data secara real-time (Flow) dan diubah jadi StateFlow untuk Compose
    val notes: StateFlow<List<Note>> = repo.getAllNotes()
        .stateIn(viewModelScope, SharingStarted.Companion.Lazily, emptyList())

    fun insert(title: String, description: String) = viewModelScope.launch {
        // Validasi sederhana: jangan simpan jika judul & deskripsi kosong
        if (title.isBlank() && description.isBlank()) return@launch

        repo.insert(Note(title = title.trim(), description = description.trim()))
    }

    fun update(id: Long, title: String, description: String) = viewModelScope.launch {
        if (id <= 0) return@launch // Validasi ID
        repo.update(Note(id = id, title = title.trim(), description = description.trim()))
    }

    fun delete(note: Note) = viewModelScope.launch {
        repo.delete(note)
    }
}
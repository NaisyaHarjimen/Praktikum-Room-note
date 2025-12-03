package com.example.roomnote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.roomnote.data.AppDatabase
import com.example.roomnote.data.NoteRepository
import com.example.roomnote.viewmodel.NoteViewModel
import com.example.roomnote.viewmodel.NoteViewModelFactory
import com.example.roomnote.ui.theme.RoomNoteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getDatabase(applicationContext)
        val repo = NoteRepository(db.noteDao())
        val factory = NoteViewModelFactory(repo)

        setContent {
            RoomNoteTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val vm: NoteViewModel = viewModel(factory = factory)
                    NoteScreen(vm)
                }
            }
        }
    }
}
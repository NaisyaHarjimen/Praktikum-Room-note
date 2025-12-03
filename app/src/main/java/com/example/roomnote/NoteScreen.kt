package com.example.roomnote

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roomnote.data.Note
import com.example.roomnote.viewmodel.NoteViewModel

// Daftar warna pastel untuk catatan
val noteColors = listOf(
    Color(0xFFFFF59D), // Kuning
    Color(0xFF80DEEA), // Biru Cyan
    Color(0xFFF48FB1), // Pink
    Color(0xFFA5D6A7), // Hijau
    Color(0xFFCE93D8), // Ungu
    Color(0xFFFFCC80)  // Oranye
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteScreen(vm: NoteViewModel) {
    val notes by vm.notes.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var editingId by remember { mutableStateOf<Long?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background, // Background utama aplikasi
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingId = null
                    title = ""
                    desc = ""
                    showDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Note")
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // --- HEADER MODERN ---
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Catatan Saya",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 32.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "${notes.size} catatan tersimpan",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(20.dp))

            // --- KONTEN GRID (2 KOLOM) ---
            if (notes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Masih kosong nih...", color = Color.Gray)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2), // Membuat 2 kolom
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp) // Ruang untuk FAB
                ) {
                    items(notes) { note ->
                        // Pilih warna berdasarkan ID (agar warna tetap sama untuk note yg sama)
                        val colorIndex = (note.id % noteColors.size).toInt()
                        val bgColor = noteColors[colorIndex]

                        NoteGridItem(
                            note = note,
                            backgroundColor = bgColor,
                            onEdit = {
                                editingId = note.id
                                title = note.title
                                desc = note.description
                                showDialog = true
                            },
                            onDelete = { vm.delete(note) }
                        )
                    }
                }
            }
        }

        // --- POPUP DIALOG INPUT ---
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(if (editingId == null) "Tulis Sesuatu" else "Edit Catatan") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            placeholder = { Text("Judul...") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = desc,
                            onValueChange = { desc = it },
                            placeholder = { Text("Isi catatanmu di sini...") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 4,
                            maxLines = 6,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (title.isNotBlank() || desc.isNotBlank()) {
                            if (editingId == null) {
                                vm.insert(title, desc)
                            } else {
                                vm.update(editingId!!, title, desc)
                            }
                            showDialog = false
                        }
                    }) { Text("Simpan") }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) { Text("Batal") }
                }
            )
        }
    }
}

@Composable
fun NoteGridItem(
    note: Note,
    backgroundColor: Color,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    // Card dengan bentuk Sticky Note
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp) // Tinggi minimal agar kotak terlihat proporsional
            .clickable { onEdit() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black.copy(alpha = 0.8f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = note.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black.copy(alpha = 0.6f),
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Tombol hapus kecil di pojok bawah
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.White.copy(alpha = 0.4f),
                    modifier = Modifier.size(32.dp).clickable { onDelete() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Hapus",
                            modifier = Modifier.size(16.dp),
                            tint = Color.Black.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}
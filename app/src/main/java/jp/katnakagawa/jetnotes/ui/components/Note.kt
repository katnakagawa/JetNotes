package jp.katnakagawa.jetnotes.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jp.katnakagawa.jetnotes.domain.model.NoteModel
import jp.katnakagawa.jetnotes.util.fromHex

@ExperimentalMaterialApi
@Composable
fun NotesList(
    notes: List<NoteModel>,
    onNoteCheckedChange: (NoteModel) -> Unit,
    onNoteClick: (NoteModel) -> Unit
) {
    LazyColumn {
        items(
            items = notes,
            itemContent = { note ->
                val bottomPadding = if (notes.last() == note) 72.dp else 8.dp
                Note(
                    modifier = Modifier.padding(bottom = bottomPadding),
                    note = note,
                    onNoteClick = onNoteClick,
                    onNoteCheckedChange = onNoteCheckedChange
                )
            }
        )
    }
}

@ExperimentalMaterialApi
@Composable
fun Note(
    modifier: Modifier = Modifier,
    note: NoteModel,
    onNoteClick: (NoteModel) -> Unit = {},
    onNoteCheckedChange: (NoteModel) -> Unit = {},
    isSelected: Boolean = false
) {
    val background = if (isSelected)
        Color.LightGray
    else
        MaterialTheme.colors.surface

    Card(
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .then(modifier),
        backgroundColor = background
    ) {
        ListItem(
            text = { Text(text = note.title, maxLines = 1) },
            secondaryText = { Text(text = note.content, maxLines = 1) },
            icon = {
               NoteColor(
                   color = Color.fromHex(note.color.hex),
                   size = 40.dp,
                   border = 1.dp
               )
            },
            trailing = {
               if (note.isCheckedOff != null) {
                   Checkbox(
                       checked = note.isCheckedOff,
                       onCheckedChange = { isChecked ->
                           val newNote = note.copy(isCheckedOff = isChecked)
                           onNoteCheckedChange.invoke(newNote)
                       },
                       modifier = Modifier.padding(start = 8.dp)
                   )
               }
            },
            modifier = Modifier.clickable { onNoteClick.invoke(note) }
        )
    }
}

@ExperimentalMaterialApi
@Preview
@Composable
private fun NotePreview() {
    Note(note = NoteModel(1, "Note 1", "Content 1", null))
}
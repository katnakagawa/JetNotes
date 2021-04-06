package jp.katnakagawa.jetnotes.ui.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jp.katnakagawa.jetnotes.R
import jp.katnakagawa.jetnotes.domain.model.ColorModel
import jp.katnakagawa.jetnotes.domain.model.NEW_NOTE_ID
import jp.katnakagawa.jetnotes.domain.model.NoteModel
import jp.katnakagawa.jetnotes.routing.JetNotesRouter
import jp.katnakagawa.jetnotes.routing.Screen
import jp.katnakagawa.jetnotes.ui.components.NoteColor
import jp.katnakagawa.jetnotes.util.fromHex
import jp.katnakagawa.jetnotes.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun SaveNoteScreen(viewModel: MainViewModel) {

    val noteEntry: NoteModel by viewModel
        .noteEntry
        .observeAsState(initial = NoteModel())

    val colors: List<ColorModel> by viewModel
        .colors
        .observeAsState(initial = listOf())

    val bottomDrawerState: BottomDrawerState =
        rememberBottomDrawerState(initialValue = BottomDrawerValue.Closed)

    val moveNoteToTrashDialogShownState: MutableState<Boolean> = rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            val isEditingMode: Boolean = noteEntry.id != NEW_NOTE_ID
            SaveNoteTopAppBar(
                isEditingMode = isEditingMode,
                onBackClick = {
                    JetNotesRouter.navigateTo(Screen.Notes)
                },
                onSaveNoteClick = { viewModel.saveNote(noteEntry) },
                onOpenColorPickerClick = { scope.launch { bottomDrawerState.open() }  },
                onDeleteNoteClick = {
                    moveNoteToTrashDialogShownState.value = true
                }
            )
        },
        content = {
            BottomDrawer(
                drawerContent = {
                    ColorPicker(
                        colors = colors,
                        onColorSelect = { color ->
                            val newNoteEntry = noteEntry.copy(color = color)
                            viewModel.onNoteEntryChange(newNoteEntry)
                        }
                    )
                },
                drawerState = bottomDrawerState,
                content = {
                    SaveNoteContent(
                        note = noteEntry,
                        onNoteChange = { updateNoteEntry ->
                            viewModel.onNoteEntryChange(updateNoteEntry)
                        }
                    )

                    if (moveNoteToTrashDialogShownState.value) {
                        AlertDialog(
                            onDismissRequest = {
                                moveNoteToTrashDialogShownState.value = false
                            },
                            title = {
                                Text(text = "Move note to the trash?")
                            },
                            text = {
                                Text(text = "Are you sure you want to " +
                                        "move this note to the trash?")
                            },
                            confirmButton = {
                                TextButton(onClick = {
                                    viewModel.moveNoteToTrash(noteEntry)
                                }) {
                                    Text("Confirm")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = {
                                    moveNoteToTrashDialogShownState.value = false
                                }) {
                                    Text(text = "Dismiss")
                                }
                            }
                        )
                    }
                }
            )
        }
    )
}

@Composable
private fun SaveNoteTopAppBar(
    isEditingMode: Boolean,
    onBackClick: () -> Unit,
    onSaveNoteClick: () -> Unit,
    onOpenColorPickerClick: () -> Unit,
    onDeleteNoteClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "Save Note",
                color = MaterialTheme.colors.onPrimary
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "")
            }
        },
        actions =  {
            // Save note action icon
            IconButton(onClick = onSaveNoteClick) {
                Icon(
                    imageVector = Icons.Default.Check, 
                    contentDescription = "",
                    tint = MaterialTheme.colors.onPrimary
                )
            }
            
            // Open color picker action icon
            IconButton(onClick = onOpenColorPickerClick) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.abc_vector_test),
                    contentDescription = "",
                    tint = MaterialTheme.colors.onPrimary
                )
            }

            if (isEditingMode) {
                IconButton(onClick = onDeleteNoteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "",
                        tint = MaterialTheme.colors.onPrimary
                    )
                }
            }
        }
    )
}

@Composable
private fun ColorPicker(
    colors: List<ColorModel>,
    onColorSelect: (ColorModel) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Color picker",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(items = colors, itemContent = { color ->
                ColorItem(color, onColorSelect)
            })
        }
    }
}

@Composable
fun ColorItem(
    color: ColorModel,
    onColorSelect: (ColorModel) -> Unit
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable(
            onClick = {
                onColorSelect.invoke(color)
            }
        )
    ) {
        NoteColor(
            color = Color.fromHex(color.hex),
            size = 80.dp,
            border = 2.dp,
            modifier = Modifier.padding(10.dp)
        )
        Text(
            text = color.name,
            fontSize = 22.sp,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .align(Alignment.CenterVertically)
        )
    }
}

@Composable
private fun SaveNoteContent(
    note: NoteModel,
    onNoteChange: (NoteModel) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        ContentTextField(
            label = "Title",
            text = note.title,
            onTextChange = { newTitle ->
                onNoteChange.invoke(note.copy(title = newTitle))
            }
        )

        ContentTextField(
            modifier = Modifier
                .heightIn(max = 240.dp)
                .padding(top = 16.dp),
            label = "Body",
            text = note.content,
            onTextChange = { newContent ->
                onNoteChange.invoke(note.copy(content = newContent))
            }
        )

        val canBeCheckedOff: Boolean = note.isCheckedOff != null
        NoteCheckOption(
            isChecked = canBeCheckedOff,
            onCheckedChange = { canBeCheckedOffNewValue ->
                val isCheckedOff: Boolean? = if (canBeCheckedOffNewValue) false else null
                onNoteChange.invoke(note.copy(isCheckedOff = isCheckedOff))
            }
        )

        PickedColor(color = note.color)
    }
}

@Composable
private fun ContentTextField(
    modifier: Modifier = Modifier,
    label: String,
    text: String,
    onTextChange: (String) -> Unit
) {
    TextField(
        value = text,
        onValueChange = onTextChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        label = { Text(label) }
    )
}

@Composable
private fun NoteCheckOption(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(modifier = Modifier
        .padding(8.dp)
        .padding(top = 16.dp)
    ) {
        Text(
            text = "Can note be checked off?",
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
private fun PickedColor(color: ColorModel) {
    Row(modifier = Modifier
        .padding(8.dp)
        .padding(16.dp)
    ) {
        Text(
            text = "Picked color",
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        )
        NoteColor(
            color = Color.fromHex(color.hex),
            size = 40.dp,
            border = 1.dp,
            modifier = Modifier.padding(4.dp)
        )
    }
}
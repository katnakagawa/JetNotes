package jp.katnakagawa.jetnotes.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import jp.katnakagawa.jetnotes.domain.model.NoteModel
import jp.katnakagawa.jetnotes.viewmodel.MainViewModel
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import jp.katnakagawa.jetnotes.R
import jp.katnakagawa.jetnotes.routing.Screen
import jp.katnakagawa.jetnotes.ui.components.AppDrawer
import jp.katnakagawa.jetnotes.ui.components.Note
import kotlinx.coroutines.launch
import java.lang.IllegalStateException


private const val NO_DIALOG = 1
private const val RESTORE_NOTES_DIALOG = 2
private const val PERMANENTLY_DELETE_DIALOG = 3

@ExperimentalMaterialApi
@Composable
fun TrashScreen(viewModel: MainViewModel) {

    val notesInTrash: List<NoteModel> by viewModel
        .notesInTrash
        .observeAsState(initial = listOf())

    val selectedNotes: List<NoteModel> by viewModel
        .selectedNotes
        .observeAsState(initial = listOf())

    val dialog: MutableState<Int> = rememberSaveable { mutableStateOf(NO_DIALOG) }

    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            val areActionsVisible = selectedNotes.isNotEmpty()
            TrashTopAppBar(
                onNavigationIconClick = { scope.launch { scaffoldState.drawerState.open() } },
                onRestoreNotesClick = { dialog.value = RESTORE_NOTES_DIALOG },
                onDeleteNotesClick = { dialog.value = PERMANENTLY_DELETE_DIALOG },
                areActionsVisible = areActionsVisible
            )
        },
        scaffoldState = scaffoldState,
        drawerContent = {
            AppDrawer(
                currentScreen = Screen.Trash,
                closeDrawerAction = { scope.launch { scaffoldState.drawerState.close() } }
            )
        },
        content = {
            Content(
                notes = notesInTrash,
                onNoteClick = { viewModel.onNoteSelected(it) },
                selectedNotes = selectedNotes
            )

            if (dialog.value != NO_DIALOG) {
                val confirmAction: () -> Unit = when (dialog.value) {
                    RESTORE_NOTES_DIALOG -> {
                        {
                            viewModel.restoreNotes(selectedNotes)
                            dialog.value = NO_DIALOG
                        }
                    }
                    PERMANENTLY_DELETE_DIALOG -> {
                        {
                            viewModel.permanentlyDeleteNotes(selectedNotes)
                            dialog.value = NO_DIALOG
                        }
                    }
                    else -> {
                        {
                            dialog.value = NO_DIALOG
                        }
                    }
                }

                AlertDialog(
                    onDismissRequest = {
                        dialog.value = NO_DIALOG
                    },
                    title = { Text(text = mapDialogTitle(dialog.value)) },
                    text = { Text(text = mapDialogText(dialog.value)) },
                    confirmButton = {
                        TextButton(onClick = confirmAction) {
                            Text(text = "Confirm")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { dialog.value = NO_DIALOG }) {
                            Text(text = "Dismiss")
                        }
                    }
                )
            }
        }
    )
}

@Composable
private fun TrashTopAppBar(
    onNavigationIconClick: () -> Unit,
    onRestoreNotesClick: () -> Unit,
    onDeleteNotesClick: () -> Unit,
    areActionsVisible: Boolean
) {
    TopAppBar(
        title = { Text(text = "Trash", color = MaterialTheme.colors.onPrimary) },
        navigationIcon = {
            IconButton(onClick = onNavigationIconClick) {
                Icon(imageVector = Icons.Filled.List, contentDescription = "")
            }
        },
        actions = {
            if (areActionsVisible) {
                IconButton(onClick = onRestoreNotesClick) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.abc_vector_test),
                        contentDescription = "",
                        tint = MaterialTheme.colors.onPrimary
                    )
                }
                IconButton(onClick = onDeleteNotesClick) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.abc_vector_test),
                        contentDescription = "",
                        tint = MaterialTheme.colors.onPrimary
                    )
                }
            }
        }
    )
}

@ExperimentalMaterialApi
@Composable
private fun Content(
    notes: List<NoteModel>,
    onNoteClick: (NoteModel) -> Unit,
    selectedNotes: List<NoteModel>
) {
    val tabs = listOf("REGULAR", "CHECKABLE")

    // Init state for selected tab
    var selectedTab by remember {
        mutableStateOf(0)
    }

    Column {
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(text = title) },
                    selected = selectedTab ==index,
                    onClick = { selectedTab = index }
                )
            }
        }

        val filteredNotes = when (selectedTab) {
            0 -> {
                notes.filter { it.isCheckedOff == null }
            }
            1 -> {
                notes.filter { it.isCheckedOff != null }
            }
            else -> throw  IllegalStateException("Tab not supported - index: $selectedTab")
        }

        LazyColumn {
            items(
                items = filteredNotes,
                itemContent = { note ->
                    val isNoteSelected = selectedNotes.contains(note)
                    Note(
                        note = note,
                        onNoteClick = onNoteClick,
                        isSelected = isNoteSelected
                    )
                }
            )
        }
    }
}

private fun mapDialogTitle(dialog: Int): String = when (dialog) {
    RESTORE_NOTES_DIALOG -> "Restore notes"
    PERMANENTLY_DELETE_DIALOG -> "Delete notes forever"
    // else -> throw RuntimeException("Dialog not supported: $dialog")
    else -> ""
}

private fun mapDialogText(dialog: Int): String = when (dialog) {
    RESTORE_NOTES_DIALOG -> "Are you sure you want to restore selected notes?"
    PERMANENTLY_DELETE_DIALOG -> "Are you sure you want to delete selected notes permanently?"
    // else -> throw RuntimeException("Dialog not supported: $dialog")
    else -> ""
}
package jp.katnakagawa.jetnotes.ui.screens


import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import jp.katnakagawa.jetnotes.domain.model.NoteModel
import jp.katnakagawa.jetnotes.routing.Screen
import jp.katnakagawa.jetnotes.ui.components.AppDrawer
import jp.katnakagawa.jetnotes.ui.components.NotesList
import jp.katnakagawa.jetnotes.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun NotesScreen(viewModel: MainViewModel) {
    val notes: List<NoteModel> by viewModel
        .notesNotInTrash
        .observeAsState(listOf())

    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "JetNotes",
                        color = MaterialTheme.colors.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        scope.launch { scaffoldState.drawerState.open() }
                    }) {
                        Icon(imageVector = Icons.Filled.List, contentDescription = "")
                    }
                }
            )
        },
        scaffoldState = scaffoldState,
        drawerContent = {
            AppDrawer(
                currentScreen = Screen.Notes,
                closeDrawerAction = {
                    scope.launch { scaffoldState.drawerState.close() }
                }
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
           FloatingActionButton(
               onClick = { viewModel.onCreateNewNoteClick() },
               contentColor = MaterialTheme.colors.background,
               content = {
                   Icon(
                       imageVector = Icons.Filled.Add,
                       contentDescription = ""
                   )
               }
           )
        },
        content = {
            if (notes.isNotEmpty()) {
                NotesList(
                    notes = notes,
                    onNoteCheckedChange = { viewModel.onNoteCheckedChange(it) },
                    onNoteClick = { viewModel.onNoteClick(it) }
                )
            }
        }
    )
}

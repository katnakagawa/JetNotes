package jp.katnakagawa.jetnotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import jp.katnakagawa.jetnotes.routing.JetNotesRouter
import jp.katnakagawa.jetnotes.routing.Screen
import jp.katnakagawa.jetnotes.ui.screens.NotesScreen
import jp.katnakagawa.jetnotes.ui.screens.SaveNoteScreen
import jp.katnakagawa.jetnotes.ui.screens.TrashScreen
import jp.katnakagawa.jetnotes.ui.theme.JetNotesTheme
import jp.katnakagawa.jetnotes.viewmodel.MainViewModel
import jp.katnakagawa.jetnotes.viewmodel.MainViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels(factoryProducer = {
        MainViewModelFactory(
            this,
            (application as JetNotesApplication).dependencyInjector.repository
        )
    })

    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetNotesTheme {
                MainActivityScreen(viewModel = viewModel)
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun MainActivityScreen(viewModel: MainViewModel) {
    Surface {
        when (JetNotesRouter.currentScreen) {
            is Screen.Notes -> NotesScreen(viewModel)
            is Screen.SaveNote -> SaveNoteScreen(viewModel)
            is Screen.Trash -> TrashScreen(viewModel)
        }
    }
}

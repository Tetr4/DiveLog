package cloud.mike.divelog.ui.edit

import androidx.compose.runtime.getValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import cloud.mike.divelog.data.dives.Dive
import org.koin.androidx.compose.koinViewModel
import java.util.UUID

private const val EDIT_ROUTE = "edit"
private const val DIVE_ID = "diveId"

fun NavGraphBuilder.editScreen(
    onNavigateUp: () -> Unit,
    onShowDetail: (Dive) -> Unit,
) {
    composable(
        route = "$EDIT_ROUTE?$DIVE_ID={$DIVE_ID}",
        arguments = listOf(
            navArgument(DIVE_ID) {
                type = NavType.StringType
                nullable = true
            },
        ),
    ) {
        val viewModel: EditViewModel = koinViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        EditScreen(
            uiState = uiState,
            onClose = onNavigateUp,
            onShowDetail = onShowDetail,
            onFetchDive = viewModel::fetchDive,
            onSave = viewModel::save,
        )
    }
}

fun NavController.showEdit(dive: Dive) {
    navigate("$EDIT_ROUTE?$DIVE_ID=${dive.id}")
}

fun NavController.showAdd() {
    navigate(EDIT_ROUTE)
}

val SavedStateHandle.diveId: UUID?
    get() = get<String>(DIVE_ID)?.let { UUID.fromString(it) }

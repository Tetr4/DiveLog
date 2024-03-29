package cloud.mike.divelog.ui.detail

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

private const val DETAIL_ROUTE = "detail"
private const val DIVE_ID = "diveId"

fun NavGraphBuilder.detailScreen(
    onNavigateUp: () -> Unit,
    onShowEdit: (Dive) -> Unit,
) {
    composable(
        route = "$DETAIL_ROUTE/{$DIVE_ID}",
        arguments = listOf(
            navArgument(DIVE_ID) { type = NavType.StringType },
        ),
    ) {
        val viewModel: DetailViewModel = koinViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        DetailScreen(
            uiState = uiState,
            onNavigateUp = onNavigateUp,
            onShowEdit = onShowEdit,
            onFetchDive = viewModel::fetchDive,
            onDeleteDive = viewModel::deleteDive,
            onDismissDeleteError = viewModel::dismissDeleteError,
        )
    }
}

fun NavController.showDetail(dive: Dive) {
    navigate("$DETAIL_ROUTE/${dive.id}")
}

val SavedStateHandle.diveId: UUID
    get() = UUID.fromString(get<String>(DIVE_ID))

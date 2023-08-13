package cloud.mike.divelog.ui.home

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.paging.compose.collectAsLazyPagingItems
import cloud.mike.divelog.data.dives.Dive
import org.koin.androidx.compose.koinViewModel

object HomeNavigation {
    const val route = "home"
}

fun NavGraphBuilder.homeScreen(
    onShowDetail: (Dive) -> Unit,
) {
    composable(HomeNavigation.route) {
        val viewModel: HomeViewModel = koinViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val diveItems = viewModel.dives.collectAsLazyPagingItems()
        HomeScreen(
            uiState = uiState,
            diveItems = diveItems,
            onShowDetail = onShowDetail,
            onSearch = viewModel::search,
        )
    }
}

fun NavController.showHome() {
    navigate(HomeNavigation.route)
}
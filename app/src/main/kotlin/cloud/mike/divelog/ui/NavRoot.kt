package cloud.mike.divelog.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import cloud.mike.divelog.ui.detail.detailScreen
import cloud.mike.divelog.ui.detail.showDetail
import cloud.mike.divelog.ui.home.HomeNavigation
import cloud.mike.divelog.ui.home.homeScreen

@Composable
fun NavRoot() {
    val mainController = rememberNavController()
    NavHost(
        navController = mainController,
        startDestination = HomeNavigation.route,
    ) {
        homeScreen(
            onShowDetail = mainController::showDetail,
        )
        detailScreen(
            onShowEdit = {},
            onNavigateUp = mainController::navigateUp,
        )
    }
}

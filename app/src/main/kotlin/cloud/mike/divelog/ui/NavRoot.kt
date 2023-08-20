package cloud.mike.divelog.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import cloud.mike.divelog.ui.detail.detailScreen
import cloud.mike.divelog.ui.detail.showDetail
import cloud.mike.divelog.ui.edit.editScreen
import cloud.mike.divelog.ui.edit.showAdd
import cloud.mike.divelog.ui.edit.showEdit
import cloud.mike.divelog.ui.home.HOME_ROUTE
import cloud.mike.divelog.ui.home.homeScreen

@Composable
fun NavRoot() {
    val mainController = rememberNavController()
    NavHost(
        navController = mainController,
        startDestination = HOME_ROUTE,
    ) {
        homeScreen(
            onShowDetail = mainController::showDetail,
            onShowAdd = mainController::showAdd,
        )
        detailScreen(
            onShowEdit = mainController::showEdit,
            onNavigateUp = mainController::navigateUp,
        )
        editScreen(
            onNavigateUp = mainController::navigateUp,
            onShowDetail = mainController::showDetail,
        )
    }
}

package cloud.mike.divelog.ui.home

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import cloud.mike.divelog.App
import cloud.mike.divelog.R
import cloud.mike.divelog.data.dives.Dive
import cloud.mike.divelog.localization.errors.ErrorMessage
import cloud.mike.divelog.ui.DiveTheme
import cloud.mike.divelog.ui.create.CreateDiveSheet
import cloud.mike.divelog.ui.home.bottombar.HomeBottomBar
import cloud.mike.divelog.ui.home.filters.TagFilters
import cloud.mike.divelog.ui.home.list.DiveList
import cloud.mike.divelog.ui.home.search.SearchView
import cloud.mike.divelog.ui.imports.ImportSheet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

private val dragHandleVerticalPadding = 22.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeState,
    diveItems: LazyPagingItems<DiveItem>,
    onShowDetail: (Dive) -> Unit,
    onSearch: (query: String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val bluetoothSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val createDiveSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val snackbarHostState = remember { SnackbarHostState() }

    fun showBluetoothImport() = scope.launch { bluetoothSheetState.show() }
    fun showAddDive() = scope.launch { createDiveSheetState.show() }
    fun showDiveSpots() = Toast.makeText(context, "Map not implemented", Toast.LENGTH_SHORT).show()
    suspend fun showError(message: ErrorMessage) = snackbarHostState.showSnackbar(message.content)

    Scaffold(
        bottomBar = {
            HomeBottomBar(
                showBluetoothImport = ::showBluetoothImport,
                showDiveSpots = ::showDiveSpots,
                showAddDive = ::showAddDive,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { appBarsPadding ->
        SearchableList(
            modifier = Modifier
                .padding(appBarsPadding)
                .consumeWindowInsets(ScaffoldDefaults.contentWindowInsets)
                .safeDrawingPadding(),
            diveItems = diveItems,
            onDiveClicked = onShowDetail,
            query = uiState.query,
            onQueryChanged = onSearch,
        )
    }

    Sheets(
        bluetoothSheetState = bluetoothSheetState,
        createDiveSheetState = createDiveSheetState,
        onShowDetail = onShowDetail,
        onShowError = ::showError,
    )
}

@Composable
fun SearchableList(
    diveItems: LazyPagingItems<DiveItem>,
    onDiveClicked: (Dive) -> Unit,
    query: String,
    onQueryChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SearchView(
            modifier = Modifier.padding(horizontal = 16.dp),
            value = query,
            onValueChange = onQueryChanged,
            placeholder = stringResource(R.string.home_search_placeholder),
        )
        Spacer(Modifier.height(8.dp))
        if (App.SHOW_FILTERS) {
            TagFilters(modifier = Modifier.padding(horizontal = 16.dp))
        }
        DiveList(
            items = diveItems,
            // TODO long press to show quick delete, hide, edit, share, etc. in bottom bar?
            onDiveClicked = onDiveClicked,
            onRetry = diveItems::retry,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Sheets(
    bluetoothSheetState: SheetState,
    createDiveSheetState: SheetState,
    onShowDetail: (Dive) -> Unit,
    onShowError: suspend (ErrorMessage) -> Unit,
) {
    val scope = rememberCoroutineScope()
    if (!bluetoothSheetState.isCollapsed) {
        ModalBottomSheet(
            sheetState = bluetoothSheetState,
            onDismissRequest = {},
        ) {
            ImportSheet(
                modifier = Modifier
                    .heightIn(min = 192.dp)
                    .padding(bottom = dragHandleVerticalPadding),
                onShowError = onShowError,
            )
        }
    }
    if (!createDiveSheetState.isCollapsed) {
        ModalBottomSheet(
            sheetState = createDiveSheetState,
            onDismissRequest = {},
        ) {
            CreateDiveSheet(
                modifier = Modifier.padding(bottom = dragHandleVerticalPadding),
                onDiveCreated = { dive ->
                    scope.launch {
                        createDiveSheetState.hide()
                        onShowDetail(dive)
                    }
                },
                onShowError = onShowError,
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun Preview() {
    val pagingData: PagingData<DiveItem> = PagingData.from(
        data = Dive.samples.map(DiveItem::Item),
        sourceLoadStates = LoadStates(
            refresh = LoadState.NotLoading(endOfPaginationReached = true),
            append = LoadState.NotLoading(endOfPaginationReached = true),
            prepend = LoadState.NotLoading(endOfPaginationReached = true),
        ),
    )
    DiveTheme {
        HomeScreen(
            uiState = HomeState(),
            diveItems = MutableStateFlow(pagingData).collectAsLazyPagingItems(),
            onShowDetail = {},
            onSearch = {},
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private val SheetState.isCollapsed
    get() = currentValue == SheetValue.Hidden && targetValue == SheetValue.Hidden

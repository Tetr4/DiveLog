package cloud.mike.divelog.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
import cloud.mike.divelog.ui.home.filters.TagFilters
import cloud.mike.divelog.ui.home.list.DiveList
import cloud.mike.divelog.ui.home.search.SearchView
import cloud.mike.divelog.ui.imports.ImportSheet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeState,
    diveItems: LazyPagingItems<DiveItem>,
    onShowDetail: (Dive) -> Unit,
    onSearch: (query: String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val bluetoothSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    fun showAddDive() = Unit // TODO

    fun showBluetoothImport() {
        scope.launch { bluetoothSheetState.show() }
    }

    suspend fun showError(message: ErrorMessage) {
        snackbarHostState.showSnackbar(message.content)
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                title = { Text(stringResource(R.string.home_title)) },
                scrollBehavior = scrollBehavior,
            )
        },
        bottomBar = {
            HomeBottomBar(
                showBluetoothImport = ::showBluetoothImport,
                showAddDive = ::showAddDive,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { appBarsPadding ->
        SearchableList(
            modifier = Modifier
                .padding(appBarsPadding)
                .padding(top = 16.dp),
            diveItems = diveItems,
            onDiveClicked = onShowDetail,
            query = uiState.query,
            onQueryChanged = onSearch,
        )
    }

    if (!bluetoothSheetState.isCollapsed) {
        ModalBottomSheet(
            sheetState = bluetoothSheetState,
            onDismissRequest = { },
        ) {
            ImportSheet(
                modifier = Modifier.fillMaxHeight(0.6f),
                onShowError = ::showError,
            )
        }
    }
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
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SearchView(
            modifier = Modifier.padding(horizontal = 16.dp),
            value = query,
            onValueChange = onQueryChanged,
            placeholder = stringResource(R.string.home_search_placeholder),
        )
        if (App.SHOW_FILTERS) {
            TagFilters(
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
        DiveList(
            items = diveItems,
            onDiveClicked = onDiveClicked,
            onRetry = diveItems::retry,
        )
    }
}

@Composable
private fun HomeBottomBar(
    showBluetoothImport: () -> Unit,
    showAddDive: () -> Unit,
) {
    BottomAppBar(
        actions = {
            IconButton(onClick = showBluetoothImport) {
                Icon(Icons.Filled.Bluetooth, stringResource(R.string.home_button_bluetooth_import))
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = showAddDive) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.home_button_add_dive))
            }
        },
    )
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

package cloud.mike.divelog.ui.home.list

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import cloud.mike.divelog.data.dives.Dive
import cloud.mike.divelog.ui.DiveTheme
import cloud.mike.divelog.ui.home.DiveItem
import cloud.mike.divelog.ui.home.list.item.DateHeader
import cloud.mike.divelog.ui.home.list.item.DiveListItem
import cloud.mike.divelog.ui.home.list.states.InitialEmptyState
import cloud.mike.divelog.ui.home.list.states.InitialErrorState
import cloud.mike.divelog.ui.home.list.states.InitialLoadingState
import cloud.mike.divelog.ui.home.list.states.TrailingErrorState
import cloud.mike.divelog.ui.home.list.states.TrailingLoadingState
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDate

@Composable
fun DiveList(
    items: LazyPagingItems<DiveItem>,
    onDiveClicked: (Dive) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (val state = items.loadState.refresh) {
        is LoadState.Loading -> InitialLoadingState(modifier = modifier)
        is LoadState.Error -> InitialErrorState(
            modifier = modifier,
            error = state.error,
            onRetry = onRetry,
        )
        is LoadState.NotLoading -> if (items.itemSnapshotList.isEmpty()) {
            InitialEmptyState(modifier = modifier)
        } else {
            ContentState(
                modifier = modifier,
                items = items,
                onDiveClicked = onDiveClicked,
                onRetry = onRetry,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ContentState(
    items: LazyPagingItems<DiveItem>,
    onDiveClicked: (Dive) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp),
    ) {
        // Sticky headers with paging 3 are currently a bit awkward:
        // https://issuetracker.google.com/issues/193785330
        for (index in 0 until items.itemCount) {
            when (val peekedItem = items.peek(index)) {
                is DiveItem.Header -> stickyHeader(key = peekedItem.id, contentType = "header") {
                    val header = items[index] as DiveItem.Header
                    DateHeader(localDate = header.localDate)
                }
                is DiveItem.Item -> item(key = peekedItem.id, contentType = "item") {
                    val item = items[index] as DiveItem.Item
                    DiveListItem(
                        dive = item.dive,
                        onClick = { onDiveClicked(item.dive) },
                    )
                }
                null -> Unit // Not needed as skeletons are not enabled
            }
        }
        // Trailing states
        when (val state = items.loadState.append) {
            is LoadState.NotLoading -> Unit // No trailing empty state, list just ends
            is LoadState.Loading -> item { TrailingLoadingState() }
            is LoadState.Error -> item { TrailingErrorState(error = state.error, onRetry = onRetry) }
            else -> error("Unsupported state: $state")
        }
    }
}

private class PreviewProvider : PreviewParameterProvider<PagingData<DiveItem>> {
    override val values: Sequence<PagingData<DiveItem>> = sequenceOf(
        // Initial loading
        PagingData.empty(),
        // Initial error
        PagingData.from(
            data = emptyList(),
            sourceLoadStates = LoadStates(
                refresh = LoadState.Error(RuntimeException("Lorem Ipsum")),
                append = LoadState.NotLoading(endOfPaginationReached = true),
                prepend = LoadState.NotLoading(endOfPaginationReached = true),
            ),
        ),
        // Initial empty
        PagingData.from(
            data = emptyList(),
            sourceLoadStates = LoadStates(
                refresh = LoadState.NotLoading(endOfPaginationReached = true),
                append = LoadState.NotLoading(endOfPaginationReached = true),
                prepend = LoadState.NotLoading(endOfPaginationReached = true),
            ),
        ),
        // Trailing loading
        PagingData.from(
            data = listOf(
                DiveItem.Header(LocalDate.now()),
                DiveItem.Item(Dive.sample),
            ),
            sourceLoadStates = LoadStates(
                refresh = LoadState.NotLoading(endOfPaginationReached = true),
                append = LoadState.Loading,
                prepend = LoadState.NotLoading(endOfPaginationReached = true),
            ),
        ),
        // Trailing error
        PagingData.from(
            data = listOf(
                DiveItem.Header(LocalDate.now()),
                DiveItem.Item(Dive.sample),
            ),
            sourceLoadStates = LoadStates(
                refresh = LoadState.NotLoading(endOfPaginationReached = true),
                append = LoadState.Error(RuntimeException("Lorem Ipsum")),
                prepend = LoadState.NotLoading(endOfPaginationReached = true),
            ),
        ),
        // Trailing empty (end of list)
        PagingData.from(
            data = Dive.samples.map(DiveItem::Item),
            sourceLoadStates = LoadStates(
                refresh = LoadState.NotLoading(endOfPaginationReached = true),
                append = LoadState.NotLoading(endOfPaginationReached = true),
                prepend = LoadState.NotLoading(endOfPaginationReached = true),
            ),
        ),
    )
}

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun Preview(
    @PreviewParameter(PreviewProvider::class) pagingData: PagingData<DiveItem>,
) {
    DiveTheme {
        DiveList(
            items = MutableStateFlow(pagingData).collectAsLazyPagingItems(),
            onDiveClicked = {},
            onRetry = {},
        )
    }
}

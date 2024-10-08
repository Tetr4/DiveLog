package cloud.mike.divelog.ui.home

import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.testing.asSnapshot
import cloud.mike.divelog.data.dives.Dive
import cloud.mike.divelog.data.dives.DiveRepository
import cloud.mike.divelog.ui.MainDispatcherRule
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertArrayEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.time.LocalDate

class HomeViewModelTest {

    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()

    @Test
    fun test_addDateHeaders() = runTest {
        // given
        val pagingData = PagingData.from(
            data = listOf(
                // <HEADER EXPECTED HERE>
                Dive.sample.copy(startDate = LocalDate.parse("2000-12-01")),
                Dive.sample.copy(startDate = LocalDate.parse("2000-12-01")),
                // <HEADER EXPECTED HERE>
                Dive.sample.copy(startDate = LocalDate.parse("2000-12-02")),
            ),
            sourceLoadStates = LoadStates(
                refresh = LoadState.NotLoading(endOfPaginationReached = false),
                append = LoadState.NotLoading(endOfPaginationReached = false),
                prepend = LoadState.NotLoading(endOfPaginationReached = false),
            ),
        )
        val diveRepo: DiveRepository = mock {
            on(mock.getDivesPages(any())) doReturn flowOf(pagingData)
        }
        val viewModel = HomeViewModel(
            diveRepo = diveRepo,
        )

        // when
        val items = viewModel.dives.asSnapshot { scrollTo(index = 2) }

        // then
        assertArrayEquals(
            arrayOf(
                DiveItem.Header::class.java,
                DiveItem.Item::class.java,
                DiveItem.Item::class.java,
                DiveItem.Header::class.java,
                DiveItem.Item::class.java,
            ),
            items
                .map { it.javaClass }
                .toTypedArray(),
        )
    }
}

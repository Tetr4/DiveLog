package cloud.mike.divelog.ui.home

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import cloud.mike.divelog.data.dives.Dive
import cloud.mike.divelog.data.dives.DiveRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.LocalDateTime

/** A list item, which is either a date header or dive data. */
@Immutable
sealed interface DiveItem {
    val id: String

    data class Header(val localDate: LocalDate, override val id: String = localDate.toString()) : DiveItem
    data class Item(val dive: Dive, override val id: String = dive.id.toString()) : DiveItem
}

@Immutable
data class HomeState(
    val query: String = "",
)

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val diveRepo: DiveRepository,
) : ViewModel() {

    private val query = MutableStateFlow("")

    val dives = query.flatMapLatest { query ->
        diveRepo.getDivePages(query)
            .map { it.addDateHeaders() }
            .cachedIn(viewModelScope)
    }

    val uiState = query.map(::HomeState)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeState())

    fun search(query: String) {
        this.query.value = query
    }
}

private fun PagingData<Dive>.addDateHeaders(): PagingData<DiveItem> = this
    .map(DiveItem::Item)
    .insertSeparators { top: DiveItem.Item?, bottom: DiveItem.Item? ->
        when {
            // no header at end of list
            bottom == null -> null
            // header at start of list or between items with different dates
            top == null || !top.dive.start.sameDayAs(bottom.dive.start)
            -> DiveItem.Header(bottom.dive.start.toLocalDate())
            // no headers between items with same dates
            else -> null
        }
    }

private fun LocalDateTime.sameDayAs(other: LocalDateTime) = toLocalDate() == other.toLocalDate()

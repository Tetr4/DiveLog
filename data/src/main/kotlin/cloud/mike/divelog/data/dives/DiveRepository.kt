package cloud.mike.divelog.data.dives

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.map
import cloud.mike.divelog.persistence.dives.DivesDao
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.util.UUID

private const val PAGE_SIZE = 50
private const val FAKE_PAGES = false

class DiveRepository(
    private val divesDao: DivesDao,
) {

    fun getDiveStream(id: UUID): Flow<Dive?> = divesDao.getDiveStream(id).map { it?.toEntity() }
    fun getDivesPages(query: String): Flow<PagingData<Dive>> = if (FAKE_PAGES) {
        val pager = Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { FakePagingSource() },
        )
        pager.flow
    } else {
        val pager = Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { divesDao.getDivesPages(query) },
        )
        pager.flow.map { diveDtos -> diveDtos.map { it.toEntity() } }
    }

    suspend fun addDive(dive: Dive) = addDives(listOf(dive))
    suspend fun addDives(dives: List<Dive>) = divesDao.upsertDives(dives.map(Dive::toDto))
    suspend fun updateDive(dive: Dive) = divesDao.upsertDive(dive.toDto())
    suspend fun deleteDive(dive: Dive) = divesDao.deleteDive(dive.toDto().dive)

    suspend fun getNextDiveNumber(): Int = (divesDao.getMaxDiveNumber() ?: 0) + 1
    suspend fun containsDiveAt(timestamp: LocalDateTime): Boolean = divesDao.diveExists(
        startDate = timestamp.toLocalDate(),
        startTime = timestamp.toLocalTime(),
    )
}

private class FakePagingSource : PagingSource<Int, Dive>() {

    override fun getRefreshKey(state: PagingState<Int, Dive>): Int? {
        val anchor = state.anchorPosition ?: return null
        return state.closestPageToPosition(anchor)?.prevKey?.plus(1)
            ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Dive> = try {
        delay(1_000)
        val page = params.key ?: 0
        val pageStart = (page - 0) * PAGE_SIZE
        val isLastPage = pageStart + PAGE_SIZE >= Dive.samples.lastIndex
        val pageEnd = if (isLastPage) {
            Dive.samples.lastIndex + 1
        } else {
            pageStart + PAGE_SIZE
        }
        if (Dive.samples.isEmpty()) {
            LoadResult.Page(
                data = emptyList(),
                prevKey = null,
                nextKey = null,
            )
        } else {
            LoadResult.Page(
                data = Dive.samples.subList(pageStart, pageEnd),
                prevKey = null,
                nextKey = if (isLastPage) null else page + 1,
            )
        }
    } catch (e: Exception) {
        LoadResult.Error(e)
    }
}

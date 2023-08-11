package cloud.mike.divelog.persistence.dives

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import cloud.mike.divelog.persistence.AppDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime
import java.util.UUID
import kotlin.time.Duration.Companion.minutes

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class DivesDaoTest {

    private lateinit var database: AppDatabase

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            context = ApplicationProvider.getApplicationContext(),
            klass = AppDatabase::class.java,
        ).build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `write and read dives`() = runTest {
        // given
        val dao = database.divesDao()
        val dive = DiveDto(
            id = UUID.randomUUID(),
            number = 1,
            locationId = null,
            start = LocalDateTime.now(),
            diveTime = 30.minutes,
            maxDepthMeters = null,
            minTemperatureCelsius = null,
            notes = null,
        )

        // when
        dao.insertDive(dive)
        val dives = dao.loadDivesStream().first()

        // then
        assertEquals(1, dives.size)
        assertEquals(dive, dives.first().dive)
    }
}

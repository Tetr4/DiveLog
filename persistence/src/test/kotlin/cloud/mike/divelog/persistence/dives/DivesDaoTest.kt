package cloud.mike.divelog.persistence.dives

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import cloud.mike.divelog.persistence.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID
import kotlin.time.Duration.Companion.minutes

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
    fun `write and read dive`() = runTest {
        // given
        val dao = database.divesDao()
        val locationId = UUID.randomUUID()
        val expected = DiveWithLocationAndProfile(
            dive = DiveDto(
                id = UUID.randomUUID(),
                number = 1,
                locationId = locationId,
                startDate = LocalDate.now(),
                startTime = LocalTime.now(),
                duration = 30.minutes,
                maxDepthMeters = null,
                minTemperatureCelsius = null,
                buddy = null,
                notes = null,
            ),
            location = DiveLocationDto(
                id = locationId,
                name = "Test",
                latitude = 24.262363202339,
                longitude = 35.51645954667073,
            ),
            profile = null,
        )

        // when
        dao.upsertDive(expected)
        val actual = dao.getDiveStream(expected.dive.id).first()

        // then
        assertEquals(expected, actual)
    }
}

package cloud.mike.divelog.ui.common

import org.junit.Assert.assertEquals
import org.junit.Test

class ListExtTest {

    @Test
    fun samplingIgnoresLargerData() {
        // given
        val list = List(42) { it }

        // when
        val result = list.sample(50)

        // then
        assertEquals(list, result)
    }

    @Test
    fun `sampling supports empty list`() {
        // given
        val list = emptyList<String>()

        // when
        val result = list.sample(10)

        // then
        assertEquals(0, result.size)
    }

    @Test
    fun `sampling supports single element`() {
        // given
        val list = listOf(1)

        // when
        val result = list.sample(1)

        // then
        assertEquals(1, result.size)
    }

    @Test
    fun `sampling reduces evenly splittable data`() {
        // given
        val list = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)

        // when
        val result = list.sample(4)

        // then
        assertEquals(listOf(0, 3, 6, 9), result)
    }

    @Test
    fun `sampling reduces not evenly splittable data`() {
        // given
        val list = listOf(0, 1, 2, 3, 4, 5, 6, 7)

        // when
        val result = list.sample(3)

        // then
        assertEquals(listOf(0, 3, 6), result)
    }
}
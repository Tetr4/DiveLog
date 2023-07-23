package cloud.mike.divelog.data.importer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

private const val TEST_BYTE: Byte = 0b0000_1101
private const val UINT24_MAX_VALUE = 16777215 // 2^24 - 1

class PayloadUtilsTest {

    @Test
    fun byte_flag_0() = assertTrue(TEST_BYTE.flag(0))

    @Test
    fun byte_flag_1() = assertFalse(TEST_BYTE.flag(1))

    @Test
    fun byte_flag_2() = assertTrue(TEST_BYTE.flag(2))

    @Test
    fun bytearray_uInt8() = assertEquals(UByte.MAX_VALUE.toInt(), byteArrayOf(-1).uInt8(0))

    @Test
    fun bytearray_uInt16() = assertEquals(UShort.MAX_VALUE.toInt(), byteArrayOf(-1, -1).uInt16(0))

    @Test
    fun bytearray_uInt24() = assertEquals(UINT24_MAX_VALUE, byteArrayOf(-1, -1, -1).uInt24(0))

    @Test
    fun bytearray_uInt16_index_1() = assertEquals(UShort.MAX_VALUE.toInt(), byteArrayOf(0, -1, -1).uInt16(1))

    @Test
    fun bytearray_uInt16_index_2() = assertEquals(UShort.MAX_VALUE.toInt(), byteArrayOf(0, 0, -1, -1).uInt16(2))

    @Test
    fun bytearray_uInt16_index_3() = assertEquals(UShort.MAX_VALUE.toInt(), byteArrayOf(0, 0, 0, -1, -1).uInt16(3))

    @Test
    fun bytearray_uInt24_index_2() = assertEquals(UINT24_MAX_VALUE, byteArrayOf(0, 0, -1, -1, -1).uInt24(2))

    @Test
    fun bytearray_uInt16_endianess() = assertEquals(6720, byteArrayOf(0x40, 0x1A).uInt16(0))
}

package cloud.mike.divelog.data.importer

import org.junit.Assert.assertEquals
import org.junit.Test

private const val UINT24_MAX_VALUE = 16777215 // 2^24 - 1

class PayloadUtilsTest {

    @Test
    fun bytearray_uInt8() = assertEquals(UByte.MAX_VALUE.toInt(), byteArrayOf(-1).uInt8(0))

    @Test
    fun bytearray_uInt16L() = assertEquals(UShort.MAX_VALUE.toInt(), byteArrayOf(-1, -1).uInt16L(0))

    @Test
    fun bytearray_uInt24L() = assertEquals(UINT24_MAX_VALUE, byteArrayOf(-1, -1, -1).uInt24L(0))

    @Test
    fun bytearray_uInt16L_index_1() = assertEquals(UShort.MAX_VALUE.toInt(), byteArrayOf(0, -1, -1).uInt16L(1))

    @Test
    fun bytearray_uInt16L_index_2() = assertEquals(UShort.MAX_VALUE.toInt(), byteArrayOf(0, 0, -1, -1).uInt16L(2))

    @Test
    fun bytearray_uInt16L_index_3() = assertEquals(UShort.MAX_VALUE.toInt(), byteArrayOf(0, 0, 0, -1, -1).uInt16L(3))

    @Test
    fun bytearray_uInt24L_index_2() = assertEquals(UINT24_MAX_VALUE, byteArrayOf(0, 0, -1, -1, -1).uInt24L(2))

    @Test
    fun bytearray_uInt16L_endianess() = assertEquals(6720, byteArrayOf(0x40, 0x1A).uInt16L(0))

    @Test
    fun bytearray_uInt16B() = assertEquals(UShort.MAX_VALUE.toInt(), byteArrayOf(-1, -1).uInt16B(0))

    @Test
    fun bytearray_uInt24B() = assertEquals(UINT24_MAX_VALUE, byteArrayOf(-1, -1, -1).uInt24B(0))

    @Test
    fun bytearray_uInt32B() = assertEquals(UInt.MAX_VALUE.toLong(), byteArrayOf(-1, -1, -1, -1).uInt32B(0))

    @Test
    fun bytearray_uInt16B_index_1() = assertEquals(UShort.MAX_VALUE.toInt(), byteArrayOf(0, -1, -1).uInt16B(1))

    @Test
    fun bytearray_uInt16B_index_2() = assertEquals(UShort.MAX_VALUE.toInt(), byteArrayOf(0, 0, -1, -1).uInt16B(2))

    @Test
    fun bytearray_uInt16B_index_3() = assertEquals(UShort.MAX_VALUE.toInt(), byteArrayOf(0, 0, 0, -1, -1).uInt16B(3))

    @Test
    fun bytearray_uInt24B_index_2() = assertEquals(UINT24_MAX_VALUE, byteArrayOf(0, 0, -1, -1, -1).uInt24B(2))

    @Test
    fun bytearray_uInt32B_index_2() = assertEquals(
        UInt.MAX_VALUE.toLong(),
        byteArrayOf(0, 0, -1, -1, -1, -1).uInt32B(2),
    )
}

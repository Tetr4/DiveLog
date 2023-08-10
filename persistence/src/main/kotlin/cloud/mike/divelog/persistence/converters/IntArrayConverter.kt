package cloud.mike.divelog.persistence.converters

import androidx.room.TypeConverter
import java.nio.ByteBuffer

class IntArrayConverter {
    @TypeConverter
    fun encode(value: IntArray): ByteArray {
        val buffer = ByteBuffer.allocate(value.size * Int.SIZE_BYTES)
        buffer.asIntBuffer().put(value)
        return buffer.array()
    }

    @TypeConverter
    fun decode(value: ByteArray): IntArray {
        val buffer = ByteBuffer.wrap(value).asIntBuffer()
        val array = IntArray(buffer.limit())
        buffer.get(array)
        return array
    }
}

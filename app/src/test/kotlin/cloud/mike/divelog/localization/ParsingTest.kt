package cloud.mike.divelog.localization

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Locale

class ParsingTest {

    @Test
    fun `isValidNumber returns true for valid number`() {
        assertTrue("123.45".isValidNumber(Locale.US))
    }

    @Test
    fun `isValidNumber returns false for invalid number`() {
        assertFalse("abc".isValidNumber(Locale.US))
    }

    @Test
    fun `isValidInteger returns true for valid integer`() {
        assertTrue("123".isValidInteger(Locale.US))
    }

    @Test
    fun `isValidInteger returns false for invalid integer`() {
        assertFalse("123.45".isValidInteger(Locale.US))
    }

    @Test
    fun `isValidInteger returns true integer with thounsands separator`() {
        assertTrue("123,000".isValidInteger(Locale.US))
    }

    @Test
    fun `isValidInteger supports other locales`() {
        assertTrue("123.000".isValidInteger(Locale.GERMANY))
    }

    @Test
    fun `toNumberOrNull returns number for valid input`() {
        assertEquals(123.45, "123.45".toNumberOrNull(Locale.US))
    }

    @Test
    fun `toNumberOrNull returns null for invalid input`() {
        assertNull("abc".toNumberOrNull(Locale.US))
    }
}

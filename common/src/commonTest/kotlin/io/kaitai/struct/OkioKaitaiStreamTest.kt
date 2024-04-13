package io.kaitai.struct

import okio.IOException
import kotlin.test.*

class OkioKaitaiStreamTest {
    lateinit var stream: KaitaiStream

    val eofException = IOException::class
    val testData = "12345".map { it.code.toByte() }.toByteArray()

    @BeforeTest
    fun setup() {
        stream = OkioKaitaiStream(testData)
    }

    @AfterTest
    fun teardown() {
        stream.close()
    }

    @Test
    fun testReadS1() {
        val first = stream.readS1().toShort()
        assertEquals(expected = 0x31, actual = first)

        val second = stream.readS1().toShort()
        assertEquals(expected = 0x32, actual = second)
    }

    @Test
    fun testReadS2be() {
        val first = stream.readS2be()
        assertEquals(expected = 0x3132, actual = first)
        val second = stream.readS2be()
        assertEquals(expected = 0x3334, actual = second)

        assertFailsWith(eofException) {
            stream.readS2be()
        }
    }

    @Test
    fun testReadBytes5() {
        val actual = stream.readBytes(5)
        assertContentEquals(expected = testData, actual = actual)
    }

    @Test
    fun testReadBytes6() {
        assertFails {
            stream.readBytes(6)
        }
    }
}

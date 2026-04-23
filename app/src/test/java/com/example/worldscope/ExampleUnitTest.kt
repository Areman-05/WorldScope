package com.example.worldscope

import com.example.worldscope.domain.model.WikiSummary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun wikiSummary_acepta_extract_nulo() {
        val summary = WikiSummary(title = "Test", extract = null, thumbnailUrl = null)
        assertEquals("Test", summary.title)
        assertNull(summary.extract)
    }

    @Test
    fun conversion_base_y_destino_se_mantiene() {
        val base = "EUR"
        val target = "USD"
        assertTrue(base.length == 3)
        assertTrue(target.length == 3)
    }
}
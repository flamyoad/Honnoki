package com.flamyoad.honnoki

import com.flamyoad.honnoki.parser.EMPTY_TAG
import com.flamyoad.honnoki.parser.ancestorNonNull
import com.flamyoad.honnoki.parser.parentNonNull
import org.jsoup.nodes.Element
import org.junit.Assert
import org.junit.Test

const val HTML_TEXT = """
    <tr>
        <td class="table-label"><i class="info-author"></i>Author(s) :</td>
        <td class="table-value">
            <h2>Flower Lotus Living in the Palace</h2>
        </td>
    </tr>
"""

val emptyElement = Element(EMPTY_TAG)

class JsoupExtensionTest {
    @Test
    fun `Test for Jsoup Element equality`() {
        val a = Element("<p>")
        val b = Element("<p>")
        Assert.assertEquals(a, b)
    }

    @Test
    fun `parentNonNull() should not return a null if parent does not exist`() {
        val element = Element("<p></p>")
        Assert.assertNotNull(element.parentNonNull())
    }

    @Test
    fun `parentNonNull() should return pre-determined empty element if parent does not exist`() {
        val element = Element("<p></p>")
        Assert.assertEquals(element.parentNonNull(), emptyElement)
    }

    @Test
    fun `parentNonNull() once should produce same result as ancestorNonNull(1)`() {
        val element = Element(HTML_TEXT)
        Assert.assertEquals(element.parentNonNull(), element.ancestorNonNull(1))
    }
}
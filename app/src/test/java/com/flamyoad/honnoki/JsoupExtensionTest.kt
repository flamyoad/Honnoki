package com.flamyoad.honnoki

import com.flamyoad.honnoki.parser.EMPTY_TAG
import com.flamyoad.honnoki.parser.ancestorNonNull
import com.flamyoad.honnoki.parser.parentNonNull
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.junit.Assert
import org.junit.Ignore
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
        Assert.assertTrue(a.tag() == b.tag())
    }

    @Test
    fun `parentNonNull() should not return a null if parent does not exist`() {
        val html = "<div id=\"element1\" class=\"container\">Hello, Jsoup!</div>"
        val document = Jsoup.parse(html)
        val element = document.getElementById("element1")
        Assert.assertNotNull(element.parentNonNull())
    }

    @Test
    @Ignore
    fun `parentNonNull() should return pre-determined empty element if parent does not exist`() {
        val html = "<body id=\"element1\" </body>"
        val document = Jsoup.parse(html)
        val element = document.getElementById("element1")
        Assert.assertEquals(element.parentNonNull(), emptyElement)
    }

    @Test
    fun `parentNonNull() once should produce same result as ancestorNonNull(1)`() {
        val html = "<div id=\"element1\" class=\"container\">Hello, Jsoup!</div>"
        val document = Jsoup.parse(html)
        val element = document.getElementById("element1")
        Assert.assertEquals(element.parentNonNull(), element.ancestorNonNull(1))
    }
}
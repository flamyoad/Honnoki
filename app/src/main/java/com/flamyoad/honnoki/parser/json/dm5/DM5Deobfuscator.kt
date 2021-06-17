package com.flamyoad.honnoki.parser.json.dm5

import android.app.job.JobServiceEngine
import android.util.Log
import app.cash.quickjs.QuickJs
import app.cash.quickjs.QuickJsException

class DM5Deobfuscator() {

    /* Modifies the function to become a one-line code which can be defined and called in one step
        This is because the original code needs to be called in two steps:
             i. Define the function
             ii. Call the function

        ..which we cannot do here. It needs to be one line only. Hence we wrap the function with
        braces. The "eval" is not needed here and is removed

        Example of a one-liner:

        (function fac (n) {
               return (n === 0 ? 1 : n*fac(n-1));
            })(10)

         It will return 3628800 (You can try it in Chrome console). Notice how the function
         must first be wrapped in braces.
         This is a standard feature to define and call the function in one line(see ECMA-262, ed. 5.1, p. 98).
    */
    fun getChapterImagesFromJs(jsPacked: String): List<String> {
        if (jsPacked.isEmpty()) {
            return emptyList()
        }

        val jsEngine = QuickJs.create()

        try {
            val modifiedJs = jsPacked.replace("eval", "(")
                .replace("}(", "})(")

            // Content of this variable: var newImgs=['1.jpg', '2.jpg', '3.jpg']
            val arr = jsEngine.evaluate(modifiedJs).toString()

            val imageLinks = arr
                .substring(arr.indexOf('[') + 1, arr.indexOf(']')) // Extract items in array
                .split(',') // Split the array
                .map { it.replace("'", "") } // Remove the unnecessary single quote in string

            return imageLinks

        } catch (ex: QuickJsException) {
            ex.printStackTrace()
            return emptyList()
        } finally {
            jsEngine.close()
        }
    }
}
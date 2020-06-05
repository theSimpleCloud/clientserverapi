/*
 * MIT License
 *
 * Copyright (C) 2020 Frederick Baier
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.clientserverapi

import eu.thesimplecloud.clientserverapi.lib.json.GsonCreator
import eu.thesimplecloud.clientserverapi.lib.json.GsonExclude
import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import org.junit.Assert
import org.junit.Test

class JsonDataTest {

    @Test
    fun test(){
        Assert.assertNull(JsonData.empty().getObjectOrNull(TestEnum::class.java))
        val testDataClass = TestDataClass("test", "test2")
        Assert.assertEquals("test2", JsonData.fromObject(testDataClass, GsonCreator().create()).getString("excludedString"))
        Assert.assertNull(JsonData.fromObject(testDataClass, GsonCreator().excludeAnnotations(GsonExclude::class.java).create()).getString("excludedString"))
    }

    @Test
    fun test2() {
        val innerJsonData = JsonData.empty().append("test", 3)
        val jsonData = JsonData.empty().append("data", innerJsonData)
        val innerJsonData2 = jsonData.getObject("data", JsonData::class.java)
        Assert.assertEquals(innerJsonData.toString(), innerJsonData2.toString())
        Assert.assertEquals(3, innerJsonData2!!.getInt("test"))
    }

    @Test
    fun json_array_test() {
        JsonData.fromObject(listOf("1", "2", "3", "4"))
    }


    enum class TestEnum() {
        TEST_1, TEST_2
    }

    class TestDataClass(val string: String, @GsonExclude val excludedString: String)

}
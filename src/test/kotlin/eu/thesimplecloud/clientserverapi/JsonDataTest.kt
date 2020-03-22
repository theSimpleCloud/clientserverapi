package eu.thesimplecloud.clientserverapi

import eu.thesimplecloud.clientserverapi.lib.json.GsonExclude
import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import org.junit.Assert
import org.junit.Test

class JsonDataTest {

    @Test
    fun test(){
        Assert.assertNull(JsonData().getObjectOrNull(TestEnum::class.java))
        val testDataClass = TestDataClass("test", "test2")
        Assert.assertEquals("test2", JsonData.fromObject(testDataClass).getString("excludedString"))
        Assert.assertNull(JsonData.fromObjectWithGsonExclude(testDataClass).getString("excludedString"))
    }

    @Test
    fun test2() {
        val innerJsonData = JsonData().append("test", 3)
        val jsonData = JsonData().append("data", innerJsonData)
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
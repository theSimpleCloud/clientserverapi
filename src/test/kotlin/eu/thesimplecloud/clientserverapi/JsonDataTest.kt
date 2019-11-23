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


    enum class TestEnum() {
        TEST_1, TEST_2
    }

    class TestDataClass(val string: String, @GsonExclude val excludedString: String)

}
package eu.thesimplecloud.clientserverapi

import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import org.junit.Assert
import org.junit.Test

class JsonDataTest {

    @Test
    fun test(){
        Assert.assertNull(JsonData().getObjectOrNull(TestEnum::class.java))
    }


    enum class TestEnum() {
        TEST_1, TEST_2
    }

}
package eu.thesimplecloud.clientserverapi.communicationpromise

import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import org.junit.Test

class PromiseTimeoutTest {

    @Test
    fun test() {
        val promise = CommunicationPromise<Int>()
        Thread.sleep(400)
        promise.cause().printStackTrace()
    }

}
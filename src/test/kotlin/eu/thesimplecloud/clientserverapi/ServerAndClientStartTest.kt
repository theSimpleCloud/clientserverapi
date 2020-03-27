package eu.thesimplecloud.clientserverapi

import eu.thesimplecloud.clientserverapi.client.NettyClient
import eu.thesimplecloud.clientserverapi.server.NettyServer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import eu.thesimplecloud.clientserverapi.communication.testclasses.TestConnectedClientValue
import kotlinx.coroutines.delay
import org.junit.Test

class ServerAndClientStartTest {

    /*

    @Test
    fun test() {
        GlobalScope.launch {
            delay(15000)
            val nettyServer = NettyServer<TestConnectedClientValue>("127.0.0.1", 1921)
            GlobalScope.launch {
                nettyServer.start().addListener { println(it.isSuccess) }
            }
        }

        val nettyClient = NettyClient("127.0.0.1", 1921)
        while(!nettyClient.start().awaitUninterruptibly().isSuccess) {
            println("Failed to connect to server. Retrying in 5 seconds.")
            Thread.sleep(5000)
            println("Retrying")
        }
        println("Connected")
    }

    */

}
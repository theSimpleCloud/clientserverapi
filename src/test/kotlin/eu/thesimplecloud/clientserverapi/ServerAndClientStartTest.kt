package eu.thesimplecloud.clientserverapi

import eu.thesimplecloud.clientserverapi.client.NettyClient
import eu.thesimplecloud.clientserverapi.server.NettyServer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import eu.thesimplecloud.clientserverapi.communication.testclasses.TestConnectedClientValue
import org.junit.Test

class ServerAndClientStartTest {


    @Test(timeout = 3000)
    fun test() {
        val nettyServer = NettyServer<TestConnectedClientValue>("127.0.0.1", 1921)
        nettyServer.registerPacketsByPackage("me.wetterbericht.clientserverapi.communication.packet")
        GlobalScope.launch {
            nettyServer.start()
        }
        while (!nettyServer.isListening()) {
            Thread.sleep(10)
        }
        val nettyClient = NettyClient("127.0.0.1", 1921)
        nettyClient.addPacketsPackage("me.wetterbericht.clientserverapi.communication.packet")
        nettyClient.start()
        while (!nettyClient.isOpen()) {
            Thread.sleep(10)
        }
    }

}
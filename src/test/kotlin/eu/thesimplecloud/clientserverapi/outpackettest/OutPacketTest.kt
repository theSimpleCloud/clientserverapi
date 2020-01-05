package eu.thesimplecloud.clientserverapi.outpackettest

import eu.thesimplecloud.clientserverapi.client.NettyClient
import eu.thesimplecloud.clientserverapi.communication.testclasses.TestConnectedClientValue
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.sendQuery
import eu.thesimplecloud.clientserverapi.outpackettest.out.PacketOutSomething
import eu.thesimplecloud.clientserverapi.server.NettyServer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.Test

class OutPacketTest {

    /*

    @Test
    fun test() {
        val nettyServer = NettyServer<TestConnectedClientValue>("127.0.0.1", 1921)
        nettyServer.addPacketsByPackage("eu.thesimplecloud.clientserverapi.outpackettest.in")
        GlobalScope.launch {
            nettyServer.start()
        }
        while (!nettyServer.isListening()) {
            Thread.sleep(10)
        }
        val nettyClient = NettyClient("127.0.0.1", 1921)
        nettyClient.addPacketsByPackage("eu.thesimplecloud.clientserverapi.outpackettest.out")
        GlobalScope.launch {
            nettyClient.start()
        }
        nettyClient.sendUnitQuery(PacketOutSomething()).awaitUninterruptibly().addResultListener { println(it) }
        Thread.sleep(200)
    }
    */

}
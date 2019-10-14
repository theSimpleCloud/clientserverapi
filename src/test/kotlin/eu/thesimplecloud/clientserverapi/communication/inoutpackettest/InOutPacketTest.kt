package eu.thesimplecloud.clientserverapi.communication.inoutpackettest

import eu.thesimplecloud.clientserverapi.client.NettyClient
import eu.thesimplecloud.clientserverapi.communication.inoutpackettest.outpackets.PacketOutTest
import eu.thesimplecloud.clientserverapi.communication.testclasses.TestConnectedClientValue
import eu.thesimplecloud.clientserverapi.server.NettyServer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.Test

class InOutPacketTest {

    /*
    @Test(timeout = 2000)
    fun test(){
        val nettyServer = NettyServer<TestConnectedClientValue>("127.0.0.1", 1921)
        nettyServer.addPacketsByPackage("eu.thesimplecloud.clientserverapi.communication.inoutpackettest.in")
        GlobalScope.launch {
            nettyServer.start()
        }
        while (!nettyServer.isListening()) {
            Thread.sleep(10)
        }
        val nettyClient = NettyClient("127.0.0.1", 1921)
        nettyClient.addPacketsByPackage("eu.thesimplecloud.clientserverapi.communication.inoutpackettest.outpackets")
        nettyClient.start()
        while (!nettyClient.isOpen()) {
            Thread.sleep(10)
        }
        nettyClient.sendQuery(PacketOutTest()).syncUninterruptibly()
        println("test1")
    }
    */


}
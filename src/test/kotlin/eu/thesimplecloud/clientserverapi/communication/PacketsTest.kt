package eu.thesimplecloud.clientserverapi.communication

import eu.thesimplecloud.clientserverapi.client.NettyClient
import eu.thesimplecloud.clientserverapi.lib.packet.packetpromise.IPacketPromise
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.server.NettyServer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import eu.thesimplecloud.clientserverapi.communication.testclasses.TestConnectedClientValue
import eu.thesimplecloud.clientserverapi.communication.testclasses.TestJsonPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packetresponse.responsehandler.ObjectPacketResponseHandler
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kotlin.concurrent.thread


class PacketsTest {


    var nettyServer = NettyServer<TestConnectedClientValue>("127.0.0.1", 1917)
    var nettyClient = NettyClient("127.0.0.1", 1917)



    @Test(timeout = 6000)
    fun test() {
        println(0)
        nettyServer.registerPacketsByPackage("eu.thesimplecloud.clientserverapi.communication.testclasses")
        println(1)
        thread {
            nettyServer.start()
        }
        println(2)
        while (!nettyServer.isListening()) {
            Thread.sleep(10)
        }
        println(3)
        nettyClient.addPacketsPackage("eu.thesimplecloud.clientserverapi.communication.testclasses")
        thread {
            nettyClient.start()
        }
        println(4)
        while (!nettyClient.isOpen()) {
            Thread.sleep(10)
        }
        println(5)

        val packetPromise: IPacketPromise<String> = nettyClient.sendQuery(TestJsonPacket("test")) { packet ->
            packet as JsonPacket
            packet.jsonData.getString("message")
        }
        packetPromise.addPacketPromiseListener { println(it.get()) }
        packetPromise.sync()
        Assert.assertEquals(packetPromise.get(), "test")
    }

    @After
    fun after() {
        nettyServer.shutdown()
        nettyClient.shutdown()
    }

}
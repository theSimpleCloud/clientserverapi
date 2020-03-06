package eu.thesimplecloud.clientserverapi.testobject

import eu.thesimplecloud.clientserverapi.client.NettyClient
import eu.thesimplecloud.clientserverapi.communication.testclasses.TestConnectedClientValue
import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.sendQuery
import eu.thesimplecloud.clientserverapi.lib.packetresponse.responsehandler.ObjectPacketResponseHandler
import eu.thesimplecloud.clientserverapi.server.NettyServer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.Test

class ObjectPacketTest {

    /*
    @Test
    fun test() {
        val nettyServer = NettyServer<TestConnectedClientValue>("127.0.0.1", 1921)
        nettyServer.addPacketsByPackage("eu.thesimplecloud.clientserverapi.testobject")
        GlobalScope.launch {
            nettyServer.start()
        }
        while (!nettyServer.isListening()) {
            Thread.sleep(10)
        }
        val nettyClient = NettyClient("127.0.0.1", 1921)
        nettyClient.addPacketsByPackage("eu.thesimplecloud.clientserverapi.testobject")
        GlobalScope.launch {
            nettyClient.start()
        }
        nettyClient.sendQuery<Int>(PacketIOMessage("hi"), 250).then { println("2w-----${it}") }
                .addFailureListener { println("failure ${it::class.java.simpleName} ${it.message}") }
        //nettyClient.sendQuery<JsonData>(PacketIOMessage("hi"))
        //        .thenNonNull { println(it.getString("test")) }
        //        .addFailureListener { println(it.message) }
        GlobalScope.launch {
            nettyClient.sendUnitQuery(PacketIOMessage("hi")).awaitUninterruptibly().addResultListener { println("test: " + it.toString()) }
        }
        //nettyClient.sendUnitQuery(PacketIOMessage("hi")).awaitUninterruptibly().addResultListener { println("test: " + it.toString()) }
        //7nettyClient.sendUnitQuery(PacketIOMessage("hi")).awaitUninterruptibly().addResultListener { println("test: " + it.toString()) }
        //nettyClient.sendUnitQuery(PacketIOMessage("hi")).awaitUninterruptibly().addResultListener { println("test: " + it.toString()) }
        Thread.sleep(650)
    }
    */

}
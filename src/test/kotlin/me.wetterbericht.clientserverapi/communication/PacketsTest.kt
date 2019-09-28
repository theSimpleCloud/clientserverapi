package me.wetterbericht.clientserverapi.communication

import eu.thesimplecloud.clientserverapi.client.NettyClient
import eu.thesimplecloud.clientserverapi.lib.packet.packetpromise.IPacketPromise
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.server.NettyServer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.wetterbericht.clientserverapi.communication.packet.TestJsonPacket
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test



class PacketsTest {


    var nettyServer = NettyServer<TestConnectedClientValue>("127.0.0.1", 1900)
    var nettyClient = NettyClient("127.0.0.1", 1900)

    @Before
    fun startServerAndClient() {
        nettyServer.registerPacketsByPackage("me.wetterbericht.clientserverapi.communication.packet")
        GlobalScope.launch {
            nettyServer.start()
        }
        while (!nettyServer.isActive()) {
            Thread.sleep(10)
        }
        println("client")
        nettyClient.addPacketsPackage("me.wetterbericht.clientserverapi.communication.packet")
        GlobalScope.launch {
            nettyClient.start()
        }
        while (!nettyClient.isOpen()) {
            Thread.sleep(10)
        }
    }

    @Test(timeout = 3000)
    fun test() {
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
        nettyClient.disconnect()
    }

}
package me.wetterbericht.clientserverapi

import eu.thesimplecloud.clientserverapi.client.NettyClient
import eu.thesimplecloud.clientserverapi.server.NettyServer
import eu.thesimplecloud.clientserverapi.server.client.clientmanager.ClientManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.wetterbericht.clientserverapi.communication.TestConnectedClientValue
import org.junit.After
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import javax.xml.bind.JAXBElement

class ServerAndClientStartTest {


    @Test(timeout = 3000)
    fun test() {
        val nettyServer = NettyServer<TestConnectedClientValue>("127.0.0.1", 1900)
        nettyServer.registerPacketsByPackage("me.wetterbericht.clientserverapi.communication.packet")
        GlobalScope.launch {
            nettyServer.start()
        }
        while (!nettyServer.isActive()) {
            Thread.sleep(10)
        }
        val nettyClient = NettyClient("127.0.0.1", 1900)
        nettyClient.addPacketsPackage("me.wetterbericht.clientserverapi.communication.packet")
        nettyClient.start()
        while (!nettyClient.isOpen()) {
            Thread.sleep(10)
        }
    }

}
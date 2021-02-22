/*
 * MIT License
 *
 * Copyright (C) 2020 Frederick Baier
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.clientserverapi.testing

import eu.thesimplecloud.clientserverapi.lib.factory.CommunicationBootstrapFactoryGetter
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.sendQuery
import eu.thesimplecloud.clientserverapi.lib.util.Address
import eu.thesimplecloud.clientserverapi.testobject.ITestObj
import eu.thesimplecloud.clientserverapi.testobject.client.PacketIOWork
import eu.thesimplecloud.clientserverapi.testobject.client.PacketOutMessage
import org.junit.Test
import kotlin.concurrent.thread

/**
 * Created by IntelliJ IDEA.
 * Date: 25.09.2020
 * Time: 18:36
 * @author Frederick Baier
 */
class ObjectPacketTestTest {


    @Test
    fun test() {
        CommunicationBootstrapFactoryGetter.setEnvironment(CommunicationBootstrapFactoryGetter.ApplicationEnvironment.TEST)
        val factory = CommunicationBootstrapFactoryGetter.getFactory()
        val nettyServer = factory.createServer(Address("127.0.0.1", 1921))


        nettyServer.getPacketManager().registerPacket(PacketIOWork::class.java)
        nettyServer.addPacketsByPackage("eu.thesimplecloud.clientserverapi.testobject.server")
        thread {
            nettyServer.start()
        }
        while (!nettyServer.isListening()) {
            Thread.sleep(10)
        }
        val nettyClient = factory.createClient(Address("127.0.0.1", 1921))
        nettyClient.addPacketsByPackage("eu.thesimplecloud.clientserverapi.testobject.client")
        nettyClient.start().syncUninterruptibly()



        val clientConnection = nettyClient.getConnection()
        clientConnection.sendUnitQuery(PacketOutMessage("hi"), 1500).syncUninterruptibly()
        val packetIOWork = PacketIOWork()
        clientConnection.sendQuery<ITestObj>(packetIOWork, 1500).syncUninterruptibly()
        ConnectionAssert.assertSentPacketReceived(PacketOutMessage::class.java, clientConnection)
        ConnectionAssert.assertSentPacketReceived(packetIOWork, clientConnection)
        nettyClient.shutdown()
    }

}
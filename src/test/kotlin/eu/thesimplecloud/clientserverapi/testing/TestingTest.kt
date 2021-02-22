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

import eu.thesimplecloud.clientserverapi.client.INettyClient
import eu.thesimplecloud.clientserverapi.cluster.packet.PacketIOTest
import eu.thesimplecloud.clientserverapi.lib.factory.CommunicationBootstrapFactoryGetter
import eu.thesimplecloud.clientserverapi.lib.util.Address
import eu.thesimplecloud.clientserverapi.server.INettyServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * Created by IntelliJ IDEA.
 * Date: 02/02/2021
 * Time: 12:56
 * @author Frederick Baier
 */
class TestingTest {

    private lateinit var client: INettyClient
    private lateinit var server: INettyServer

    @Before
    fun before() {
        CommunicationBootstrapFactoryGetter.setEnvironment(CommunicationBootstrapFactoryGetter.ApplicationEnvironment.TEST)

        val factory = CommunicationBootstrapFactoryGetter.getFactory()
        val address = Address("127.0.0.1", 4000)
        server = factory.createServer(address)
        server.addPacketsByPackage("eu.thesimplecloud.clientserverapi.testing.packets")
        server.start().syncUninterruptibly()

        client = factory.createClient(address)
        client.addPacketsByPackage("eu.thesimplecloud.clientserverapi.testing.packets")
        client.start().syncUninterruptibly()
    }

    @After
    fun after() {
        client.shutdown().syncUninterruptibly()
        server.shutdown().syncUninterruptibly()
    }

    @Test
    fun afterConnect_ConnectionIsOpen() {
        Assert.assertTrue(client.getConnection().isOpen())
        Assert.assertTrue(server.getClientManager().getClients().first().isOpen())
        client.shutdown()
        server.shutdown()
    }

    @Test
    fun afterShutdown_ConnectionIsNotOpen() {
        client.shutdown().syncUninterruptibly()
        server.shutdown().syncUninterruptibly()

        Assert.assertFalse(client.getConnection().isOpen())
        Assert.assertTrue(server.getClientManager().getClients().isEmpty())
    }

    @Test
    fun whenNoPacketWasSent_ThrowError() {
        Assert.assertThrows(AssertionError::class.java) {
            ConnectionAssert.assertSentPacketReceived(PacketIOTest::class.java, client.getConnection())
        }
    }

    @Test
    fun whenPacketWasSent_ThrowNoError() {
        client.getConnection().sendUnitQuery(PacketIOTest("test")).syncUninterruptibly()
        ConnectionAssert.assertSentPacketReceived(PacketIOTest::class.java, client.getConnection())
    }



}
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

package eu.thesimplecloud.clientserverapi.clientserverlist

import eu.thesimplecloud.clientserverapi.client.INettyClient
import eu.thesimplecloud.clientserverapi.lib.factory.CommunicationBootstrapFactoryGetter
import eu.thesimplecloud.clientserverapi.lib.util.Address
import eu.thesimplecloud.clientserverapi.server.INettyServer
import org.junit.*

/**
 * Created by IntelliJ IDEA.
 * Date: 05/02/2021
 * Time: 12:56
 * @author Frederick Baier
 */
class ClientServerListTest {

    companion object {
        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            CommunicationBootstrapFactoryGetter.setEnvironment(CommunicationBootstrapFactoryGetter.ApplicationEnvironment.TEST)
        }
    }

    private lateinit var server: INettyServer
    private lateinit var clientOne: INettyClient
    private lateinit var clientTwo: INettyClient

    @Before
    fun setUp() {
        val factory = CommunicationBootstrapFactoryGetter.getFactory()
        server = factory.createServer(Address("127.0.0.1", 1630))
        clientOne = factory.createClient(Address("127.0.0.1", 1630))
        clientTwo = factory.createClient(Address("127.0.0.1", 1630))

        server.start().syncUninterruptibly()
        clientOne.start().syncUninterruptibly()
        clientTwo.start().syncUninterruptibly()
    }

    @After
    fun after() {
        server.shutdown().syncUninterruptibly()
        clientOne.shutdown().syncUninterruptibly()
        clientTwo.shutdown().syncUninterruptibly()
    }

    @Test
    fun createdListOnServer_IsEmpty() {
        val syncList =
            server.getClientServerSyncListManager().getSyncListByNameOrCreate<TestIdentifiable>("test")
        Assert.assertTrue(syncList.getAllElements().isEmpty())
    }

    @Test
    fun createdListOnClient_IsEmpty() {
        val syncList =
            clientOne.getClientServerSyncListManager().getSyncListByNameOrCreate<TestIdentifiable>("test")
        Assert.assertTrue(syncList.getAllElements().isEmpty())
    }

    @Test
    fun afterElementAdd_ListSizeIsOne() {
        val serverSyncList =
            clientOne.getClientServerSyncListManager().getSyncListByNameOrCreate<TestIdentifiable>("test")

        serverSyncList.addElement(TestIdentifiable("rrr", 543))

        Assert.assertEquals(1, serverSyncList.getAllElements().size)
    }

    @Test
    fun serverAddedElement_ClientsCanSee() {
        val serverSyncList =
            server.getClientServerSyncListManager().getSyncListByNameOrCreate<TestIdentifiable>("test")

        serverSyncList.addElement(TestIdentifiable("test1", 23)).syncUninterruptibly()

        val syncListClientOne = clientOne.getClientServerSyncListManager().getSyncListByName<TestIdentifiable>("test")!!
        val firstClientOne = syncListClientOne.getAllElements().first()

        Assert.assertEquals("test1", firstClientOne.getIdentifier())

        val syncListClientTwo = clientTwo.getClientServerSyncListManager().getSyncListByName<TestIdentifiable>("test")!!
        val firstClientTwo = syncListClientTwo.getAllElements().first()

        Assert.assertEquals("test1", firstClientTwo.getIdentifier())
    }

    @Test
    fun clientAddElement_OtherClientAnsServerCanSee() {
        val clientOneSynList =
            clientOne.getClientServerSyncListManager().getSyncListByNameOrCreate<TestIdentifiable>("test")

        clientOneSynList.addElement(TestIdentifiable("test12", 23)).syncUninterruptibly()

        val serverSyncList = server.getClientServerSyncListManager().getSyncListByName<TestIdentifiable>("test")!!
        val firstServer = serverSyncList.getAllElements().first()

        Assert.assertEquals("test12", firstServer.getIdentifier())

        val syncListClientTwo = clientTwo.getClientServerSyncListManager().getSyncListByName<TestIdentifiable>("test")!!
        val firstClientTwo = syncListClientTwo.getAllElements().first()

        Assert.assertEquals("test12", firstClientTwo.getIdentifier())
    }

    @Test
    fun clientUpdateElement_OtherClientAnsServerCanSee() {
        val clientOneSynList =
            clientOne.getClientServerSyncListManager().getSyncListByNameOrCreate<TestIdentifiable>("test")

        clientOneSynList.addElement(TestIdentifiable("test12", 23)).syncUninterruptibly()

        val elementToUpdate = clientOneSynList.getAllElements().first()
        elementToUpdate.number = 4653
        clientOneSynList.updateElement(elementToUpdate).syncUninterruptibly()

        val serverSyncList = server.getClientServerSyncListManager().getSyncListByName<TestIdentifiable>("test")!!
        val firstServer = serverSyncList.getAllElements().first()

        Assert.assertEquals(4653, firstServer.number)

        val syncListClientTwo = clientTwo.getClientServerSyncListManager().getSyncListByName<TestIdentifiable>("test")!!
        val firstClientTwo = syncListClientTwo.getAllElements().first()

        Assert.assertEquals(4653, firstClientTwo.number)
    }

    @Test
    fun clientRemoveElement_ElementIsGone() {
        val clientOneSynList =
            clientOne.getClientServerSyncListManager().getSyncListByNameOrCreate<TestIdentifiable>("test")

        clientOneSynList.addElement(TestIdentifiable("test12", 23)).syncUninterruptibly()
        clientOneSynList.removeElement("test12").syncUninterruptibly()


        val serverSyncList = server.getClientServerSyncListManager().getSyncListByName<TestIdentifiable>("test")!!
        val firstServer = serverSyncList.getAllElements().firstOrNull()

        Assert.assertEquals(null, firstServer)

        val syncListClientTwo = clientTwo.getClientServerSyncListManager().getSyncListByName<TestIdentifiable>("test")!!
        val firstClientTwo = syncListClientTwo.getAllElements().firstOrNull()

        Assert.assertEquals(null, firstClientTwo)
    }


    @Test
    fun afterElementAddAndClientConnect_ClientWillSeeElement() {
        val factory = CommunicationBootstrapFactoryGetter.getFactory()

        this.clientOne.shutdown().syncUninterruptibly()
        val serverSyncList =
            server.getClientServerSyncListManager().getSyncListByNameOrCreate<TestIdentifiable>("test")

        serverSyncList.addElement(TestIdentifiable("test2", 23)).syncUninterruptibly()

        clientOne = factory.createClient(Address("127.0.0.1", 1630))
        clientOne.start().syncUninterruptibly()


        val syncListClientOne = clientOne.getClientServerSyncListManager().getSyncListByName<TestIdentifiable>("test")!!
        val firstClientOne = syncListClientOne.getAllElements().first()

        Assert.assertEquals("test2", firstClientOne.getIdentifier())
    }


}
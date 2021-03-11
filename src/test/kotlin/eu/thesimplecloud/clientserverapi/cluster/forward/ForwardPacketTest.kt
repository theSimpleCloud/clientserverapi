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

package eu.thesimplecloud.clientserverapi.cluster.forward

import eu.thesimplecloud.clientserverapi.cluster.ICluster
import eu.thesimplecloud.clientserverapi.cluster.auth.impl.SecretAuthProvider
import eu.thesimplecloud.clientserverapi.cluster.component.node.IRemoteNode
import eu.thesimplecloud.clientserverapi.cluster.factory.DefaultClusterFactory
import eu.thesimplecloud.clientserverapi.cluster.type.IClientCluster
import eu.thesimplecloud.clientserverapi.lib.factory.CommunicationBootstrapFactoryGetter
import eu.thesimplecloud.clientserverapi.lib.util.Address
import eu.thesimplecloud.clientserverapi.testobject.client.PacketIOWork
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

class ForwardPacketTest {

    companion object {
        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            CommunicationBootstrapFactoryGetter.setEnvironment(CommunicationBootstrapFactoryGetter.ApplicationEnvironment.TEST)
        }
    }

    private lateinit var clusterOne: ICluster
    private lateinit var clusterTwo: ICluster
    private lateinit var clusterClient: IClientCluster

    @Before
    fun setUp() {
        clusterOne =
                DefaultClusterFactory().createNewCluster(
                        "1.0",
                        SecretAuthProvider("123"),
                        Address("127.0.0.1", 1500),
                        listOf("eu.thesimplecloud.clientserverapi.testobject.client")
                )

        clusterTwo = DefaultClusterFactory().joinClusterAsNode(
                "1.0", SecretAuthProvider("123"),
                Address("127.0.0.1", 1505),
                listOf(Address("127.0.0.1", 1500)),
                listOf("eu.thesimplecloud.clientserverapi.testobject.client"))

        clusterClient = DefaultClusterFactory().joinClusterAsClient(
                "1.0", SecretAuthProvider("123"),
                Address("127.0.0.1", 1500),
                listOf("eu.thesimplecloud.clientserverapi.testobject.client"))
    }

    @Test
    fun test() {
        val nodeByServerAddress = clusterClient.getComponentManager().getNodeByServerAddress(Address("127.0.0.1", 1505))!! as IRemoteNode
        println(1)
        nodeByServerAddress.getPacketSender().sendUnitQuery(PacketIOWork()).syncUninterruptibly()
        println(4)
        Thread.sleep(200)
    }

    @After
    fun tearDown() {
        println("teat down")
        clusterOne.shutdown().syncUninterruptibly()
        clusterTwo.shutdown().syncUninterruptibly()
        clusterClient.shutdown().syncUninterruptibly()
    }
}
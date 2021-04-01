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

package eu.thesimplecloud.clientserverapi.testing.client

import eu.thesimplecloud.clientserverapi.client.INettyClient
import eu.thesimplecloud.clientserverapi.cluster.ICluster
import eu.thesimplecloud.clientserverapi.lib.bootstrap.AbstractCommunicationBootstrap
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.handler.DefaultConnectionHandler
import eu.thesimplecloud.clientserverapi.lib.handler.IConnectionHandler
import eu.thesimplecloud.clientserverapi.lib.packet.codec.IPacketDecoder
import eu.thesimplecloud.clientserverapi.lib.packet.codec.IPacketEncoder
import eu.thesimplecloud.clientserverapi.lib.packet.codec.impl.DefaultPacketDecoder
import eu.thesimplecloud.clientserverapi.lib.packet.codec.impl.DefaultPacketEncoder
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.util.Address
import eu.thesimplecloud.clientserverapi.testing.NetworkTestManager

/**
 * Created by IntelliJ IDEA.
 * Date: 25.06.2020
 * Time: 10:57
 * @author Frederick Baier
 */

class TestNettyClient(
    address: Address,
    connectionHandler: IConnectionHandler = DefaultConnectionHandler(),
    packetEncoder: IPacketEncoder = DefaultPacketEncoder(),
    packetDecoder: IPacketDecoder = DefaultPacketDecoder(),
    cluster: ICluster? = null
) : AbstractCommunicationBootstrap(address, connectionHandler, packetEncoder, packetDecoder, cluster), INettyClient {

    private val clientConnection = TestClientConnection(this)

    override fun getConnection(): IConnection {
        return this.clientConnection
    }

    override fun start(): ICommunicationPromise<Unit> {
        return CommunicationPromise.runAsync({ NetworkTestManager.connectToServer(this@TestNettyClient) })
    }

    override fun shutdown(): ICommunicationPromise<Unit> {
        NetworkTestManager.closeConnection(this.clientConnection)
        return CommunicationPromise.UNIT_PROMISE
    }

    override fun isActive(): Boolean {
        return this.clientConnection.isOpen()
    }

}
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

import com.google.common.collect.Maps
import eu.thesimplecloud.clientserverapi.client.INettyClient
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.WrappedPacket
import eu.thesimplecloud.clientserverapi.server.INettyServer
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClient
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClientValue
import eu.thesimplecloud.clientserverapi.testing.client.TestNettyClient
import eu.thesimplecloud.clientserverapi.testing.server.TestClientManager
import eu.thesimplecloud.clientserverapi.testing.server.TestConnectedClient
import eu.thesimplecloud.clientserverapi.testing.server.TestNettyServer
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by IntelliJ IDEA.
 * Date: 11.06.2020
 * Time: 17:52
 * @author Frederick Baier
 */
object NetworkTestManager {

    private val portServerMap = Maps.newConcurrentMap<Int, TestNettyServer<out IConnectedClientValue>>()

    private val serverToConnectedClients = Maps.newConcurrentMap<INettyServer<*>, MutableList<INettyClient>>()

    private fun getServerListeningOnPort(port: Int): INettyServer<*>? {
        return portServerMap[port]
    }

    fun registerServer(server: TestNettyServer<out IConnectedClientValue>) {
        this.portServerMap[server.getPort()] = server
    }

    fun unregisterServer(server: INettyServer<*>) {
        this.portServerMap.remove(server.getPort())
    }

    fun isServerRegistered(server: INettyServer<*>): Boolean {
        return this.portServerMap.containsKey(server.getPort())
    }

    fun connectToServer(client: INettyClient) {
        val server = getServerListeningOnPort(client.getPort())
                ?: throw IllegalArgumentException("There is no server listening on port ${client.getPort()}")
        val list = this.serverToConnectedClients.getOrPut(server) { CopyOnWriteArrayList() }

        list.add(client)
        server as TestNettyServer<*>

        val clientManager = server.getClientManager() as TestClientManager<IConnectedClientValue>
        val connectedClient = TestConnectedClient(server, client.getConnection()) as TestConnectedClient<IConnectedClientValue>

        //set other side connections
        val clientConnection = client.getConnection() as AbstractTestConnection
        clientConnection.otherSideConnection = connectedClient

        clientManager.addClient(connectedClient)
        server.connectionHandler.onConnectionActive(connectedClient)

        client as TestNettyClient
        client.connectionHandler.onConnectionActive(client.getConnection())


    }

    fun sendPacket(fromConnection: IConnection, packet: WrappedPacket) {
        fromConnection as AbstractTestConnection
        val otherSideConnection = fromConnection.otherSideConnection!!
        otherSideConnection as AbstractTestConnection
        otherSideConnection.incomingPacket(packet)
    }

    fun closeConnection(connection: IConnection) {
        connection as AbstractTestConnection
        val otherSideConnection = connection.otherSideConnection as AbstractTestConnection?
        shutdownConnection(connection)
        otherSideConnection?.let { shutdownConnection(it) }

        otherSideConnection?.let { otherSideConnection.otherSideConnection = null }
        connection.otherSideConnection = null
    }

    private fun shutdownConnection(connection: AbstractTestConnection) {
        if (connection is IConnectedClient<*>) {
            disconnectFromServer(connection as IConnectedClient<IConnectedClientValue>)
        } else {
            performDisconnectOnClient(connection)
        }
    }

    private fun performDisconnectOnClient(connection: IConnection) {
        val client = connection.getCommunicationBootstrap() as TestNettyClient
        client.connectionHandler.onConnectionInactive(connection)
    }

    private fun disconnectFromServer(connection: IConnectedClient<IConnectedClientValue>) {
        val server = connection.getCommunicationBootstrap() as TestNettyServer<IConnectedClientValue>
        val clientManager = server.getClientManager() as TestClientManager<IConnectedClientValue>
        clientManager.removeClient(connection)
        server.connectionHandler.onConnectionInactive(connection)
    }

}
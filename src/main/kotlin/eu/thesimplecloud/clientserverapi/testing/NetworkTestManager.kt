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
import eu.thesimplecloud.clientserverapi.lib.bootstrap.ICommunicationBootstrap
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.WrappedPacket
import eu.thesimplecloud.clientserverapi.server.INettyServer
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClient
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

    private val portServerMap = Maps.newConcurrentMap<Int, TestNettyServer>()

    private val serverToConnectedClients = Maps.newConcurrentMap<INettyServer, MutableList<INettyClient>>()

    private fun getServerListeningOnPort(port: Int): INettyServer? {
        return portServerMap[port]
    }

    fun registerServer(server: TestNettyServer) {
        this.portServerMap[server.getAddress().port] = server
    }

    fun unregisterServer(server: INettyServer) {
        this.portServerMap.remove(server.getAddress().port)
    }

    fun isServerRegistered(server: INettyServer): Boolean {
        return this.portServerMap.containsKey(server.getAddress().port)
    }

    fun connectToServer(client: INettyClient) {
        val server = getServerListeningOnPort(client.getAddress().port)
                ?: throw IllegalArgumentException("There is no server listening on port ${client.getAddress().port}")
        val list = this.serverToConnectedClients.getOrPut(server) { CopyOnWriteArrayList() }
        list.add(client)

        server as TestNettyServer

        val clientManager = server.getClientManager() as TestClientManager
        val connectedClient = TestConnectedClient(server, client.getConnection())
        clientManager.addClient(connectedClient)

        //set other side connections
        setConnectionActive(client, client.getConnection(), connectedClient)

        setConnectionActive(server, connectedClient, client.getConnection())
    }

    private fun setConnectionActive(
            forBootstrap: ICommunicationBootstrap,
            connectionOnBoostrapSide: IConnection,
            connectionOnOtherSide: IConnection
    ) {
        connectionOnBoostrapSide as AbstractTestConnection
        connectionOnBoostrapSide.otherSideConnection = connectionOnOtherSide
        forBootstrap.getConnectionHandler().onConnectionActive(connectionOnBoostrapSide)
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
    }

    private fun shutdownConnection(connection: AbstractTestConnection) {
        if (connection is IConnectedClient) {
            disconnectFromServer(connection)
        } else {
            performDisconnectOnClient(connection)
        }
        connection.otherSideConnection = null
    }

    private fun performDisconnectOnClient(connection: IConnection) {
        val client = connection.getCommunicationBootstrap() as TestNettyClient
        client.getConnectionHandler().onConnectionInactive(connection)
    }

    private fun disconnectFromServer(connection: IConnectedClient) {
        val server = connection.getCommunicationBootstrap()
        val clientManager = server.getClientManager() as TestClientManager
        clientManager.removeClient(connection)
        server.getConnectionHandler().onConnectionInactive(connection)
    }

}
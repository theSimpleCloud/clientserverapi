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

import com.google.common.collect.HashBiMap
import eu.thesimplecloud.clientserverapi.client.INettyClient
import eu.thesimplecloud.clientserverapi.lib.bootstrap.ICommunicationBootstrap
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.server.INettyServer
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by IntelliJ IDEA.
 * Date: 11.06.2020
 * Time: 17:52
 * @author Frederick Baier
 */
object NetworkTestManager {

    private val portServerMap = HashMap<Int, INettyServer<*>>()

    private val serverToConnectedClients = HashBiMap.create<INettyServer<*>, MutableList<INettyClient>>()

    fun getServerListeningOnPort(port: Int): INettyServer<*>? {
        return portServerMap[port]
    }

    fun connectToServer(client: INettyClient, port: Int) {
        val server = getServerListeningOnPort(port)
                ?: throw IllegalArgumentException("There is no server listening on port $port")
        val list = serverToConnectedClients.getOrPut(server) { CopyOnWriteArrayList() }
        list.add(client)
        server.getClientManager()
        client
    }

    fun getServerClientIsConnectedTo(nettyClient: INettyClient): INettyServer<*>? {
        val inverse = this.serverToConnectedClients.inverse()
        val value = inverse.keys.filter { it.contains(nettyClient) }
        return inverse[value]
    }

    fun sendPacket(senderBootstrap: ICommunicationBootstrap, receiverConnection: IConnection) {

    }

}
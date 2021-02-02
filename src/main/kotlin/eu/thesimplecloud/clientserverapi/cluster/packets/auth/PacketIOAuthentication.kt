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

package eu.thesimplecloud.clientserverapi.cluster.packets.auth

import eu.thesimplecloud.clientserverapi.cluster.node.impl.DefaultRemoteNode
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClient

/**
 * Created by IntelliJ IDEA.
 * Date: 31/01/2021
 * Time: 15:37
 * @author Frederick Baier
 */
abstract class PacketIOAuthentication<T : NodeInfo>() : ObjectPacket<T>() {

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Any> {
        val value = this.value ?: return contentException("value")
        val cluster = connection.getCommunicationBootstrap().getCluster()!!
        if (value.version != cluster.getVersion()) {
            return failure(NotTheSameVersionException())
        }
        val authSuccess = handleAuth(connection, value)
        if (!authSuccess) {
            return failure(AuthFailedException())
        }
        connection.setAuthenticated(true)
        val node = DefaultRemoteNode(connection, cluster, value.serverAddress)
        connection as IConnectedClient
        connection.setClientValue("node", node)
        cluster.getClusterAdapters().forEach { it.onNodeJoin(node) }
        return unit()
    }

    abstract fun handleAuth(connection: IConnection, value: T): Boolean

    class NotTheSameVersionException(): Exception()

    class AuthFailedException() : Exception()

    override fun isAuthRequired(): Boolean {
        return false
    }
}
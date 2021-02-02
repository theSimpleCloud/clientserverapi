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

package eu.thesimplecloud.clientserverapi.server.client.clientmanager

import eu.thesimplecloud.clientserverapi.lib.connection.AbstractNettyConnection
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.server.INettyServer
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.ConnectedClient
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClient
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClientValue
import io.netty.channel.ChannelHandlerContext
import java.util.concurrent.CopyOnWriteArrayList

class ClientManager(private val nettyServer: INettyServer) : IClientManager {
    private val clients = CopyOnWriteArrayList<IConnectedClient>()

    /**
     * Adds the specified [ChannelHandlerContext] as a client
     */
    fun addClient(ctx: ChannelHandlerContext): IConnection {
        val connection = ConnectedClient(ctx.channel(), nettyServer)
        this.clients.add(connection)
        return connection
    }

    /**
     * Returns the client registered to the specified [ChannelHandlerContext] or null if there is no client registered to the specified [ChannelHandlerContext]
     */
    fun getClient(ctx: ChannelHandlerContext) = this.clients.firstOrNull {
        it as AbstractNettyConnection
        it.getChannel() == ctx.channel()
    }

    override fun <T : IConnectedClientValue> getClientByClientValue(name: String, clientValue: T): IConnectedClient? {
        return this.clients.firstOrNull { it.getClientValue<T>(name) == clientValue }
    }


    /**
     * Removes the specified [ChannelHandlerContext] from the client list
     * @return the client that was registered.
     */
    fun removeClient(ctx: ChannelHandlerContext): IConnection? {
        val client = getClient(ctx)
        client?.let { this.clients.remove(it) }
        return client
    }

    override fun getClients(): Collection<IConnectedClient> = clients.filter { it.isOpen() }

}
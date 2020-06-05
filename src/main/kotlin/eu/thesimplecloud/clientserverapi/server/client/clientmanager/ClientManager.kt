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

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.server.NettyServer
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.ConnectedClient
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClient
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClientValue
import io.netty.channel.ChannelHandlerContext
import java.util.concurrent.CopyOnWriteArrayList

class ClientManager<T : IConnectedClientValue>(private val nettyServer: NettyServer<T>) : IClientManager<T> {
    private val clients = CopyOnWriteArrayList<IConnectedClient<T>>()

    fun addClient(ctx: ChannelHandlerContext): IConnection {
        val connection = ConnectedClient<T>(ctx.channel(), nettyServer)
        this.clients.add(connection)
        return connection
    }

    override fun getClient(ctx: ChannelHandlerContext) = this.clients.firstOrNull { it.getChannel() == ctx.channel() }

    override fun getClientByClientValue(clientValue: IConnectedClientValue): IConnectedClient<T>? = this.clients.firstOrNull { it.getClientValue() == clientValue }

    fun removeClient(ctx: ChannelHandlerContext): IConnection? {
        val client = getClient(ctx)
        client?.let { this.clients.remove(it) }
        return client
    }

    override fun getClients(): Collection<IConnectedClient<T>> = clients.filter { it.isOpen() }

}
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

package eu.thesimplecloud.clientserverapi.server.client.connectedclient

import eu.thesimplecloud.clientserverapi.lib.connection.AbstractNettyConnection
import eu.thesimplecloud.clientserverapi.server.INettyServer
import io.netty.channel.Channel

class ConnectedClient<T : IConnectedClientValue>(
        private val channel: Channel,
        private val nettyServer: INettyServer<T>
) : AbstractNettyConnection(), IConnectedClient<T> {


    @Volatile private var clientValue: T? = null

    override fun getClientValue(): T? = clientValue

    override fun setClientValue(connectedClientValue: T) {
        this.clientValue = connectedClientValue
    }

    override fun getNettyServer(): INettyServer<T> = nettyServer

    override fun getCommunicationBootstrap(): INettyServer<T> = nettyServer

    override fun getChannel(): Channel = channel


}
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

package eu.thesimplecloud.clientserverapi.testing.server

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.server.INettyServer
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClient
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClientValue
import eu.thesimplecloud.clientserverapi.testing.AbstractTestConnection

/**
 * Created by IntelliJ IDEA.
 * Date: 25.06.2020
 * Time: 14:14
 * @author Frederick Baier
 */

class TestConnectedClient<T : IConnectedClientValue>(
        private val nettyServer: INettyServer<T>,
        otherSideConnection: IConnection
) : AbstractTestConnection(), IConnectedClient<T> {

    private var clientValue: T? = null

    init {
        this.otherSideConnection = otherSideConnection
    }

    override fun getCommunicationBootstrap(): INettyServer<T> {
        return this.nettyServer
    }

    override fun getNettyServer(): INettyServer<T> {
        return nettyServer
    }

    override fun getClientValue(): T? = clientValue

    override fun setClientValue(connectedClientValue: T) {
        this.clientValue = connectedClientValue
    }
}

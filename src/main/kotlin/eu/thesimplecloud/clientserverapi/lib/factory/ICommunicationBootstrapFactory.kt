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

package eu.thesimplecloud.clientserverapi.lib.factory

import eu.thesimplecloud.clientserverapi.client.INettyClient
import eu.thesimplecloud.clientserverapi.cluster.ICluster
import eu.thesimplecloud.clientserverapi.lib.handler.DefaultConnectionHandler
import eu.thesimplecloud.clientserverapi.lib.handler.DefaultServerHandler
import eu.thesimplecloud.clientserverapi.lib.handler.IConnectionHandler
import eu.thesimplecloud.clientserverapi.lib.handler.IServerHandler
import eu.thesimplecloud.clientserverapi.lib.util.Address
import eu.thesimplecloud.clientserverapi.server.INettyServer

/**
 * Created by IntelliJ IDEA.
 * Date: 25.09.2020
 * Time: 18:58
 * @author Frederick Baier
 */
interface ICommunicationBootstrapFactory {

    /**
     * Creates a server
     * @param address the address to bind to
     * @param connectionHandler the handler to listen for connections (connect, disconnect, failure)
     * @param serverHandler the handler to listen for server events (start, close, failure)
     */
    fun createServer(
        address: Address,
        connectionHandler: IConnectionHandler = DefaultConnectionHandler(),
        serverHandler: IServerHandler = DefaultServerHandler(),
        cluster: ICluster? = null
    ): INettyServer

    /**
     * Creates a client
     * @param address the address to connect to
     * @param connectionHandler the handler to listen for connect, close, failure
     */
    fun createClient(
        address: Address,
        connectionHandler: IConnectionHandler = DefaultConnectionHandler(),
        cluster: ICluster? = null
    ): INettyClient

}
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

package eu.thesimplecloud.clientserverapi.cluster.node.impl

import eu.thesimplecloud.clientserverapi.cluster.ICluster
import eu.thesimplecloud.clientserverapi.cluster.node.ISelfNode
import eu.thesimplecloud.clientserverapi.cluster.node.handler.NodeConnectionHandler
import eu.thesimplecloud.clientserverapi.cluster.node.handler.ServerHandler
import eu.thesimplecloud.clientserverapi.lib.access.AuthAccessHandler
import eu.thesimplecloud.clientserverapi.lib.factory.BootstrapFactoryGetter
import eu.thesimplecloud.clientserverapi.lib.util.Address
import eu.thesimplecloud.clientserverapi.server.INettyServer

/**
 * Created by IntelliJ IDEA.
 * Date: 30/01/2021
 * Time: 17:53
 * @author Frederick Baier
 */
class DefaultSelfNode(
    private val bindAddress: Address,
    private val cluster: ICluster,
    packetsPackages: List<String>
) : ISelfNode {

    private val startupTime = System.currentTimeMillis()

    private val server = BootstrapFactoryGetter.getFactory()
        .createServer(bindAddress, NodeConnectionHandler(), ServerHandler(), cluster)

    init {
        server.setAccessHandler(AuthAccessHandler())
        server.addPacketsByPackage("eu.thesimplecloud.clientserverapi.cluster.packets")
        server.addPacketsByPackage(*packetsPackages.toTypedArray())
        server.start().syncUninterruptibly()
    }

    override fun getServer(): INettyServer {
        return this.server
    }

    override fun getStartupTime(): Long {
        return this.startupTime
    }

    override fun getServerAddress(): Address {
        return this.bindAddress
    }

    override fun getCluster(): ICluster {
        return this.cluster
    }
}
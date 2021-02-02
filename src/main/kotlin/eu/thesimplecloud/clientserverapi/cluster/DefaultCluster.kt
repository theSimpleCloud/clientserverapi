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

package eu.thesimplecloud.clientserverapi.cluster

import eu.thesimplecloud.clientserverapi.client.INettyClient
import eu.thesimplecloud.clientserverapi.cluster.adapter.IClusterAdapter
import eu.thesimplecloud.clientserverapi.cluster.adapter.impl.DefaultClusterAdapter
import eu.thesimplecloud.clientserverapi.cluster.auth.IClusterAuthProvider
import eu.thesimplecloud.clientserverapi.cluster.list.manager.ClusterListManager
import eu.thesimplecloud.clientserverapi.cluster.list.manager.IClusterListManager
import eu.thesimplecloud.clientserverapi.cluster.node.INode
import eu.thesimplecloud.clientserverapi.cluster.node.IRemoteNode
import eu.thesimplecloud.clientserverapi.cluster.node.ISelfNode
import eu.thesimplecloud.clientserverapi.cluster.node.handler.NodeConnectionHandler
import eu.thesimplecloud.clientserverapi.cluster.node.impl.DefaultRemoteNode
import eu.thesimplecloud.clientserverapi.cluster.node.impl.DefaultSelfNode
import eu.thesimplecloud.clientserverapi.cluster.packets.auth.NodeInfo
import eu.thesimplecloud.clientserverapi.lib.factory.BootstrapFactoryGetter
import eu.thesimplecloud.clientserverapi.lib.util.Address
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by IntelliJ IDEA.
 * Date: 30/01/2021
 * Time: 17:55
 * @author Frederick Baier
 */
class DefaultCluster(
    private val version: String,
    private val authProvider: IClusterAuthProvider,
    bindAddress: Address,
    remoteAddresses: List<NodeInfo> = emptyList()
) : ICluster, IClusterAdapter {

    private val clusterListManager = ClusterListManager(this)

    private val selfNode = DefaultSelfNode(bindAddress, this)

    private val clusterAdapters = CopyOnWriteArrayList<IClusterAdapter>()

    private val nodes: CopyOnWriteArrayList<INode>

    init {
        val remoteNodes = remoteAddresses.map {
            DefaultRemoteNode(
                createNodeClient(it.serverAddress).getConnection(),
                this,
                it.serverAddress,
                it.startupTime
            )
        }
        nodes = CopyOnWriteArrayList(remoteNodes.union(listOf(selfNode)))
        authProvider.authenticateOnRemoteNodes(this, getRemoteNodes())

        addClusterAdapter(this)
        addClusterAdapter(DefaultClusterAdapter())
    }

    override fun getHeadNode(): INode {
        return getNodes().minByOrNull { it.getStartupTime() }!!
    }

    override fun getSelfNode(): ISelfNode {
        return selfNode
    }

    override fun getNodes(): List<INode> {
        return this.nodes
    }

    override fun getAuthProvider(): IClusterAuthProvider {
        return this.authProvider
    }

    override fun getClusterAdapters(): List<IClusterAdapter> {
        return this.clusterAdapters
    }

    override fun addClusterAdapter(adapter: IClusterAdapter) {
        this.clusterAdapters.add(adapter)
    }

    override fun removeClusterAdapter(adapter: IClusterAdapter) {
        this.clusterAdapters.remove(adapter)
    }

    override fun getVersion(): String {
        return version
    }

    override fun getClusterListManager(): IClusterListManager {
        return this.clusterListManager
    }

    private fun createNodeClient(address: Address): INettyClient {
        val factory = BootstrapFactoryGetter.getFactory()
        val client = factory.createClient(address, NodeConnectionHandler(), cluster = this)
        client.addPacketsByPackage("eu.thesimplecloud.clientserverapi.cluster.packets")
        client.start().syncUninterruptibly()
        return client
    }

    override fun onNodeJoin(remoteNode: IRemoteNode) {
        this.nodes.add(remoteNode)
    }

    override fun onNodeLeave(remoteNode: IRemoteNode) {
        this.nodes.removeIf {it === remoteNode }
    }
}
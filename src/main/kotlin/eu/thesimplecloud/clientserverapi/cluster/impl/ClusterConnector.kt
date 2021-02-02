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

package eu.thesimplecloud.clientserverapi.cluster.impl

import eu.thesimplecloud.clientserverapi.client.INettyClient
import eu.thesimplecloud.clientserverapi.cluster.ICluster
import eu.thesimplecloud.clientserverapi.cluster.node.IRemoteNode
import eu.thesimplecloud.clientserverapi.cluster.node.handler.NodeConnectionHandler
import eu.thesimplecloud.clientserverapi.cluster.node.impl.DefaultRemoteNode
import eu.thesimplecloud.clientserverapi.cluster.packets.PacketIOGetAllNodes
import eu.thesimplecloud.clientserverapi.cluster.packets.auth.NodeInfo
import eu.thesimplecloud.clientserverapi.lib.factory.BootstrapFactoryGetter
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.sendQuery
import eu.thesimplecloud.clientserverapi.lib.util.Address

/**
 * Created by IntelliJ IDEA.
 * Date: 02/02/2021
 * Time: 09:54
 * @author Frederick Baier
 */
class ClusterConnector(
    private val cluster: ICluster,
    private val connectAddress: Address
) {

    fun connectToAllNodes(): List<IRemoteNode> {
        val firstClient = startClusterClient(connectAddress)
        val allNodeInfos = getAllNodeInfos(firstClient)
        val firstClientNodeInfo = allNodeInfos.firstOrNull { it.serverAddress == connectAddress }
            ?: throw IllegalStateException("First Client's NodeInfo was not included")
        val firstRemoteNode = DefaultRemoteNode(firstClient.getConnection(), cluster, firstClientNodeInfo.serverAddress, firstClientNodeInfo.startupTime)

        val allOtherNodeInfos = allNodeInfos.filter { it.serverAddress != firstClientNodeInfo.serverAddress }
        val otherNodes = allOtherNodeInfos.map { connectToRemoteNode(it) }
        return otherNodes.union(listOf(firstRemoteNode)).toList()
    }

    private fun getAllNodeInfos(client: INettyClient): List<NodeInfo> {
        val promise = client.getConnection().sendQuery<Array<NodeInfo>>(PacketIOGetAllNodes(), 4000)
        return promise.getBlocking().toList()
    }

    private fun startClusterClient(address: Address): INettyClient {
        val factory = BootstrapFactoryGetter.getFactory()
        val client = factory.createClient(address, NodeConnectionHandler(), cluster = cluster)
        client.addPacketsByPackage("eu.thesimplecloud.clientserverapi.cluster.packets")
        client.start().syncUninterruptibly()
        return client
    }

    private fun connectToRemoteNode(nodeInfo: NodeInfo): DefaultRemoteNode {
        val clusterClient = startClusterClient(nodeInfo.serverAddress)
        return DefaultRemoteNode(clusterClient.getConnection(), cluster, nodeInfo.serverAddress, nodeInfo.startupTime)
    }

}
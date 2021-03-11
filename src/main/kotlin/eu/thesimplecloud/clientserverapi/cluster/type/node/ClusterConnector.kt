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

package eu.thesimplecloud.clientserverapi.cluster.type.node

import eu.thesimplecloud.clientserverapi.client.INettyClient
import eu.thesimplecloud.clientserverapi.cluster.component.IRemoteClusterComponent
import eu.thesimplecloud.clientserverapi.cluster.component.handler.ClusterConnectionHandler
import eu.thesimplecloud.clientserverapi.cluster.component.node.IRemoteNode
import eu.thesimplecloud.clientserverapi.cluster.component.node.impl.DefaultRemoteNode
import eu.thesimplecloud.clientserverapi.cluster.packets.PacketIOGetAllComponents
import eu.thesimplecloud.clientserverapi.cluster.packets.auth.dto.ClientComponentDTO
import eu.thesimplecloud.clientserverapi.cluster.packets.auth.dto.ComponentDTO
import eu.thesimplecloud.clientserverapi.cluster.packets.auth.dto.NodeComponentDTO
import eu.thesimplecloud.clientserverapi.cluster.type.AbstractCluster
import eu.thesimplecloud.clientserverapi.lib.access.AuthAccessHandler
import eu.thesimplecloud.clientserverapi.lib.factory.CommunicationBootstrapFactoryGetter
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.sendQuery
import eu.thesimplecloud.clientserverapi.lib.util.Address
import eu.thesimplecloud.jsonlib.JsonLib

/**
 * Created by IntelliJ IDEA.
 * Date: 02/02/2021
 * Time: 09:54
 * @author Frederick Baier
 */
class ClusterConnector(
    private val cluster: AbstractCluster,
    private val connectAddress: Address,
    private val packetsPackages: List<String>,
    private val connectMethod: ConnectMethod
) {

    private val firstClient: INettyClient
    private val allComponents: List<ComponentDTO>
    val firstRemoteNode: IRemoteNode

    init {
        firstClient = startClusterClient(connectAddress)
        allComponents = getAllComponents(firstClient)
        val allNodeInfos = allComponents.filterIsInstance<NodeComponentDTO>()
        firstRemoteNode = createFirstRemoteNode(firstClient, allNodeInfos)
        firstClient.getConnection().setProperty("component", firstRemoteNode)
    }

    fun connectToCluster() {
        val allNodeInfos = allComponents.filterIsInstance<NodeComponentDTO>()
        cluster.onComponentJoin(firstRemoteNode, cluster.getSelfComponent())

        if (connectMethod == ConnectMethod.ONE_NODE) {
            val missingComponents = MissingClusterComponentsCreator(cluster, listOf(firstRemoteNode), allComponents).createMissingComponents()
            joinComponentsToCluster(missingComponents)
            return
        }

        val allOtherNodeInfos = allNodeInfos.filter { it.serverAddress != firstRemoteNode.getServerAddress() }
        val otherNodes = allOtherNodeInfos.map { connectToRemoteNode(it) }
        joinComponentsToCluster(otherNodes)
        val allRemoteNodes = otherNodes.union(listOf(firstRemoteNode)).toList()

        val missingComponents = MissingClusterComponentsCreator(cluster, allRemoteNodes, allComponents).createMissingComponents()
        joinComponentsToCluster(missingComponents)
    }

    private fun joinComponentsToCluster(components: List<IRemoteClusterComponent>) {
        components.forEach { this.cluster.onComponentJoin(it, cluster.getSelfComponent()) }
    }

    private fun createFirstRemoteNode(client: INettyClient, allNodeInfos: List<NodeComponentDTO>): IRemoteNode {
        val firstClientNodeInfo = allNodeInfos.firstOrNull { it.serverAddress == connectAddress }
            ?: throw IllegalStateException("First Client's NodeInfo was not included")
        return DefaultRemoteNode(client.getConnection(), cluster, firstClientNodeInfo.serverAddress, firstClientNodeInfo.uniqueId, firstClientNodeInfo.startupTime)
    }

    private fun getAllComponents(client: INettyClient): List<ComponentDTO> {
        val promise = client.getConnection().sendQuery<JsonLib>(PacketIOGetAllComponents(), 4000)
        val jsonLib = promise.getBlocking()
        val nodesArray = jsonLib.getObject("nodes", Array<NodeComponentDTO>::class.java)!!
        val clientsArray = jsonLib.getObject("clients", Array<ClientComponentDTO>::class.java)!!
        return nodesArray.union(clientsArray.toList()).toList()
    }

    private fun startClusterClient(address: Address): INettyClient {
        val factory = CommunicationBootstrapFactoryGetter.getFactory()
        val client = factory.createClient(address, ClusterConnectionHandler(), cluster = cluster)
        client.setAccessHandler(AuthAccessHandler())
        client.addPacketsByPackage("eu.thesimplecloud.clientserverapi.cluster.packets")
        client.addPacketsByPackage(*packetsPackages.toTypedArray())
        client.start().syncUninterruptibly()
        client.getConnection().setAuthenticated(true)
        return client
    }

    private fun connectToRemoteNode(nodeInfo: NodeComponentDTO): DefaultRemoteNode {
        val clusterClient = startClusterClient(nodeInfo.serverAddress)
        val remoteNode =  DefaultRemoteNode(clusterClient.getConnection(), cluster, nodeInfo.serverAddress, nodeInfo.uniqueId, nodeInfo.startupTime)
        clusterClient.getConnection().setProperty("component", remoteNode)
        return remoteNode
    }

    enum class ConnectMethod {

        ALL_NODES, ONE_NODE

    }

}
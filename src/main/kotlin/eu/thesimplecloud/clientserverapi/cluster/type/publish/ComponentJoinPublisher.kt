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

package eu.thesimplecloud.clientserverapi.cluster.type.publish

import eu.thesimplecloud.clientserverapi.cluster.ICluster
import eu.thesimplecloud.clientserverapi.cluster.component.IClusterComponent
import eu.thesimplecloud.clientserverapi.cluster.component.IRemoteClusterComponent
import eu.thesimplecloud.clientserverapi.cluster.component.client.IClusterClient
import eu.thesimplecloud.clientserverapi.cluster.component.client.IRemoteClusterClient
import eu.thesimplecloud.clientserverapi.cluster.component.node.ISelfNode
import eu.thesimplecloud.clientserverapi.cluster.packets.component.PacketIOClusterComponentJoin
import eu.thesimplecloud.clientserverapi.cluster.type.INodeCluster

class ComponentJoinPublisher(
    cluster: ICluster,
    private val joiningComponent: IRemoteClusterComponent,
    private val senderComponent: IClusterComponent
) : AbstractComponentActionPublisher(cluster, joiningComponent) {


    fun publishComponentJoin() {
        if (!shallPublish()) {
            return
        }

        publishComponentJoin0()
    }

    private fun publishComponentJoin0() {
        publishJoinToSelfClients()

        if (senderComponent is ISelfNode && joiningComponent is IClusterClient) {
            publishJoinToAllNodes()
        }
    }

    private fun publishJoinToSelfClients() {
        cluster as INodeCluster
        val packet = PacketIOClusterComponentJoin(joiningComponent.getComponentDTO())
        val allSelfClients = getSelfClients()
        val selfClientsWithoutJoiningOne = allSelfClients.filter { it != joiningComponent }
        selfClientsWithoutJoiningOne.forEach { it.getPacketSender().sendUnitQuery(packet) }
    }



    private fun getSelfClients(): List<IRemoteClusterClient> {
        val componentManager = this.cluster.getComponentManager()
        return componentManager.getRemoteComponents().filterIsInstance<IRemoteClusterClient>().filter { it.isConnectedDirect() }
    }

    private fun publishJoinToAllNodes() {
        val packet = PacketIOClusterComponentJoin(joiningComponent.getComponentDTO())
        val nodesPacketSender = cluster.getNodesPacketSender()
        nodesPacketSender.sendUnitQuery(packet)
    }

}
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

package eu.thesimplecloud.clientserverapi.cluster.component.manager

import eu.thesimplecloud.clientserverapi.cluster.component.IClusterComponent
import eu.thesimplecloud.clientserverapi.cluster.component.IRemoteClusterComponent
import eu.thesimplecloud.clientserverapi.cluster.component.ISelfClusterComponent
import eu.thesimplecloud.clientserverapi.cluster.component.client.IClusterClient
import eu.thesimplecloud.clientserverapi.cluster.component.node.INode
import eu.thesimplecloud.clientserverapi.cluster.component.node.IRemoteNode
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.IPacketSender
import eu.thesimplecloud.clientserverapi.lib.util.Address
import java.util.*

interface IComponentManager {

    /**
     * Returns the head node
     */
    fun getHeadNode(): INode

    /**
     * Returns the component found by the specified [sender]
     */
    fun getComponentByPacketSender(sender: IPacketSender): IRemoteClusterComponent?

    /**
     * Returns the component found by the specified [uniqueId]
     */
    fun getComponentByUniqueId(uniqueId: UUID): IClusterComponent?

    /**
     * Returns all components in this cluster
     */
    fun getComponents(): List<IClusterComponent>

    /**
     * Returns all components in this cluster
     */
    fun getClients(): List<IClusterClient> {
        return this.getComponents().filterIsInstance<IClusterClient>()
    }

    /**
     * Returns all nodes in the cluster
     */
    fun getNodes(): List<INode> {
        return this.getComponents().filterIsInstance<INode>()
    }

    /**
     * Returns all nodes in the cluster
     */
    fun getNodeByServerAddress(serverAddress: Address): INode? {
        return getNodes().firstOrNull { it.getServerAddress() == serverAddress }
    }

    /**
     * Returns all remote nodes
     */
    fun getRemoteNodes(): List<IRemoteNode> {
        return this.getRemoteComponents().filterIsInstance<IRemoteNode>()
    }

    /**
     * Returns all remote components in the cluster
     */
    fun getRemoteComponents(): List<IRemoteClusterComponent>

    /**
     * Returns the self component
     */
    fun getSelfComponent(): ISelfClusterComponent

}
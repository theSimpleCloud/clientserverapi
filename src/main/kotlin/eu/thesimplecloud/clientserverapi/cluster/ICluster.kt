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

import eu.thesimplecloud.clientserverapi.cluster.adapter.IClusterListenerAdapter
import eu.thesimplecloud.clientserverapi.cluster.auth.IClusterAuthProvider
import eu.thesimplecloud.clientserverapi.cluster.component.ISelfClusterComponent
import eu.thesimplecloud.clientserverapi.cluster.component.manager.IComponentManager
import eu.thesimplecloud.clientserverapi.cluster.component.node.INode
import eu.thesimplecloud.clientserverapi.cluster.packetsender.IClientsPacketSender
import eu.thesimplecloud.clientserverapi.cluster.packetsender.INodesPacketSender
import eu.thesimplecloud.clientserverapi.lib.list.manager.ISyncListManager
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

/**
 * Created by IntelliJ IDEA.
 * Date: 30/01/2021
 * Time: 16:11
 * @author Frederick Baier
 */
interface ICluster {

    fun getHeadNode(): INode {
        return this.getComponentManager().getHeadNode()
    }

    /**
     * Returns the auth provider of this cluster
     */
    fun getAuthProvider() : IClusterAuthProvider

    /**
     * Returns all [IClusterListenerAdapter]s
     */
    fun getClusterListeners(): List<IClusterListenerAdapter>

    /**
     * Adds the specified [IClusterListenerAdapter]
     */
    fun addListener(listener: IClusterListenerAdapter)

    /**
     * Removes the specified [IClusterListenerAdapter]
     */
    fun removeListener(listener: IClusterListenerAdapter)

    /**
     * Returns the [IComponentManager]
     */
    fun getComponentManager(): IComponentManager

    /**
     * Returns the self component
     */
    fun getSelfComponent(): ISelfClusterComponent {
        return this.getComponentManager().getSelfComponent()
    }

    /**
     * Returns the [INodesPacketSender] used to send packets to all nodes in the cluster
     */
    fun getNodesPacketSender(): INodesPacketSender

    /**
     * Returns the [IClientsPacketSender] used to send packets to all clients in the cluster
     */
    fun getClientsPacketSender(): IClientsPacketSender

    /**
     * Returns the version of this cluster
     */
    fun getVersion(): String

    /**
     * Returns the [ISyncListManager]
     */
    fun getClusterListManager(): ISyncListManager

    /**
     * Shuts this node down
     */
    fun shutdown(): ICommunicationPromise<Unit>

}
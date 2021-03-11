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

import eu.thesimplecloud.clientserverapi.cluster.auth.IClusterAuthProvider
import eu.thesimplecloud.clientserverapi.cluster.component.node.IRemoteNode
import eu.thesimplecloud.clientserverapi.cluster.component.node.ISelfNode
import eu.thesimplecloud.clientserverapi.cluster.component.node.impl.DefaultSelfNode
import eu.thesimplecloud.clientserverapi.cluster.packetsender.ISelfClientsPacketSender
import eu.thesimplecloud.clientserverapi.cluster.packetsender.impl.DefaultSelfClientsPacketSender
import eu.thesimplecloud.clientserverapi.cluster.type.AbstractCluster
import eu.thesimplecloud.clientserverapi.cluster.type.INodeCluster
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.combineAllPromises
import eu.thesimplecloud.clientserverapi.lib.util.Address

/**
 * Created by IntelliJ IDEA.
 * Date: 30/01/2021
 * Time: 17:55
 * @author Frederick Baier
 */
class DefaultNodeCluster(
    version: String,
    authProvider: IClusterAuthProvider,
    bindAddress: Address,
    connectAddresses: List<Address>,
    packetsPackages: List<String>
) : AbstractCluster(version, authProvider), INodeCluster {

    private val selfNode = DefaultSelfNode(bindAddress, this, packetsPackages)

    private val selfClientsPacketSender = DefaultSelfClientsPacketSender(this)

    init {
        println("------------Node with id ${selfNode.getUniqueId()}")
        if (connectAddresses.isEmpty()) {
            emptyList<IRemoteNode>()
        } else {
            connectToCluster(connectAddresses, packetsPackages)
        }

        try {
            this.getAuthProvider().authenticateOnRemoteNodes(this, this.componentManager.getRemoteNodes())
        } catch (e: Exception) {
            selfNode.getServer().shutdown()
            throw e
        }
    }

    private fun connectToCluster(connectAddresses: List<Address>, packetsPackages: List<String>) {
        connectAddresses.forEach {
            try {
                ClusterConnector(this, it, packetsPackages, ClusterConnector.ConnectMethod.ALL_NODES).connectToCluster()
                return
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    override fun getSelfComponent(): ISelfNode {
        return this.selfNode
    }

    override fun getSelfClientsPacketSender(): ISelfClientsPacketSender {
        return this.selfClientsPacketSender
    }

    override fun shutdown(): ICommunicationPromise<Unit> {
        val remoteNodes = this.getComponentManager().getRemoteNodes()
        val shutdownPromises = remoteNodes.map { it.getPacketSender().getCommunicationBootstrap().shutdown() }
        val serverShutdownPromise = selfNode.getServer().shutdown()
        return shutdownPromises.union(listOf(serverShutdownPromise)).combineAllPromises()
    }

}
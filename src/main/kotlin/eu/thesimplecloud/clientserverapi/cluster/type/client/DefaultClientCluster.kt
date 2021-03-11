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

package eu.thesimplecloud.clientserverapi.cluster.type.client

import eu.thesimplecloud.clientserverapi.cluster.auth.IClusterAuthProvider
import eu.thesimplecloud.clientserverapi.cluster.component.client.ISelfClusterClient
import eu.thesimplecloud.clientserverapi.cluster.component.client.impl.DefaultSelfClusterClient
import eu.thesimplecloud.clientserverapi.cluster.component.node.IRemoteNode
import eu.thesimplecloud.clientserverapi.cluster.type.AbstractCluster
import eu.thesimplecloud.clientserverapi.cluster.type.IClientCluster
import eu.thesimplecloud.clientserverapi.cluster.type.node.ClusterConnector
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.util.Address
import java.util.*

/**
 * Created by IntelliJ IDEA.
 * Date: 20/02/2021
 * Time: 10:28
 * @author Frederick Baier
 */
class DefaultClientCluster(
    version: String,
    authProvider: IClusterAuthProvider,
    serverAddress: Address,
    packetsPackages: List<String>
) : AbstractCluster(version, authProvider), IClientCluster {

    private val selfClient: DefaultSelfClusterClient = DefaultSelfClusterClient(this, UUID.randomUUID())

    init {
        val clusterConnector =
            ClusterConnector(this, serverAddress, packetsPackages, ClusterConnector.ConnectMethod.ONE_NODE)
        selfClient.setNodeConnectedTo(clusterConnector.firstRemoteNode)
        clusterConnector.connectToCluster()
        val node = this.componentManager.getNodeByServerAddress(serverAddress)!! as IRemoteNode
        selfClient.setNodeConnectedTo(node)
        this.getAuthProvider().authenticateOnRemoteNodes(this, listOf(node))
    }

    override fun getSelfComponent(): ISelfClusterClient {
        return this.selfClient
    }

    override fun shutdown(): ICommunicationPromise<Unit> {
        return this.selfClient.getClient().shutdown()
    }
}
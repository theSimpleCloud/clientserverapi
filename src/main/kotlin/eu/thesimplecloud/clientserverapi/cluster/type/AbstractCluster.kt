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

package eu.thesimplecloud.clientserverapi.cluster.type

import eu.thesimplecloud.clientserverapi.cluster.ICluster
import eu.thesimplecloud.clientserverapi.cluster.adapter.IClusterListenerAdapter
import eu.thesimplecloud.clientserverapi.cluster.adapter.impl.DefaultClusterAdapter
import eu.thesimplecloud.clientserverapi.cluster.auth.IClusterAuthProvider
import eu.thesimplecloud.clientserverapi.cluster.component.IClusterComponent
import eu.thesimplecloud.clientserverapi.cluster.component.IRemoteClusterComponent
import eu.thesimplecloud.clientserverapi.cluster.component.node.INode
import eu.thesimplecloud.clientserverapi.cluster.packetsender.IClientsPacketSender
import eu.thesimplecloud.clientserverapi.cluster.packetsender.INodesPacketSender
import eu.thesimplecloud.clientserverapi.cluster.packetsender.impl.DefaultClientsPacketSender
import eu.thesimplecloud.clientserverapi.cluster.packetsender.impl.DefaultNodesPacketSender
import eu.thesimplecloud.clientserverapi.lib.list.manager.ISyncListManager
import eu.thesimplecloud.clientserverapi.lib.list.manager.impl.ClusterSyncListManager
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by IntelliJ IDEA.
 * Date: 30/01/2021
 * Time: 17:55
 * @author Frederick Baier
 */
abstract class AbstractCluster(
    private val version: String,
    private val authProvider: IClusterAuthProvider
) : ICluster, IClusterListenerAdapter {

    private val nodesPacketSender = DefaultNodesPacketSender(this)
    private val clientsPacketSender = DefaultClientsPacketSender(this)

    private val clusterListManager = ClusterSyncListManager(this)

    private val clusterListeners = CopyOnWriteArrayList<IClusterListenerAdapter>()

    protected val remoteComponents: CopyOnWriteArrayList<IRemoteClusterComponent> = CopyOnWriteArrayList()

    init {
        addListener(this)
        addListener(DefaultClusterAdapter())
    }

    override fun getHeadNode(): INode {
        return getNodes().minByOrNull { it.getStartupTime() }!!
    }

    override fun getComponents(): List<IClusterComponent> {
        return this.remoteComponents.union(listOf(getSelfComponent())).toList()
    }

    override fun getAuthProvider(): IClusterAuthProvider {
        return this.authProvider
    }

    override fun getClusterListeners(): List<IClusterListenerAdapter> {
        return this.clusterListeners
    }

    override fun addListener(listener: IClusterListenerAdapter) {
        this.clusterListeners.add(listener)
    }

    override fun removeListener(listener: IClusterListenerAdapter) {
        this.clusterListeners.remove(listener)
    }

    override fun getVersion(): String {
        return version
    }

    override fun getClusterListManager(): ISyncListManager {
        return this.clusterListManager
    }

    override fun getNodesPacketSender(): INodesPacketSender {
        return this.nodesPacketSender
    }

    override fun getClientsPacketSender(): IClientsPacketSender {
        return this.clientsPacketSender
    }

}
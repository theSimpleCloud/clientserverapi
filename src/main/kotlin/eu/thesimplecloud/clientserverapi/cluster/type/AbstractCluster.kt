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
import eu.thesimplecloud.clientserverapi.cluster.component.manager.DefaultComponentManager
import eu.thesimplecloud.clientserverapi.cluster.component.manager.IComponentManager
import eu.thesimplecloud.clientserverapi.cluster.packetsender.IClientsPacketSender
import eu.thesimplecloud.clientserverapi.cluster.packetsender.INodesPacketSender
import eu.thesimplecloud.clientserverapi.cluster.packetsender.impl.DefaultClientsPacketSender
import eu.thesimplecloud.clientserverapi.cluster.packetsender.impl.DefaultNodesPacketSender
import eu.thesimplecloud.clientserverapi.cluster.type.publish.ComponentJoinPublisher
import eu.thesimplecloud.clientserverapi.cluster.type.publish.ComponentLeavePublisher
import eu.thesimplecloud.clientserverapi.lib.list.manager.ISyncListManager
import eu.thesimplecloud.clientserverapi.lib.list.manager.impl.ClusterSyncListManager
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.combineAllPromises
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
) : ICluster {


    protected val componentManager = DefaultComponentManager(this)

    private val nodesPacketSender = DefaultNodesPacketSender(this)
    private val clientsPacketSender = DefaultClientsPacketSender(this)

    private val clusterListManager = ClusterSyncListManager(this)

    private val clusterListeners = CopyOnWriteArrayList<IClusterListenerAdapter>()

    init {
        addListener(DefaultClusterAdapter())
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

    override fun getComponentManager(): IComponentManager {
        return this.componentManager
    }

    fun onComponentJoin(joiningComponent: IRemoteClusterComponent, senderComponent: IClusterComponent): ICommunicationPromise<Unit> {
        val selfComponent = joiningComponent.getCluster().getSelfComponent()
        val selfId = selfComponent.getUniqueId()
        if (joiningComponent.getUniqueId() == selfId)
            throw IllegalStateException("Component may not join itself")
        this.componentManager.addComponents(joiningComponent)
        val promises = this.clusterListeners.map { it.onComponentJoin(joiningComponent) }
        ComponentJoinPublisher(this, joiningComponent, senderComponent).publishComponentJoin()
        return promises.combineAllPromises()
    }

    fun onComponentLeave(leavingComponent: IRemoteClusterComponent, senderComponent: IClusterComponent): ICommunicationPromise<Unit> {
        this.componentManager.removeComponents(leavingComponent)
        val promises = this.clusterListeners.map { it.onComponentLeave(leavingComponent) }

        ComponentLeavePublisher(this, leavingComponent, senderComponent).publishComponentLeave()
        return promises.combineAllPromises()
    }

}
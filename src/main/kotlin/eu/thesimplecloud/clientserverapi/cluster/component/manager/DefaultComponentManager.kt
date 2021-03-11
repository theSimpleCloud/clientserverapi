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

import eu.thesimplecloud.clientserverapi.cluster.ICluster
import eu.thesimplecloud.clientserverapi.cluster.component.IClusterComponent
import eu.thesimplecloud.clientserverapi.cluster.component.IRemoteClusterComponent
import eu.thesimplecloud.clientserverapi.cluster.component.ISelfClusterComponent
import eu.thesimplecloud.clientserverapi.cluster.component.node.INode
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.IPacketSender
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

class DefaultComponentManager(
    private val cluster: ICluster
) : IComponentManager {

    private val remoteComponents = CopyOnWriteArrayList<IRemoteClusterComponent>()

    override fun getHeadNode(): INode {
        return getNodes().minByOrNull { it.getStartupTime() }!!
    }

    override fun getComponentByPacketSender(sender: IPacketSender): IRemoteClusterComponent? {
        return this.remoteComponents.firstOrNull { it.getPacketSender() == sender }
    }

    override fun getComponentByUniqueId(uniqueId: UUID): IClusterComponent? {
        return this.getComponents().firstOrNull { it.getUniqueId() == uniqueId }
    }

    override fun getComponents(): List<IClusterComponent> {
        return remoteComponents.union(listOf(getSelfComponent())).toList()
    }

    override fun getRemoteComponents(): List<IRemoteClusterComponent> {
        return this.remoteComponents
    }

    override fun getSelfComponent(): ISelfClusterComponent {
        return cluster.getSelfComponent()
    }

    fun addComponents(vararg remoteComponents: IRemoteClusterComponent) {
        remoteComponents.forEach { addSingleComponent(it) }
    }

    private fun addSingleComponent(remoteComponent: IRemoteClusterComponent) {
        val componentByUniqueId = getComponentByUniqueId(remoteComponent.getUniqueId())
        require(componentByUniqueId == null) {
            "Component is already registered (self: ${cluster.getSelfComponent()::class.java.name}  component: ${remoteComponent.getUniqueId()})"
        }
        this.remoteComponents.add(remoteComponent)
    }

    fun removeComponents(vararg remoteComponents: IRemoteClusterComponent) {
        this.remoteComponents.removeAll(remoteComponents)
    }
}
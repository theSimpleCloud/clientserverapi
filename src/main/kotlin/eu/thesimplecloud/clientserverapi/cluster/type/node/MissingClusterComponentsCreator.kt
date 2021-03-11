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

import eu.thesimplecloud.clientserverapi.cluster.ICluster
import eu.thesimplecloud.clientserverapi.cluster.component.IRemoteClusterComponent
import eu.thesimplecloud.clientserverapi.cluster.packets.auth.dto.ComponentDTO

class MissingClusterComponentsCreator(
        private val cluster: ICluster,
        private val componentsConnectedTo: List<IRemoteClusterComponent>,
        private val allComponents: List<ComponentDTO>
) {

    fun createMissingComponents(): List<IRemoteClusterComponent> {
        val unconnectedComponents = getAllUnconnectedComponents()
        return unconnectedComponents.map { VirtualClusterComponentCreator(cluster, it).createVirtualComponent() }
    }

    fun getAllUnconnectedComponents(): List<ComponentDTO> {
        val connectedComponentIds = componentsConnectedTo.map { it.getUniqueId() }
        return allComponents.filterNot { connectedComponentIds.contains(it.uniqueId) }
    }

}
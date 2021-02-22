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

package eu.thesimplecloud.clientserverapi.cluster.component.node

import eu.thesimplecloud.clientserverapi.cluster.component.IClusterComponent
import eu.thesimplecloud.clientserverapi.cluster.component.type.ClusterComponentType
import eu.thesimplecloud.clientserverapi.cluster.packets.auth.dto.NodeComponentDTO
import eu.thesimplecloud.clientserverapi.lib.util.Address

/**
 * Created by IntelliJ IDEA.
 * Date: 30/01/2021
 * Time: 16:25
 * @author Frederick Baier
 *
 * A special cluster component that acts a node
 * Nodes are connected to every other node in the cluster (fully connected)
 */
interface INode : IClusterComponent {

    /**
     * Returns the time the node was started
     */
    fun getStartupTime(): Long

    /**
     * Returns the address the nodes server listens on
     */
    fun getServerAddress(): Address

    /**
     * Returns a serializable object with some information about the node
     */
    fun getNodeInfo(): NodeComponentDTO {
        return NodeComponentDTO(getCluster().getVersion(), getServerAddress(), getStartupTime(), getUniqueId())
    }

    override fun getType(): ClusterComponentType {
        return ClusterComponentType.NODE
    }

}
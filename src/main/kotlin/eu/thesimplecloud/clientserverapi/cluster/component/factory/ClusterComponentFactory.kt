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

package eu.thesimplecloud.clientserverapi.cluster.component.factory

import eu.thesimplecloud.clientserverapi.cluster.ICluster
import eu.thesimplecloud.clientserverapi.cluster.component.IRemoteClusterComponent
import eu.thesimplecloud.clientserverapi.cluster.component.client.impl.DefaultRemoteClusterClient
import eu.thesimplecloud.clientserverapi.cluster.component.node.INode
import eu.thesimplecloud.clientserverapi.cluster.component.node.impl.DefaultRemoteNode
import eu.thesimplecloud.clientserverapi.cluster.packets.auth.dto.ClientComponentDTO
import eu.thesimplecloud.clientserverapi.cluster.packets.auth.dto.ComponentDTO
import eu.thesimplecloud.clientserverapi.cluster.packets.auth.dto.NodeComponentDTO
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.IPacketSender

class ClusterComponentFactory {

    companion object {
        fun createComponent(sender: IPacketSender, componentDTO: ComponentDTO): IRemoteClusterComponent {
            val cluster = sender.getCommunicationBootstrap().getCluster()!!
            if (componentDTO is NodeComponentDTO) {
                return createDefaultNode(sender, cluster, componentDTO)
            }
            return createDefaultClient(sender, cluster, componentDTO as ClientComponentDTO)

        }

        private fun createDefaultClient(
            sender: IPacketSender,
            cluster: ICluster,
            componentDTO: ClientComponentDTO
        ): DefaultRemoteClusterClient {
            val nodeConnectedTo = cluster.getComponentManager().getComponentByUniqueId(componentDTO.connectedNodeComponentId)
            if (nodeConnectedTo == null || nodeConnectedTo !is INode) {
                throw IllegalStateException("Node of client ${componentDTO.uniqueId} cannot be found (${componentDTO.connectedNodeComponentId})")
            }
            return DefaultRemoteClusterClient(
                sender,
                cluster,
                componentDTO.uniqueId,
                nodeConnectedTo
            )
        }

        private fun createDefaultNode(
            sender: IPacketSender,
            cluster: ICluster,
            componentDTO: NodeComponentDTO
        ): DefaultRemoteNode {
            return DefaultRemoteNode(
                sender,
                cluster,
                componentDTO.serverAddress,
                componentDTO.uniqueId,
                componentDTO.startupTime
            )
        }
    }



}
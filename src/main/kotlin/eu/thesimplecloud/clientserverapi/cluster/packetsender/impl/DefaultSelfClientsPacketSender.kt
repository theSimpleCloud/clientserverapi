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

package eu.thesimplecloud.clientserverapi.cluster.packetsender.impl

import eu.thesimplecloud.clientserverapi.cluster.ICluster
import eu.thesimplecloud.clientserverapi.cluster.component.node.ISelfNode
import eu.thesimplecloud.clientserverapi.cluster.component.type.ClusterComponentType
import eu.thesimplecloud.clientserverapi.cluster.packetsender.ISelfClientsPacketSender
import eu.thesimplecloud.clientserverapi.lib.bootstrap.ICommunicationBootstrap
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.combineAllPromises
import eu.thesimplecloud.clientserverapi.server.client.clientmanager.IClientManager

/**
 * Created by IntelliJ IDEA.
 * Date: 19/02/2021
 * Time: 15:36
 * @author Frederick Baier
 */
class DefaultSelfClientsPacketSender(
    private val cluster: ICluster
) : ISelfClientsPacketSender {

    private val clientManager: IClientManager

    init {
        val selfNode = this.cluster.getSelfComponent() as ISelfNode
        this.clientManager = selfNode.getServer().getClientManager()
    }

    override fun sendUnitQuery(packet: IPacket, timeout: Long): ICommunicationPromise<Unit> {
        val allSelfClients = clientManager.getClients()
            .filter { it.getProperty<ClusterComponentType>("type") == ClusterComponentType.CLIENT }
        return allSelfClients.map { it.sendUnitQuery(packet, timeout) }.combineAllPromises()
    }

    override fun getCommunicationBootstrap(): ICommunicationBootstrap {
        return this.cluster.getSelfComponent().getCommunicationBootstrap()
    }

}
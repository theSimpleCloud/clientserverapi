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

package eu.thesimplecloud.clientserverapi.cluster.connection

import eu.thesimplecloud.clientserverapi.cluster.ICluster
import eu.thesimplecloud.clientserverapi.cluster.component.client.IRemoteClusterClient
import eu.thesimplecloud.clientserverapi.cluster.component.node.IRemoteNode
import eu.thesimplecloud.clientserverapi.cluster.packets.forward.PacketIOForward
import eu.thesimplecloud.clientserverapi.cluster.type.IClientCluster
import eu.thesimplecloud.clientserverapi.lib.bootstrap.ICommunicationBootstrap
import eu.thesimplecloud.clientserverapi.lib.packet.WrappedPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.AbstractPacketSender
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.IPacketSender
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.util.*

/**
 * Created by IntelliJ IDEA.
 * Date: 17/02/2021
 * Time: 19:15
 * @author Frederick Baier
 *
 * Virtual connections are used to send packets between components that are not directly connected
 * a) from client to client
 * b) from client to another node
 */
class VirtualClusterComponentPacketSender(
    private val cluster: ICluster,
    private val componentUniqueId: UUID
) : AbstractPacketSender() {

    override fun sendPacket(wrappedPacket: WrappedPacket, promise: ICommunicationPromise<Any>) {
        val packetSender = getPacketSenderToSendForwardPacketsTo()
        val fromComponentId = cluster.getSelfComponent().getUniqueId()
        val forwardPromise = packetSender.sendUnitQuery(PacketIOForward(componentUniqueId, fromComponentId, wrappedPacket, this))
        forwardPromise.addCompleteListener { _ ->
            handleForwardComplete(forwardPromise, promise)
        }
    }

    private fun handleForwardComplete(forwardPromise: ICommunicationPromise<Unit>, packetPromise: ICommunicationPromise<Any>) {
        if (!forwardPromise.isSuccess) {
            packetPromise.tryFailure(forwardPromise.cause())
        }
    }

    override fun getCommunicationBootstrap(): ICommunicationBootstrap {
        return cluster.getSelfComponent().getCommunicationBootstrap()
    }

    private fun getPacketSenderToSendForwardPacketsTo(): IPacketSender {
        //If this is a client there is no other connection
        if (this.cluster is IClientCluster) {
            return this.cluster.getSelfComponent().getConnection()
        }


        //If this is not a client than `forComponent` must be a client. Otherwise the connection would not exist.
        val remoteComponent = this.cluster.getComponentManager().getComponentByUniqueId(componentUniqueId)
        remoteComponent as IRemoteClusterClient

        val remoteNode = remoteComponent.getNodeConnectedTo() as IRemoteNode
        return remoteNode.getPacketSender()
    }

    override fun isAuthenticated(): Boolean {
        return true
    }


}
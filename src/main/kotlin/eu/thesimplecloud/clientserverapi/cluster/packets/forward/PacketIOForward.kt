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

package eu.thesimplecloud.clientserverapi.cluster.packets.forward

import eu.thesimplecloud.clientserverapi.cluster.component.IRemoteClusterComponent
import eu.thesimplecloud.clientserverapi.cluster.component.ISelfClusterComponent
import eu.thesimplecloud.clientserverapi.lib.packet.WrappedPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.AbstractPacketSender
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.IPacketSender
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.util.*
import kotlin.NoSuchElementException

/**
 * Created by IntelliJ IDEA.
 * Date: 20/02/2021
 * Time: 13:49
 * @author Frederick Baier
 */
class PacketIOForward : BytePacket {

    constructor() : super()


    constructor(forwardComponentId: UUID, fromComponentId: UUID, wrappedPacket: WrappedPacket, sender: IPacketSender) : super() {
        writeUUID(forwardComponentId)
        writeUUID(fromComponentId)

        sender.getCommunicationBootstrap().getPacketEncoder().encode(sender, wrappedPacket, this.buffer)
    }

    private fun writeUUID(uuid: UUID) {
        val mostSignificantBits = uuid.mostSignificantBits
        val leastSignificantBits = uuid.leastSignificantBits
        this.buffer.writeLong(mostSignificantBits)
        this.buffer.writeLong(leastSignificantBits)
    }

    private fun readUUID(): UUID {
        val mostSignificantBits = this.buffer.readLong()
        val leastSignificantBits = this.buffer.readLong()
        return UUID(mostSignificantBits, leastSignificantBits)
    }

    override suspend fun handle(sender: IPacketSender): ICommunicationPromise<Any> {
        val forwardComponentId = readUUID()
        val fromComponentId = readUUID()

        val wrappedPacket = sender.getCommunicationBootstrap().getPacketDecoder().decode(sender, this.buffer)
        val cluster = sender.getCommunicationBootstrap().getCluster()!!

        val receiverComponent = cluster.getComponentManager().getComponentByUniqueId(forwardComponentId)
                ?: return failure(NoSuchElementException("Cannot find receiver component by id $forwardComponentId (Self: ${cluster.getSelfComponent().getUniqueId()})"))
        val fromComponent = cluster.getComponentManager().getComponentByUniqueId(fromComponentId)
                ?: return failure(NoSuchElementException("Cannot find component to forward from by id $fromComponentId (Self: ${cluster.getSelfComponent().getUniqueId()})"))
        fromComponent as IRemoteClusterComponent

        if (receiverComponent is ISelfClusterComponent) {
            handlePacket(wrappedPacket, fromComponent.getPacketSender())
            return unit()
        }
        receiverComponent as IRemoteClusterComponent
        val receiverPacketSender = receiverComponent.getPacketSender()
        return receiverPacketSender.sendUnitQuery(PacketIOForward(forwardComponentId, fromComponentId, wrappedPacket, receiverPacketSender))
    }

    private fun handlePacket(wrappedPacket: WrappedPacket, sender: IPacketSender) {
        sender as AbstractPacketSender
        sender.incomingPacket(wrappedPacket)
    }
}
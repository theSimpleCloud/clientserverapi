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

package eu.thesimplecloud.clientserverapi.lib.packet

import eu.thesimplecloud.clientserverapi.lib.ByteBufStringHelper
import eu.thesimplecloud.clientserverapi.lib.bootstrap.ICommunicationBootstrap
import eu.thesimplecloud.clientserverapi.lib.debug.DebugMessage
import eu.thesimplecloud.clientserverapi.lib.packet.exception.PacketException
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.packetmanager.IPacketManager
import eu.thesimplecloud.jsonlib.JsonLib
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

class PacketDecoder(private val communicationBootstrap: ICommunicationBootstrap, private val packetManager: IPacketManager) : ByteToMessageDecoder() {


    @Synchronized
    override fun decode(ctx: ChannelHandlerContext, byteBuf: ByteBuf, out: MutableList<Any>) {
        val receivedString = ByteBufStringHelper.nextString(byteBuf)
        val jsonLib = JsonLib.fromJsonString(receivedString)
        val packetData = jsonLib.getObject("data", PacketData::class.java)
                ?: throw PacketException("PacketData is not present.")
        //objectPacket =-1

        val packet = if (packetData.isResponse) {
            val objectPacket = ObjectPacket.getNewEmptyObjectPacket<Any>()
            objectPacket.read(byteBuf, communicationBootstrap)
            objectPacket
        } else {
            val packetClass: Class<out IPacket>? = packetManager.getPacketClassByOppositePacketName(packetData.sentPacketName)
            if (packetClass == null) {
                //clean up bytebuf
                byteBuf.clear()
                throw PacketException("Can't find opposite packet of: ${packetData.sentPacketName}")
            }
            val packet =  packetClass.newInstance()
            packet.read(byteBuf, communicationBootstrap)
            packet
        }
        out.add(WrappedPacket(packetData, packet))
        if (this.communicationBootstrap.getDebugMessageManager().isActive(DebugMessage.PACKET_RECEIVED)) {
            println("Received Packet ${packet::class.java.simpleName} (${packetData.uniqueId})")
        }
    }


}
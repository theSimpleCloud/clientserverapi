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

package eu.thesimplecloud.clientserverapi.lib.packet.codec.impl

import eu.thesimplecloud.clientserverapi.lib.ByteBufStringHelper
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.PacketHeader
import eu.thesimplecloud.clientserverapi.lib.packet.WrappedPacket
import eu.thesimplecloud.clientserverapi.lib.packet.codec.IPacketDecoder
import eu.thesimplecloud.clientserverapi.lib.packet.exception.PacketException
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.jsonlib.JsonLib
import io.netty.buffer.ByteBuf

/**
 * Created by IntelliJ IDEA.
 * Date: 18/02/2021
 * Time: 13:33
 * @author Frederick Baier
 */
class DefaultPacketDecoder: IPacketDecoder {

    override fun decode(connection: IConnection, byteBuf: ByteBuf): WrappedPacket {
        val packetManager = connection.getCommunicationBootstrap().getPacketManager()

        val receivedString = ByteBufStringHelper.nextString(byteBuf)
        val jsonLib = JsonLib.fromJsonString(receivedString)
        val packetData = jsonLib.getObject("header", PacketHeader::class.java)
            ?: throw PacketException("PacketHeader is not present")


        val packet = if (packetData.isResponse) {
            val objectPacket = ObjectPacket.getNewEmptyObjectPacket<Any>()
            objectPacket.read(byteBuf, connection.getCommunicationBootstrap())
            objectPacket
        } else {
            val packetClass: Class<out IPacket>? =
                packetManager.getPacketClassByOppositePacketName(packetData.sentPacketName)
            if (packetClass == null) {
                //clean up bytebuf
                byteBuf.clear()
                throw PacketException("Can't find opposite packet of: ${packetData.sentPacketName}")
            }
            val packet = packetClass.newInstance()
            try {
                packet.read(byteBuf, connection.getCommunicationBootstrap())
            } catch (e: Exception) {
                throw PacketException("An error occurred while reading packet: ${packetClass.name}")
            }
            packet
        }
        return WrappedPacket(packetData, packet)
    }
}
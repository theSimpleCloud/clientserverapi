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
import eu.thesimplecloud.clientserverapi.lib.packet.PacketHeader
import eu.thesimplecloud.clientserverapi.lib.packet.WrappedPacket
import eu.thesimplecloud.clientserverapi.lib.packet.codec.IPacketEncoder
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.IPacketSender
import eu.thesimplecloud.jsonlib.JsonLib
import io.netty.buffer.ByteBuf

/**
 * Created by IntelliJ IDEA.
 * Date: 18/02/2021
 * Time: 13:33
 * @author Frederick Baier
 */
class DefaultPacketEncoder : IPacketEncoder {

    override fun encode(sender: IPacketSender, wrappedPacket: WrappedPacket, byteBuf: ByteBuf) {
        val headerJson = JsonLib.empty()
        val packetHeader = PacketHeader(wrappedPacket.packetHeader.uniqueId, wrappedPacket.packet::class.java.simpleName, wrappedPacket.packetHeader.isResponse)
        headerJson.append("header", packetHeader)
        ByteBufStringHelper.writeString(byteBuf, headerJson.getAsJsonString())
        wrappedPacket.packet.write(byteBuf, sender)
    }


}